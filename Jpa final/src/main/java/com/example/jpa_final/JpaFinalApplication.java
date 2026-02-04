package com.example.jpa_final;

import com.example.jpa_final.models.Author;
import com.example.jpa_final.repositories.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaFinalApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(AuthorRepository repository) {

        return args -> {
            var author = Author.builder()
                    .firstName("andrija")
                    .lastName("cvijetic")
                    .age(34)
                    .email("primer@gmail.com")
                    .build();
            repository.save(author);
        };

    }
}


