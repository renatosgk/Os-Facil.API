package com.oracle.OSfacil.infra.seguranca;

import com.oracle.OSfacil.service.AutenticacaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracaoSeguranca {

    private final FiltroTokenAcesso filtroTokenAcesso;
    private final AutenticacaoService autenticacaoService;

    public ConfiguracaoSeguranca(FiltroTokenAcesso filtroTokenAcesso,
                                 AutenticacaoService autenticacaoService) {
        this.filtroTokenAcesso = filtroTokenAcesso;
        this.autenticacaoService = autenticacaoService;
    }

    @Bean
    public SecurityFilterChain filtrosSeguranca(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers("/testes-db/**").permitAll()

                        .requestMatchers("/register-funcionario", "/register-admin").hasRole("ADMIN")

                        .requestMatchers("/assistente/**").hasAnyRole("CLIENTE", "FUNCIONARIO", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/ordem-servicos/**").hasAnyRole("CLIENTE", "FUNCIONARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/pagamentos/**").hasAnyRole("CLIENTE", "FUNCIONARIO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/veiculos/**").hasAnyRole("CLIENTE", "FUNCIONARIO", "ADMIN")

                        .requestMatchers("/ordem-servicos/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers("/clientes/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers("/funcionarios/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers("/pagamentos/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers("/veiculos/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers("/produtos/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers("/item-produtos/**").hasAnyRole("FUNCIONARIO", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(filtroTokenAcesso, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(autenticacaoService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
