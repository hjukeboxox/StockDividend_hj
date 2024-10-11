package com.dayone;

import com.dayone.scraper.NaverFinanceScraper;
import com.dayone.scraper.Scraper;
import com.dayone.scraper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        //웹사이트에 요청보내서 html문서받아오기

/*        try {
            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history/?frequency=1mo&period1=99153000&period2=1727864586&filter=history")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            Document document = connection.get();

            //Elements rows = document.select("div.table-container > table.table.yf-ewueuo.noDl > tbody > tr > td > span");
            //Elements rows = document.select("div.table-container > table.table.yf-ewueuo.noDl > tbody > tr > td");
            Elements rows = document.select("div.table-container > table.table.yf-ewueuo.noDl > tbody");

            for (Element e : rows.select("tr")) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                System.out.println(year + "/" + month + "/" + day + "->" + dividend);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }*/

//        String s = "Hello my name is %s";
//
//        String[] names = {"GREEN", "RED", "BANANA"};
//
//        for (String name : names) {
//            System.out.println(String.format(s,name));
//        }

        //1970년부터 발생한 시간을 밀리세컨드로 가져온값
        // System.out.println(System.currentTimeMillis());

        /*
        스크랩1테스트
        YahooFinanceScraper scraper = new YahooFinanceScraper();
        var result = scraper.scrap(Company.builder().ticker("O").build());

        System.out.println(result);
*/

        //스크랩2테스트
//        YahooFinanceScraper scraper = new YahooFinanceScraper();
//        var result = scraper.scrapCompanyByTicker("MMM");
//        System.out.println(result);

        //인터페이스로 코드 분리

        //Scraper scraper1 = new YahooFinanceScraper();
        //Scraper scraper2 = new NaverFinanceScraper();


//스레드 슬립 테스트
       /*
        for (int i = 0; i < 10; i++) {
            System.out.println("HELLO ->" + i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
*/

        //System.out.println("Main->"+Thread.currentThread().getName());


    }
}
