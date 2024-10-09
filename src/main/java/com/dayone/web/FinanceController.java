package com.dayone.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    //배당금 조회 API
    @GetMapping("/devidend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName){
        return null;
    }
}
