package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    //아래 레파지토리 초기화 시켜주기위해 @AllArgsConstructor붙여줌
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;


//    @Scheduled(cron = "0/5 * * * * *")
//    public void test() {
//        System.out.println("now ->" + System.currentTimeMillis());
//    }

/*
    DB Index
            cardinality: 중복데이터 많울수록면 cardinality 낮다
            성별 -> cardinality 낮다 -> 인덱스걸어도 효과 없음
            주민등록번호 -> cardinality 높다 -> 인덱스 걸면 효과 좋음
            선택도 및 호출율 등도 인덱스걸때 고려사항.
    */

    //일정 주기마다 수행
    //@Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started->" + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());
            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값 저장
            scrapedResult.getDividends().stream()
                    //디비든 모델을 디비든 엔티티로 맵핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    //엘리먼트를 하나씩 디비든 레파지토리에 삽입(존재하지않느경우만)
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });
            //회사의 갯수만큼.. 서버에 api요청날리게되서 부하가 가게됨. -> 포문을 한번 돌떄마다 스레드 슬립을 걸어서 일시정지 시켜주기.
            // FOR 문안에서 설정하기!
            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                //실행중인 스레드를 잠시 멈추게 할떄 사용
                Thread.sleep(3000); //3s를 의미
            } catch (InterruptedException e) {
                //스레드에 문제가 발생할 가능성있을떄 예외처리
                Thread.currentThread().interrupt();
            }
        }
/*
        sleep VS wait는 다름.
        wait는 notify나 notifyall매소드 호출할때까지 자동으로 안깸
*/

    }

    /**
     *스케쥴이 2개 동시에 동작할 경우 스레드풀 이용 -> 일꾼(여러스레트)를 늘리거나 줄이는 유지/관리
     * 스레드풀의 적정사이즈: CPU처리가 많은경우. I/O 작업이 많은경우
     * 스케쥴로 2개이상작업돌릴때 한작업 수행되는동안 다른작업이 동작안함.. -> 스케쥴러가 한개의 동작을 하게되서 그럼 디폴트가 그럼
     */

/*
    @Scheduled(fixedDelay = 1000)
    public void test1() throws InterruptedException {
        Thread.sleep(10000); //10초간 일시정지
        System.out.println(Thread.currentThread().getName() +"테스트1:" + LocalDateTime.now());
    }
    @Scheduled(fixedDelay = 1000)
    public void test2() {
        System.out.println(Thread.currentThread().getName() +"테스트2:" + LocalDateTime.now());
    }
*/


}
