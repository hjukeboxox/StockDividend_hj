package com.dayone;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);

        //웹사이트에 요청보내서 html문서받아오기

        try {
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
        }


    }
}
