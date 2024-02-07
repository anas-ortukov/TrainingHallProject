package org.example.service;

import org.example.entity.SubRepo;
import org.example.entity.Subscription;
import org.example.entity.User;
import org.example.entity.UserRepo;
import org.example.utils.Input;
import java.time.format.DateTimeFormatter;

public class ClientService {

    private static final User Client = AuthService.CURRENT_USER;
    private static final SubRepo subRepo = SubRepo.getInstance();
    private static final UserRepo userRepo = UserRepo.getInstance();

    public static void start() {
        System.out.printf("""
                \nYour status is %s
                        
                1. History Of Subscriptions
                2. Buy new Subscription
                3. Settings
                4. Log out
                \n""", AllServices.checkStatus(Client));
        switch (Input.inputInt("Choose : ")) {
            case 1 -> historySubs();
            case 2 -> AllServices.chooseSubs(Client);
            case 3 -> userRepo.update(Client);
            case 4 -> AuthService.CURRENT_USER = null;
        }

    }

    private static void historySubs() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        for (Subscription subscription : subRepo.getAll()) {
            if (Client.getCode() == subscription.getUserId()) {
                System.out.printf(
                        """
                        Start date : %s
                        End date : %s
                        Status : %s%n""", subscription.getStartTime().format(formatter),
                subscription.getEndDate().format(formatter),
                subscription.getStatus());
            }
        }
    }
}
