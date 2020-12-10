package com.mycompany.problemdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mycompany.problemdemo")
public class ProblemDemoAppMain {

    public static void main(String... args) {
        SpringApplication.run(ProblemDemoAppMain.class, args);
    }

}
