package org.example.db;

import org.example.entity.User;
import org.example.entity.enums.Role;

import java.util.ArrayList;
import java.util.List;

public interface DB_ADMIN {

    List<User> ADMINS = new ArrayList<>(List.of(
            User.builder()
                    .email("bobpompey818@gmail.com")
                    .role(Role.ADMIN)
                    .password("Mrbob123$")
                    .blocked(false).build()
    ));


}
