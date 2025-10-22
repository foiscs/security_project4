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

    // ✅ Instant, LocalDateTime 직렬화 지원
    @PostConstruct
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ✅ API 보안 정책
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 예외 설정 (API 요청 허용)
                .csrf(csrf -> csrf.ignoringAntMatchers("/api/**"))

                // ✅ 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/api/v1/login",       // 로그인
                                "/api/v1/users/**",    // 회원가입/조회
                                "/api/qna/**",
                                "/rental", "/rental.html", "/static/**",
                                "/css/**", "/js/**", "/images/**", "/*.html",
                                "/vulnerable/**",
                                "/cd.html", "/cs.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ 기본 폼/로그아웃 비활성화 (API 로그인 사용)
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    // ✅ 비밀번호 암호화 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
