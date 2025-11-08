package com.techcourse.api.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("선착순 이벤트 시스템 제작 - 수강신청 시스템")
                .description("""
                            수강신청 시스템의 REST API 문서입니다. 동시성 문제 학습을 위한 서비스입니다.<br>
                            원활한 서비스를 위해 데이터베이스 연결이 필요합니다. (https://www.erdcloud.com/d/sCEJbNPZh8EZSNp7P)
                            """)
                .version("v1.0.0"));
    }
}
