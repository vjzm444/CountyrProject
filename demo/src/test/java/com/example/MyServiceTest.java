package com.example;


import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.country.project.DemoApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 테스트 케이스
 *  - 조회 => O
 *  - 삭제 
 *  - Upsert
 */
@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") // test용 프로파일 사용
public class MyServiceTest {

    @Autowired
    private MockMvc mockMvc;


    /**
     * 테스트 시작할때마다 공통처리
     */
    @BeforeEach
    void before() {
        System.out.println("Test Before");
    }

    /**
     * 테스트
    @Test
    void testSomething() {
        System.out.println("Test start");
        // 테스트 통과 표시용
        assertTrue(true, "test complate: Success");
        //테스트 실패
        //assertTrue(false, "test complate: Fail");
    }
    */

    /**
     * 국가 공휴일조회
     *  - 성공 케이스
     * 
     *  jwt체크는 하지않는다
     */
    @Test
    @WithMockUser(username="testuser", roles={"USER"})
    void testGetHolidays_Success() throws Exception {

        try {
            
            // 변수로 처리
            String year = "2025";
            String country = "KR";
            String month = "5";
            int pageNumber = 0;
            
            MvcResult result = mockMvc.perform(get("/holidays/{year}/{country}", year, country)
            //.param("month", month.toString())
            .param("page", String.valueOf(pageNumber)))
            .andExpect(status().isOk())
            .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();

            // JSON을 JsonNode로 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // content 배열 가져오기
            JsonNode contentArray = root.path("content");
            System.out.println("Total holiday count: " + contentArray.size());

            // 각 공휴일 이름/날짜 출력
            for (JsonNode holiday : contentArray) {
                System.out.println("Holiday: " + holiday.path("name").asText()
                    + ", Date: " + holiday.path("date").asText());
            }

            assertTrue(true, "test complate: Success");
        } catch (Exception e) {
            //테스트 실패
            assertTrue(false, "test complate: Fail. error: "+ e.getMessage());
        }
    }

    /**
     * 테스트 종료할때마다 공통처리
     */
    @AfterEach
    void after() {
        System.out.println("Test After");
    }
}