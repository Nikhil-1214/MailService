package com.nikhil.content_service.service;

import com.nikhil.content_service.model.Content;
import com.nikhil.content_service.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ContentService {
    @Autowired private ContentRepository repository;
    @Autowired private JdbcTemplate jdbcTemplate;

    public Content getContent() {
        // 1. Fetch the last ID sent from your tracker table
        Integer lastId = jdbcTemplate.queryForObject("SELECT last_sent_id FROM tracker WHERE id = 1", Integer.class);

        // 2. Calculate next ID (loops 1 through 50)
        long nextId = (lastId % 50) + 1;

        // 3. Update the tracker table so next time it starts from here
        jdbcTemplate.update("UPDATE tracker SET last_sent_id = ? WHERE id = 1", nextId);

        // 4. Return the record
        return repository.findById(nextId).orElse(null);
    }

    public void deleteRow(Long id) {
        repository.deleteById(id);
    }
}