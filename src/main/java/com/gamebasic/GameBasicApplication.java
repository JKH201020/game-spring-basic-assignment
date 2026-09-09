package com.gamebasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// TODO (Lv 11): @EnableJpaAuditing Auditing 인프라 활성화함
@EnableJpaAuditing
@SpringBootApplication
public class GameBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameBasicApplication.class, args);
    }

}
