package com.nikhil.content_service.controller;

import com.nikhil.content_service.model.Content;
import com.nikhil.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/next")
     public Content getNex(){
         return contentService.getCont();
     }
     @DeleteMapping("/{id}")
     public void delete(@PathVariable Long id){
        contentService.deleterow(id);
     }
}
