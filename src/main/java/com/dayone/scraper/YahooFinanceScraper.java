package com.dayone.scraper;

import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d&filter=history";
    //static 안쓰면 인스턴스 생성될때마다 각각 갖게됨. static쓰면 static Area영역에 있는 공통 URL을 참조
    //private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s/";
    private static final long START_TIME = 86400; //60*80*24

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; //현재시간가져온것 밀리세컨드로 /1000 s 단위로 바꿈
            String url = String.format(STATICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            Document document = connection.get();

            //Elements tableEle = document.select("div.table-container > table.table.yf-ewueuo.noDl > tbody > tr > td > span");
            //Elements tableEle = document.select("div.table-container > table.table.yf-ewueuo.noDl > tbody > tr > td");
            Elements tableEle = document.select("div.table-container > table.table.yf-ewueuo.noDl > tbody");

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tableEle.select("tr")) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value ->" + splits[0]);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
            }
            scrapResult.setDividends(dividends);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        try {
            String url = String.format(SUMMARY_URL, ticker, ticker);

            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            Document document = connection.get();
            //Element titleEle = document.getElementsByTag("h1").get(0);
            //Elements titleEle = document.select("main.layoutContainer yf-cfn520> section.container yf-sielht > div.name yf-sielht");
            //Elements titleEle = document.select("main.layoutContainer.yf-cfn520");
            //Elements titleEle = document.select("section.container.yf-1s1umie>div.top.yf-1s1umie");
            Elements titleEle = document.select("section.container.yf-xxbei9>h1");


            //System.out.println(titleEle.text());
            //회사명 깔끔하게 문자 후처리
            String title = titleEle.text().split("\\(")[0].trim();

            //trim앞뒤 공백 제거

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
