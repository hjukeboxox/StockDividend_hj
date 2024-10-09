package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    //빈으로 선언해서 사용할거라 @Component 붙임
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("alread exists ticker ->" + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑한다
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        //널처리
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker ->" + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑

        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);


        //스크래핑 결과
        //companyId를 얻은다음 그것으로 다시 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
//내용물을 다른 값으로 맵핑할떄 사용 .map , new DividendEntity 를 쓸수있는건 엔티티에다가 생성자를 생성해줬기때문에 가능
//.filter는 특정제외.. 특정것만 선별 .sort는 내용물 정렬

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }
}
