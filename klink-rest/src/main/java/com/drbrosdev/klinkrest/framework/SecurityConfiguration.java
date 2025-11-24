package com.drbrosdev.klinkrest.framework;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class SecurityConfiguration {

    // NOTE: CORS is enforced by default for security
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:3000", "https://klink.drbros.dev")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
//            }
//        };
//    }

}
