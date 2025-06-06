package ro.ticle.paula.spring.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordBeanConfig {

     @Bean
     public BCryptPasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder(10);
     }

}
