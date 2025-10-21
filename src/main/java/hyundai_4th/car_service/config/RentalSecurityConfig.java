package hyundai_4th.car_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class RentalSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringAntMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/", "/rental", "/rental.html", "/static/**", "/api/**").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
