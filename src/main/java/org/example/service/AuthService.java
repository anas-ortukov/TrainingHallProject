package org.example.service;

import lombok.SneakyThrows;
import org.example.db.DB_ADMIN;
import org.example.entity.User;
import org.example.entity.UserRepo;
import org.example.utils.Input;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class AuthService {

    public static User CURRENT_USER;
    public static UserRepo userRepo = UserRepo.getInstance();

    public static void logIn() {
        String email = checkEmail();
        twoStepAuthentication(email);
        String password = checkPassword();
        checkRole(email, password);
    }

    private static void checkRole(String email, String password) {
        for (User admin : DB_ADMIN.ADMINS) {
            if (email.equals(admin.getEmail()) && password.equals(admin.getPassword())) {
                CURRENT_USER = admin;
                AdminService.start();
                return;
            }
        }
        for (User user : userRepo.getAll()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                CURRENT_USER = user;
                ClientService.start();
                return;
            }
        }
        System.out.println("\nThere is no such an User. Either email or password is wrong. Please try again!\n");
        logIn();
    }

    @SneakyThrows
    public static void twoStepAuthentication(String email) {
        System.out.println("\nConfirmation Code was sent to your email\n");
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
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            sendMessage(message);
        }
    }

    @SneakyThrows
    private static void sendMessage(Message message) {
        try (
                ExecutorService service = Executors.newSingleThreadExecutor()
        ) {
            Future<Integer> code = service.submit(() -> {
                Random random = new Random();
                int generatedCode = random.nextInt(1010, 1900);
                message.setText("Do not share this code with anyone : " + generatedCode);
                Transport.send(message);
                return generatedCode;

            });

            int codeOfUser = Input.inputInt("Enter the code : ");
            if (!(codeOfUser == code.get())) {
                System.out.println("\nThe entered code is wrong. The code sent again\n");
                sendMessage(message);
            }
        }
    }

    public static String checkPassword() {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*\\W)(?=.*[a-z])(?=.*[A-Z]).{8,}$");
        String password = Input.inputStr("Enter password : ");
        if (Pattern.matches(passwordPattern.pattern(), password)) {
            return password;
        }
        System.out.println("The format of password is wrong. Please try again!");
        return checkPassword();
    }

    public static String checkEmail() {
        Pattern emailPatter = Pattern.compile("^[a-zA-Z0-9._%+-]+@(?:email|gmail|mail)\\.com$");
        String email = Input.inputStr("Enter email : ");
        if (Pattern.matches(emailPatter.pattern(), email)) {
            return email;
        }
        System.out.println("\nThe format of email is wrong. Please try again!\n");
        return checkEmail();
    }

}
