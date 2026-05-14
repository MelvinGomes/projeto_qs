package trabalho_qs.melvini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/cadastro", "/css/**", "/js/**").permitAll() // Área VIP liberada pra quem tá chegando
                .anyRequest().authenticated() // O resto só entra logado
            )
            .formLogin(form -> form
                .loginPage("/login") // Nossa tela de login customizada
                .defaultSuccessUrl("/livros", true) // Pra onde vai depois de logar
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
            
        return http.build();
    }

    // O codificador de senhas (criptografia pesada)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}