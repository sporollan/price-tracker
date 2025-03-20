package com.sporollan.auth_service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserCreate {
    private String username;
    private String password;
    private String email;
}
