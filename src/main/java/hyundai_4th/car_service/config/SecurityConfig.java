package hyundai_4th.car_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/favicon.ico", "/webjars/**",
                "/css/**", "/js/**", "/images/**", "/static/**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()                 // REST 개발 편의
                .authorizeRequests()
                .antMatchers("/", "/index.html", "/api/**").permitAll() // 여기까지는 모두 허용
                .anyRequest().permitAll()
                .and()
                .httpBasic().disable()
                .formLogin().disable();       // 기본 로그인 폼 비활성화
    }
}
