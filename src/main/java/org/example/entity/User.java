package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.entity.enums.Role;

@Data
@AllArgsConstructor
@Builder
public class User {

    private String email;
    private String password;
    private Role role;
    private boolean blocked;
    private int code;

}
