package com.dayone.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
//@AllArgsConstructor 는 @Data에 들어가있지않아서 따로 추가해줌..
@Data
@AllArgsConstructor
public class ScrapedResult {
    private Company company;
    //한 회사는 다년도 배당금 정보가짐
    private List<Dividend> dividends;

    public ScrapedResult() {
        this.dividends = new ArrayList<>();
    }
}
