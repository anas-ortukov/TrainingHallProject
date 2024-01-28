package org.example.service;

import lombok.SneakyThrows;
import org.example.entity.SubRepo;
import org.example.entity.Subscription;
import org.example.entity.User;
import org.example.entity.UserRepo;
import org.example.entity.enums.SubStatus;
import org.example.entity.enums.SubType;
import org.example.utils.Input;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Properties;
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
                        sendUpdateToUser(subscription.getUserId());
                        subRepo.update(subscription);
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @SneakyThrows
    private static void sendUpdateToUser(int userId) {
        UserRepo userRepo = UserRepo.getInstance();
        for (User user : userRepo.getAll()) {
            if (user.getCode() == userId) {
                try (
                        FileInputStream fileInputStream = new FileInputStream("src/main/resources/mail.properties")
                ) {
                    String username = "azizortukov818@gmail.com";
                    String password = "vwkvnmsussdjiynm";
                    Properties properties = new Properties();
                    properties.load(fileInputStream);
                    Session session = Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
                    message.setSubject("Your subscription is expired");
                    message.setText("Your subscription is expired. If you want to enter " +
                            "\nto Training Hall buy new subcription\nWith regards Training Hall");
                    Transport.send(message);
                }
            }
        }
    }

}
