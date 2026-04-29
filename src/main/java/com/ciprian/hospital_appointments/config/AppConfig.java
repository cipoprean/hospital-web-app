package com.ciprian.hospital_appointments.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class AppConfig {

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    @Bean
    public ModelMapper modelMapperConfig() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        return modelMapper;
    }
}
