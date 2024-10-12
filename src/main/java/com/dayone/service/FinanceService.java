package com.dayone.service;

import com.dayone.exception.impl.NoCompanyException;
import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    //캐싱시 고려사항
    //요청이 얼마나 빈번하게 들어오는지...?
    //데이터의 내용이 자주변경되는가? 년배당정보 추가정도라 많지는 않을것
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company ->" + companyName);
        // 캐쉬가 사용되지않으면 로직이 사용되서 로그가 찍힘.
        //캐쉬가 사용되면 로직을 사용안해서 로그가 안찍힘..


        //1. 회사명을 기준으로 회사 정보를 조회. 주식정보는 특정정보에 대한 검색조회가 몰리는편.. 주요 주식같은경우는... 많은
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());
//값이 없는경우 예외발생시켜줌 .orElseThrow... 널처리와는 다름. 옵셔널이 벗겨진 알맹이를 뱉음.

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        //3. 결과 조회 후 반환

        //리스트 가공작업
        //방법1.
/*
        List<Dividend> dividends = new ArrayList<>();
        for (var entity : dividendEntities) {
            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());
        }
*/
        //방법2.
        List<Dividend> dividends =dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                        .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName())
                 ,dividends);
    }
}
