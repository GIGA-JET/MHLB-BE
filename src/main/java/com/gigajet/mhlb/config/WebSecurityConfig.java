package com.gigajet.mhlb.config;


import com.gigajet.mhlb.security.filter.CustomAccessDeniedHandler;
import com.gigajet.mhlb.security.filter.CustomAuthenticationEntryPoint;
import com.gigajet.mhlb.security.jwt.JwtAuthFilter;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
//                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .antMatchers("/swagger-ui/**", "/api-docs/json/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();
        http.formLogin().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/api/users/**").permitAll()
                .antMatchers("/stomp/**").permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated();

        http.cors();

        http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*");

        config.addExposedHeader(JwtUtil.AUTHORIZATION_HEADER);

//        config.addAllowedMethod("*");
        config.addAllowedMethod("OPTIONS");

//        config.addAllowedHeader("*");
        config.addAllowedHeader("Access-Control-Request-Headers");

        config.setMaxAge(600L);

        config.setAllowCredentials(true);

        config.validateAllowCredentials();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}