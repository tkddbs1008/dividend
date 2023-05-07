package zerobase.dividend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.Dividend;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constants.CacheKey;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.DividendRepository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;

@Service
@AllArgsConstructor
public class FinanceService {
	
	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;
	
	@Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
	public ScrapedResult getDividendByCompanyName(String companyName) {
		//회사명을 기준으로 회사 정보를 조회
		CompanyEntity company = this.companyRepository.findByName(companyName)
											.orElseThrow(() -> 
											new NoCompanyException());
		//조회딘 회사 ID 로 배당금 정보 조회
		List<DividendEntity> dividendEntities = 
				this.dividendRepository.findAllByCompanyId(company.getId());
		//결과 조합 후 반환
		List<Dividend> dividends = dividendEntities.stream()
				.map(e -> new Dividend(e.getDate(), e.getDividend())
				).collect(Collectors.toList());
		
		return new ScrapedResult(new Company(company.getTicker(), 
									company.getName()), 
									dividends
								);
	}
}
