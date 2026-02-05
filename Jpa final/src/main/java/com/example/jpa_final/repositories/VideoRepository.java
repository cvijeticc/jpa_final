package com.example.jpa_final.repositories;

import com.example.jpa_final.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video,Integer> {
}
