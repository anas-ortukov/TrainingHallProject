package org.example.service;

import org.example.entity.SubRepo;
import org.example.entity.Subscription;
import org.example.entity.User;
import org.example.entity.UserRepo;
import org.example.entity.enums.Role;
import org.example.entity.enums.SubStatus;
import org.example.entity.enums.SubType;
import org.example.utils.Input;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdminService {
    private static final SubRepo subRepo = SubRepo.getInstance();

    public static final UserRepo userRepo = UserRepo.getInstance();
    public static void start() {
        refreshSubs();
        System.out.println("""
                \n1. Creating new client
                2. All clients""");
        switch (Input.inputInt("Choose : ")) {
            case 1 -> createNewClient();
        }
    }

    private static void createNewClient() {
        User user = new User(AuthService.checkEmail(), AuthService.checkPassword(),
                Role.USER, false, generateId());
        userRepo.save(user);
        chooseSubs(user);
    }

    private static void chooseSubs(User user) {
        System.out.println("""
                1. DAILY
                2. MONTHLY
                3. QUARTER-YEAR""");
        switch (Input.inputInt("Choose : ")) {
            case 1 -> {
                Subscription sub = new Subscription(
                    user.getCode(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        SubType.DAILY, SubStatus.ACTIVE
                );
                subRepo.save(sub);
            }
            case 2 -> {
                Subscription sub = new Subscription(
                        user.getCode(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1),
                        SubType.DAILY, SubStatus.ACTIVE
                );
                subRepo.save(sub);
            }
            case 3 -> {
                Subscription sub = new Subscription(
                        user.getCode(), LocalDateTime.now(), LocalDateTime.now().plusMonths(3),
                        SubType.DAILY, SubStatus.ACTIVE
                );
                subRepo.save(sub);
            }
        }
        System.out.println("\nSubscription has been purchased and saved!\n");
    }

    private static int generateId() {
        Random random = new Random();
        int id = random.nextInt(100, 999);
        if (userRepo.getAll().isEmpty()) {
            return id;
        }
        for (User user : userRepo.getAll()) {
            if (user.getCode() == id) {
                return generateId();
            }
        }
        return id;
    }

    private static void refreshSubs() {
        ScheduledExecutorService checkSubs = Executors.newScheduledThreadPool(1);
        checkSubs.scheduleAtFixedRate(()-> {
            for (Subscription subscription : subRepo.getAll()) {
                if (subscription.getEndDate().isBefore(LocalDateTime.now())) {
                    subRepo.update(subscription);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
