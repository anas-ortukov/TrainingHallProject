package org.example;
import org.example.entity.AdminDbService;
import org.example.entity.enums.Role;
import org.example.service.AdminService;
import org.example.service.AllServices;
import org.example.service.AuthService;
import org.example.service.ClientService;

public class Main {

    public static void main(String[] args) {
        //To refresh the subs status
        AllServices.refreshSubs();
        //To enter new admin if there's no admin
        AdminDbService.check();
        //Program starts
        while (true) {
            if (AuthService.CURRENT_USER == null) {
                AuthService.logIn();
            }
            if (AuthService.CURRENT_USER.getRole().equals(Role.ADMIN)) {
                AdminService.start();
            } else {
                ClientService.start();
            }
        }

    }
}