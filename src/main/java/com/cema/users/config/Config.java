package com.cema.users.config;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class Config {
    public static final Contact CONTACT = new Contact("Proyecto Cema", "https://cema.atlassian.net/jira/your-work", "merlinsn@gmail.com");

    public  ApiInfo apiInfo;

    public Config(BuildProperties buildProperties) {
        this.apiInfo = new ApiInfo("Cema " + buildProperties.getName() + " API Documentation", "Swagger documentation of the " + buildProperties.getName() + " API", buildProperties.getVersion(), "urn:tos",
                CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<>());
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.cema"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo);
    }
}
