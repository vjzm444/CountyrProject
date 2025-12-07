package com.example;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.country.project.DemoApplication;


@SpringBootTest(classes = DemoApplication.class)
public class YearServiceTest {

    @BeforeEach
    void before() {
        System.out.println("Test Before");
    }

    @Test
    void testSomething() {
        System.out.println("Test start");
         // 테스트 통과 표시용
        assertTrue(true, "test complate: Success");
        //테스트 실패
        //assertTrue(false, "test complate: Fail");
    }

    @AfterEach
    void after() {
        System.out.println("Test After");
    }

    
}