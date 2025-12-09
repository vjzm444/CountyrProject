package com.country.project.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.country.project.common.DateValidator;
import com.country.project.initializer.DataInitializer;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.model.jsonModel.PublicHoliday;
import com.country.project.repository.CountryRepository;
import com.country.project.repository.HolidayRepository;

/**
 * 스케쥴러
 */
@Component
public class HolidaySyncScheduler {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(HolidaySyncScheduler.class);
    
    private final RestTemplate restTemplate;
    
    // 휴일 DB정보
    private final HolidayRepository holidayRepository;
    // 국가 DB정보
    private final CountryRepository countryRepository;

    //생성자
    public HolidaySyncScheduler(HolidayRepository _holidayRepository, CountryRepository _countryRepository) {
        this.holidayRepository = _holidayRepository;
        this.countryRepository = _countryRepository;

        this.restTemplate = new RestTemplate();
    }


    //@Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul") //1월 2일 동기화용 스케줄러 
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분 = 5*60*1000 ms
    //@Scheduled(fixedRate = 60000) // 60,000ms = 1분
    public void syncHolidayLog() {
        logger.info(">>> Holiday Sync Triggered! Time: {}", java.time.LocalDateTime.now());
    }


    /**
     * 국가별로 축일을 설정(작업중)
     
    public void addCountryHoliday(String[] codes) {

        //최근 5년
        String[] years = DateValidator.recentYear();
        List<PublicHolidayEntity> allEntities = new ArrayList<>();

        // CountryEntity 전부 조회해서 Map으로 캐싱 (DB hit 최소화)
        Map<String, CountryEntity> countryMap = countryRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
                c -> c.countryCode,   // key
                c -> c                // value
        ));

        for (String code : codes) {
            logger.info("country start ::: " + code);

            for (String year : years) {
                // API 요청 URL
                
                String url = String.format("%s/%s/%s", DataInitializer.NAGER_HOLIDAY_URL, year, code);
                //logger.info("url ::: " + url);

                PublicHoliday[] holidays = restTemplate.getForObject(url, PublicHoliday[].class);

                if (holidays == null || holidays.length == 0) {
                    continue;
                }

                // 3PublicHoliday → Entity 매핑
                for (PublicHoliday h : holidays) {
                    CountryEntity country = countryMap.get(h.getCountryCode()); // country 매핑

                    PublicHolidayEntity e = PublicHolidayEntity.builder()
                            .date(h.getDate())
                            .holidayYear(year)
                            .country(country)
                            .localName(h.getLocalName())
                            .name(h.getName())
                            .fixed(h.getFixed())
                            .global(h.getGlobal())
                            .counties(h.getCounties())
                            .launchYear(h.getLaunchYear())
                            // .types(h.getTypes()) // 필요하면 추가
                            .build();

                    allEntities.add(e);
                }

            }
        }

        // 모든 반복이 끝난 뒤 DB에 한 번에 저장
        holidayRepository.saveAll(allEntities);

        logger.info("Saved holidays: " + allEntities.size());
    }
        */
}
