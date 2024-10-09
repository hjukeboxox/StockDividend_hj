package com.dayone.web;

import com.dayone.model.Company;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company") //경로에 공통되는 부분은 빼줌
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    //배당금 자동완성 API
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        //var result = this.companyService.autocomplete(keyword);
        var result = this.companyService.getCompanyNameByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    //회사 리스트 조회 API
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {  //페이져블 넘겨주기..페이지 기능 지원 숫자 고정 final
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }


    /**
     * 회사 및 배당금 정보 추가
     *
     * @param request
     * @return
     */
    //배당금 데이터 저장, 삭제 API
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
