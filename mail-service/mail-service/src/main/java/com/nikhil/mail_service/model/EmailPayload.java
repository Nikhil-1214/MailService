package com.nikhil.mail_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@Data
@NoArgsConstructor
public class EmailPayload implements Serializable {
    private String userEmail;
    private Content content;
}
