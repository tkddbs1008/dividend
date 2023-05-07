package zerobase.dividend.scheduler;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constants.CacheKey;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.DividendRepository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;
import zerobase.dividend.scraper.YahooFinanceScraper;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {
		
	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;
	
	private final YahooFinanceScraper yahooFinanceScraper;
	
	@CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
//	@Scheduled(cron = "${scheduler.scrap.yahoo}")
	public void yahooFinanceScheduling() {
		log.info("scraping scheduler is started");
		//저장된 회사 목록을 조회
		List<CompanyEntity> companies = this.companyRepository.findAll();
		//회사마다 배당금 정보를 새로 스크랩핑
		for (var company: companies) {
			log.info("scraping scheduler is started -> " + company.getName());
			ScrapedResult scrapedResult = this.yahooFinanceScraper.
												scrap(new Company(company.getName(), 
																  company.getTicker()));
			
			scrapedResult.getDividends().stream()
										.map(e -> new DividendEntity(company.getId(), e))
										.forEach(e -> {
											boolean exists = this.dividendRepository
													.existsByCompanyIdAndDate(
															e.getCompanyId(), e.getDate());
											if(!exists) {
												this.dividendRepository.save(e);
												log.info("insert new dividend -> " + e.toString());
											}
										});
		}
		//연속적으로 스크래핑한 배당금 정보 중 데이더베이스에 없는 값은 저장
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} //3 seconds
	}
}
