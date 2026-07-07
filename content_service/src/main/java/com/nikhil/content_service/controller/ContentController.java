package com.nikhil.content_service.controller;

import com.nikhil.content_service.model.Content;
import com.nikhil.content_service.service.ContentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    // Manually injecting the dependency bypasses the Lombok Maven issue entirely!
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/next")
    public Content getNext(){
        return contentService.getContent();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        contentService.deleteRow(id);
    }
}