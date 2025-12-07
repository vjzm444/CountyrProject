package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.country.project.DemoApplication;
import com.country.project.initializer.DataInitializer;


@SpringBootTest(classes = DemoApplication.class)
public class MyServiceTest {

    @Test
    void testSomething() {
        System.out.println("Test start........");
        
        String[] result = DataInitializer.RecentYear();
        System.out.println("RecentYear result: " + String.join(", ", result));

        assertTrue(true, "myservice test complate: Success");
        //테스트 실패
        //assertTrue(false, "myservice test complate: Fail");
    }

    
}