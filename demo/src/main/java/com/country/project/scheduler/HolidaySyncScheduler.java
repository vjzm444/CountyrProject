package com.country.project.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.country.project.common.DateValidator;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.repository.CountryRepository;
import com.country.project.service.CountryService;

/**
 * 스케쥴러
 */
@Component
public class HolidaySyncScheduler {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(HolidaySyncScheduler.class);
    
    //서비스
    @Autowired
    private CountryService countryService;

    // 국가 DB정보
    private final CountryRepository countryRepository;

    //생성자
    public HolidaySyncScheduler(CountryRepository _countryRepository) {
        this.countryRepository = _countryRepository;
    }

    //1월 2일 동기화용 스케줄러 
    @Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
    //@Scheduled(fixedRate = 5 * 60 * 1000) // 5분 = 5*60*1000 ms
    //@Scheduled(fixedRate = 60000) // 60,000ms = 1분
    public void syncHolidayLog() {
        logger.info(">>> Holiday Sync START Triggered! Time: {}", java.time.LocalDateTime.now());
        resetCountryHoliday();
        logger.info(">>> Holiday Sync END Triggered! Time: {}", java.time.LocalDateTime.now());
    }


    /**
     * 최근 2년, 국가별로 축일 재설정
     *  - 리팩토링 있음.
     *     ㄴ 메모리에 전부 쌓아놓은 다음, 마지막에 DB insert로 구현하는 방법.
     */
    public void resetCountryHoliday() {

        //최근 2년
        String[] years = DateValidator.recentYear(2);
        
        // CountryEntity 전부 조회해서 Map으로 캐싱 (DB hit 최소화)
        Map<String, CountryEntity> countryMap = countryRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
                c -> c.countryCode,   // key
                c -> c                // value
        ));

        //List<String> logMessages = new ArrayList<>();
        List<String> warninglogMessages = new ArrayList<>();

        //모든 국가코드만큼 반복
        for (String countryCode : countryMap.keySet()) {
            //logger.info("Country ReSetting Start: " + countryCode);

            //최근 2년치만
            for (String year : years) {

                List<PublicHolidayEntity> result = countryService.upsertPublicHolidays(year, countryCode);

                //비엇으면 문제.
                if(result.isEmpty()){
                    warninglogMessages.add(String.format("[ERROR] resetCountryHoliday Scheduler count is null :: countryCode:%s, year:%s",countryCode, year));
                }else{
                    //logMessages.add(String.format("[SUCCESS] upsertPublicHolidays result :: countryCode:%s, year:%s,  size:%d",countryCode, year, result.size()));
                }
            }
        }

        // 정상 로그 출력
        //if (!logMessages.isEmpty()) {
            //logger.info("\n" + String.join("\n", logMessages));
        //}

        // WARNING 로그 그 다음 출력
        if (!warninglogMessages.isEmpty()) {
            logger.warn("\n" + String.join("\n", warninglogMessages));
        }
    }
        
}
