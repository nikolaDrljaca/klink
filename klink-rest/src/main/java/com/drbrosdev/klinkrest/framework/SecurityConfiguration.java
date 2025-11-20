package com.drbrosdev.klinkrest.framework;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

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


    @Component
    @RequiredArgsConstructor
    public static class KlinkKeyCheckFilter extends OncePerRequestFilter {

        private static final Set<String> UNPROTECTED = Set.of(
                "/klink",
                "/klink/query-existing"
        );

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
            var path = request.getRequestURI();
            return UNPROTECTED.stream()
                    .anyMatch(it -> Objects.equals(it, path));
        }
    }
}
