package com.example.jpa_final.repositories;

import com.example.jpa_final.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

}
