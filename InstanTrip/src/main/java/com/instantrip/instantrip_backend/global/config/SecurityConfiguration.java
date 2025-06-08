package com.instantrip.instantrip_backend.global.config;

import com.instantrip.instantrip_backend.domain.cognito.CognitoLogoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CognitoLogoutHandler cognitoLogoutHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/css/**", "/js/**", "/images/**", "/api/public/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized");
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(authorizationRequestResolver())
                        )
                        .defaultSuccessUrl("https://instantrip.ajb.kr", true)
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(cognitoLogoutHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        resolver.setAuthorizationRequestCustomizer(
                customizer -> customizer.additionalParameters(params -> {
                    params.put("prompt", "login");
                })
        );

        return resolver;
    }
}
