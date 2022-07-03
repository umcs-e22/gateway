package com.assigment.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {
    private String userUUID;
    private String username;
    private String email;
    private List<String> roles;
}
