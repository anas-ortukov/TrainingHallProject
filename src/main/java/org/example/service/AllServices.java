package org.example.service;

import org.example.entity.SubRepo;
import org.example.entity.Subscription;
import org.example.entity.User;
import org.example.entity.enums.SubStatus;
import org.example.entity.enums.SubType;
import org.example.utils.Input;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AllServices {
    private static final SubRepo subRepo = SubRepo.getInstance();

    public static void chooseSubs(User user) {
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

    public static synchronized String checkStatus(User user) {
        for (Subscription subscription : subRepo.getAll()) {
            if (subscription.getStatus().equals(SubStatus.ACTIVE) && subscription.getUserId() == user.getCode()) {
                return "✓";
            }
        }
        return "✗";
    }

    public static void refreshSubs() {
        ScheduledExecutorService checkSubs = Executors.newScheduledThreadPool(1);
        checkSubs.scheduleAtFixedRate(() -> {
            for (Subscription subscription : subRepo.getAll()) {
                if (subscription.getStatus().equals(SubStatus.ACTIVE)) {
                    if (subscription.getEndDate().isBefore(LocalDateTime.now())) {
                        subRepo.update(subscription);
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

}
