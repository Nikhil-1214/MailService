package com.nikhil.content_service.repository;

import com.nikhil.content_service.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long> {
    @Query(value = "SELECT * FROM users ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Content findFirstContent();
}
