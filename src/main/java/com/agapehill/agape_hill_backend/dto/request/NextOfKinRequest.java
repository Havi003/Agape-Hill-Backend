package com.agapehill.agape_hill_backend.dto.request;

import lombok.Data;

@Data
public class NextOfKinRequest {

    private String name;
    private String relationship;
    private String phoneNumber;
    private String email;
    private String address;
}
