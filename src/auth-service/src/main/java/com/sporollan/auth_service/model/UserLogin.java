package com.sporollan.auth_service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserLogin {
    private String email;
    private String password;
}
