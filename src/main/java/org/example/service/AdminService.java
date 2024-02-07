package org.example.service;
import org.example.entity.User;
import org.example.entity.UserRepo;
import org.example.entity.enums.Role;
import org.example.utils.Input;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
public class AdminService {
    public static final UserRepo userRepo = UserRepo.getInstance();

    public static void start() {
        System.out.println("""
                \n1. Creating new client
                2. All clients
                3. Log out""");
        switch (Input.inputInt("Choose : ")) {
            case 1 -> createNewClient();
            case 2 -> showAllClients();
            case 3 -> AuthService.CURRENT_USER = null;
        }
    }

    private static void showAllClients() {
        if (userRepo.getAll().isEmpty()) {
            System.out.println("\nNo Users yet\n");
            return;
        }
        System.out.println("N          EMAIL            STATUS");
        int i = 1;
        for (User user : userRepo.getAll()) {
            System.out.println(i + ". " + user.getEmail() + "   " + AllServices.checkStatus(user));
            i++;
        }
        buyAdditionalSub();
    }

    private static void buyAdditionalSub() {
        System.out.println("""
                \nYou can choose user's index in order to buy additional subs
                0 - Back""");
        int indexOfChoice = Input.inputInt("Choose : ") - 1;
        if (indexOfChoice == -1) {
            return;
        }
        AllServices.chooseSubs(userRepo.getAll().get(indexOfChoice));
    }

    private static void createNewClient() {
        User user = new User(AuthService.checkEmail(), AuthService.checkPassword(),
                Role.USER, false, generateId());
        userRepo.save(user);
        sendPasswordToClient(user);
        AllServices.chooseSubs(user);
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

    private static void sendPasswordToClient(User user) {
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/mail.properties");
            String username = "azizortukov818@gmail.com";
            String password = "jfwiblejdjarrawq";
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
            message.setSubject("The Training Hall Administration");
            message.setText("Your password to login Training Hall's program is " + user.getPassword() +
                    "\nYou can change it later!");
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
