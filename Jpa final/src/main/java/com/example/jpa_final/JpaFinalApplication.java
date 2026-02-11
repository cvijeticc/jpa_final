package com.example.jpa_final;

import com.example.jpa_final.models.Author;
import com.example.jpa_final.models.Video;
import com.example.jpa_final.repositories.AuthorRepository;
import com.example.jpa_final.repositories.VideoRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class JpaFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaFinalApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthorRepository repository,
            VideoRepository videoRepository
    ) {

        return args -> {
//            for (int i = 0; i < 50; i++) {
//                Faker faker = new Faker();
//                var author = Author.builder()
//                    .firstName(faker.name().firstName())
//                    .lastName(faker.name().lastName())
//                    .age(faker.number().numberBetween(19,50))
//                    .email("primer"+i+"@gmail.com")
//                    .build();
//            repository.save(author);
//            }
//            var video = Video.builder()
//                    .name("abc")
//                    .length(5)
//                    .build();
//            videoRepository.save(video);
            List<String> names = new ArrayList<>();
            names.add("Carolee");
            names.add("Charolette");
            names.add("Rolland");
            repository.findAllByFirstNameInIgnoreCase(names)
                        .forEach(System.out::println);

        };

    }
}


