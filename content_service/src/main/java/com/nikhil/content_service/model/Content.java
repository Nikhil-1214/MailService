package com.nikhil.content_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concept;

    @Column(columnDefinition = "TEXT")
    private String theory;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(columnDefinition = "TEXT")
    private String example;

    // --- MANUALLY ADDED GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }

    public String getTheory() { return theory; }
    public void setTheory(String theory) { this.theory = theory; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
}