package com.akshay.ecommerce.commonUsage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;


@Configuration
public class enableAuditing{
    @Bean
    public AuditorAware<String> auditorAware(){
        return ()-> Optional.of("user.dummy");
    }
}



//enablejpaauditing use so annotations like datecreated and lastmodified will work