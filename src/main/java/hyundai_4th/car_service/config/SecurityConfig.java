package hyundai_4th.car_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ✅ LocalDateTime, Instant 등 직렬화 문제 해결
    @PostConstruct
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ✅ 모든 보안 정책 통합
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ API 요청은 CSRF 제외 (프론트 AJAX/FETCH 허용)
                .csrf().ignoringAntMatchers("/api/**").and()

                // ✅ 접근 권한 설정
                .authorizeRequests()
                .antMatchers(
                        // 회원가입 및 사용자 API
                        "/api/v1/users/**",
                        // QnA API
                        "/api/qna/**",
                        // 렌탈 관련 페이지
                        "/rental", "/rental.html", "/static/**",
                        // 정적 리소스 및 HTML
                        "/css/**", "/js/**", "/images/**", "/*.html",
                        // 학습용 취약점 테스트 페이지
                        "/vulnerable/**",
                        "/cd.html", "/cs.html",
                        // 개발 편의용: 전체 API 공개 (운영 시 제한 권장)
                        "/api/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                // ✅ 로그인/로그아웃 허용
                .formLogin().permitAll()
                .and()
                .logout().permitAll();

        return http.build();
    }

    // ✅ 비밀번호 암호화 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
