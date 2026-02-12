package com.example.jpa_final;

import com.example.jpa_final.models.Author;
import com.example.jpa_final.models.Video;
import com.example.jpa_final.repositories.AuthorRepository;
import com.example.jpa_final.repositories.VideoRepository;
import com.example.jpa_final.specification.AuthorSpecification;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;

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
            for (int i = 0; i < 50; i++) {
                Faker faker = new Faker();
                var author = Author.builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .age(faker.number().numberBetween(19,50))
                    .email(faker.name().username()+"@gmail.com")
                    .build();
            //repository.save(author);
            }
//            var video = Video.builder()
//                    .name("abc")
//                    .length(5)
//                    .build();
//            videoRepository.save(video);

//            var author = Author.builder()
//                    .id(1)
//                    .firstName("Andrija")
//                    .lastName("Cvijetic")
//                    .age(95)
//                    .email("andr@gmail.com")
//                    .build();
//            repository.save(author);
                //repository.updateAllAuthorsAge(99);
//            List<String> names = new ArrayList<>();
//            names.add("Carolee");
//            names.add("Charolette");
//            names.add("Rolland");
//            repository.findAllByFirstNameInIgnoreCase(names)
//                        .forEach(System.out::println);
            //find by named query
            //repository.findByNamedQuery(46)
                    //.forEach(System.out::println);
            //update with named query
            //repository.updateByNamedQuery(12);
            Specification<Author> spec = Specification
                    .where(AuthorSpecification.hasAge(37))
                    .or(AuthorSpecification.firstNameLike("El"));
            repository.findAll(spec).forEach(System.out::println);
        };


    }
}


