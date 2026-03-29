package com.jobengine.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // @Configuration + @EnableAutoConfiguration + @ComponentScan
public class ProducerApplication {

    public static void main(String[] args) {
        // Bootstraps the Spring context, starts embedded Tomcat, registers all beans
        SpringApplication.run(ProducerApplication.class, args);
    }
}
