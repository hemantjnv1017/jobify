package com.jobify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobifyApplication {

    private static final Logger log = LoggerFactory.getLogger(JobifyApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(JobifyApplication.class, args);
        log.info("Jobify started");
    }
}
