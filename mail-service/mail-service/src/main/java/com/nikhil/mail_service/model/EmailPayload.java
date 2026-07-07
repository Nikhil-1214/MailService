package com.nikhil.mail_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailPayload {
    private String userEmail;
    private String userName;
    private ContentDTO content;   // the full DSA concept fetched from content-service
}