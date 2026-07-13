package com.agapehill.agape_hill_backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NextOfKinResponse {
    private String name;
    private String relationship;
    private String phoneNumber;
    private String email;
    private String address;
}