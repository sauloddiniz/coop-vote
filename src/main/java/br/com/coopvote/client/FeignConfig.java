package br.com.coopvote.client;

import org.springframework.context.annotation.Bean;


public class FeignConfig {

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
