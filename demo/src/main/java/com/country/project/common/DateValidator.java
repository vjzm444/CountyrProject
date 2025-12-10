package com.country.project.common;

import java.time.Year;
import java.util.Arrays;

/**
 * 도우미 함수
 */
public class DateValidator {

    // 인스턴스 생성 방지
    private DateValidator() {}

    //년도 체크
    public static Boolean validateYear(String year) {
        //최근 5년에 해당되는지?
        String[] recentYears = recentYear();
        boolean isValid = Arrays.asList(recentYears).contains(year);

        if (!isValid) {
            return false;
        }
        return true;
    }

    //월은 존재하지않으면 true로처리
    public static Boolean validateMonth(Integer month) {
        // month가 null이면 valid 처리
        if (month == null) {
            return true;
        }

        // 1~12 범위면 true, 아니면 false
        return month >= 1 && month <= 12;
    }

    //년, 월 체크
    public static Boolean validateYearAndMonth(String year, Integer month) {
        Boolean r1 = validateYear(year);
        Boolean r2 = validateMonth(month);
        
        // 둘 중 하나라도 false면 전체 false
        return r1 && r2;
    }

    // 현재년도 기준 최근 5년을 리턴
    public static String[] recentYear() {
        // null이면 내부에서 기본값 5 처리
        return recentYear(null);
    }

    // 현재년도 기준 최근 5년을 리턴
    public static String[] recentYear(Integer numberOfYears) {
        int currentYear = Year.now().getValue(); // 현재 연도 2025
    
        // 최근 N년, 기본값 5
        int n = (numberOfYears != null) ? numberOfYears : 5;

        String[] years = new String[n];

        int startYear = currentYear - (n - 1); // 5년이면 2021~2025
        for (int i = 0; i < n; i++) {
            years[i] = String.valueOf(startYear + i);
        }
        return years;
    }
}