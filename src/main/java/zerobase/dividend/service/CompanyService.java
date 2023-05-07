package zerobase.dividend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.AllArgsConstructor;
import zerobase.dividend.exception.impl.AlreadyExistCompanyException;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.DividendRepository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;
import zerobase.dividend.scraper.YahooFinanceScraper;

@Service
@AllArgsConstructor
public class CompanyService {
	
	private final Trie<String, String> trie;
	
	private final YahooFinanceScraper yahooFinanceScraper;
	
	private final CompanyRepository companyRepository;
	
	private final DividendRepository dividendRepository;
	
	public Company save(String ticker) {
		boolean exists = this.companyRepository.existsByTicker(ticker);
		if (exists) {
			throw new AlreadyExistCompanyException();
		}
		return this.storeCompanyAndDividend(ticker);
	}
	
	public Page<CompanyEntity> getAllCompany(Pageable pageable){
		return this.companyRepository.findAll(pageable);
	}
	
	private Company storeCompanyAndDividend(String ticker) {
		//ticker 를 기준으로 회사를 스크랩핑
		Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
		if(ObjectUtils.isEmpty(company)) {
			throw new NoCompanyException();
		}
		// 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크랩핑
		ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);
		
		//스크랩핑 결과
		CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
		List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
														.map(e -> new DividendEntity(companyEntity.getId(), e))
														.collect(Collectors.toList());
		this.dividendRepository.saveAll(dividendEntities);
		return company;
	}

	public void addAutoCompleteKeyword(String keyword) {
		this.trie.put(keyword, null);
	}
	
	public void deleteAutocompleteKeyword(String keyword) {
		this.trie.remove(keyword);
	}

	public List<String> getCompanyNamesByKeyword(String keyword){
		Pageable limit = PageRequest.of(0, 10);
		Page<CompanyEntity> companyEntities = this.companyRepository
				.findByNameStartingWithIgnoreCase(keyword, limit);
		return companyEntities.stream()
								.map(e -> e.getName())
								.collect(Collectors.toList());
	}

	public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
