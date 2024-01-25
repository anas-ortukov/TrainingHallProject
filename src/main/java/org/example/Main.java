package org.example;

import org.example.service.AuthService;

public class Main {
    public static void main(String[] args) {

        while (true) {
            if (AuthService.CURRENT_USER == null){
                AuthService.logIn();
            }

        }

    }
}