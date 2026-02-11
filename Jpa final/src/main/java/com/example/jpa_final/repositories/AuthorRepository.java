package com.example.jpa_final.repositories;

import com.example.jpa_final.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    //select * from author where first_name = 'andrija'
    List<Author> findAllByFirstName(String fn);
    //select * from author where first_name = 'andrija'
    List<Author> findAllByFirstNameIgnoreCase(String fn);
    //select * from author where first_name like '%dri%'
    List<Author> findAllByFirstNameContainingIgnoreCase(String fn);
    // select * from author where first_name like 'al%'
    List<Author> findAllByFirstNameStartsWithIgnoreCase(String fn);
    // select * from author where first_name like '%al'
    List<Author> findAllByFirstNameEndsWithIgnoreCase(String fn);
    // select * from author where firstName in ('ali', 'bou', 'coding')
    List<Author> findAllByFirstNameInIgnoreCase(List<String> firstName);



}
