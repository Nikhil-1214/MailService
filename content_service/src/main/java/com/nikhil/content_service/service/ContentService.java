package com.nikhil.content_service.service;

import com.nikhil.content_service.model.Content;
import com.nikhil.content_service.repository.ContentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
public class ContentService {
    private final ContentRepository repo;

    public ContentService(ContentRepository repo) {
        this.repo = repo;
    }

    public Content getCont(){
        return repo.findFirstContent();
    }
    @Transactional
    public void deleterow(Long id){
        repo.deleteById(id);
    }
}
