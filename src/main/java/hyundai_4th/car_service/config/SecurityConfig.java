package hyundai_4th.car_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    public SecurityConfig(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @PostConstruct
    public void setup() { objectMapper.registerModule(new JavaTimeModule()); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: API는 제외
                .csrf().ignoringAntMatchers("/api/**").and()
                // CORS 허용
                .cors().and()
                // 권한 매칭
                .authorizeRequests()
                // ★ 지점 검색 허용(403 방지)
                .antMatchers(HttpMethod.GET, "/api/v1/locations/**").permitAll()
                // 로그인/회원 관련 공개
                .antMatchers("/api/v1/login", "/api/v1/users/**").permitAll()
                // 정적 리소스/HTML
                .antMatchers("/css/**","/js/**","/images/**","/*.html","/static/**").permitAll()
                // 개발 단계: 다른 API도 열어둘 거면 아래 줄 유지, 운영 전엔 좁히세요
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // 폼/로그아웃 비활성화(우리는 REST API 사용)
                .formLogin().disable()
                .logout().disable();

        return http.build();
    }

    // 선택: 동일 출처면 없어도 되지만, 분리 프런트 대비용 CORS 기본값
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration().applyPermitDefaultValues();
        cfg.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}