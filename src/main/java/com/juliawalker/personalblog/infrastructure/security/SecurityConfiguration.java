package com.juliawalker.personalblog.infrastructure.security;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AdminProperties.class)
public class SecurityConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    private static final int REMEMBER_ME_SECONDS = 14 * 24 * 60 * 60;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminProperties adminProperties) {
        return new InMemoryUserDetailsManager(User.withUsername(adminProperties.username())
                .password(adminProperties.passwordHash())
                .roles("ADMIN")
                .build());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AdminProperties adminProperties,
            UserDetailsService userDetailsService) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin")
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me"))
                .rememberMe(rememberMe -> rememberMe
                        .key(rememberMeKey(adminProperties))
                        .tokenValiditySeconds(REMEMBER_ME_SECONDS)
                        .userDetailsService(userDetailsService))
                .httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }

    // works without a key, but remember-me cookies become invalid after every restart
    private static String rememberMeKey(AdminProperties adminProperties) {
        if (adminProperties.hasRememberMeKey()) {
            return adminProperties.rememberMeKey();
        }
        logger.warn("REMEMBER_ME_KEY is not set; remember-me logins will not survive a restart");
        return UUID.randomUUID().toString();
    }

}
