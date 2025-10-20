package hyundai_4th.car_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/qna/**").permitAll()  // QnA API - 인증 없이 접근 가능
                .antMatchers("/cd.html").permitAll()      // cd.html - 인증 없이 접근 가능
                .antMatchers("/cs.html").permitAll()      // cs.html - 인증 없이 접근 가능
                .antMatchers("/*.html").permitAll()       // 모든 HTML 파일
                .antMatchers("/css/**", "/js/**", "/images/**").permitAll()  // 정적 리소스
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .permitAll()
            .and()
            .logout()
                .permitAll();

        return http.build();
    }
}
