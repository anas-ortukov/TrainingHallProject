package org.example.entity;

import org.example.service.AuthService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {

    private static final String PATH = "src/main/java/org/example/db/users_db.txt";
    private static UserRepo singleton;
    private final List<User> users;
    private UserRepo(List<User> users) {
        this.users = users;
    }

    public static UserRepo getInstance() {
        if (singleton == null) {
            singleton = new UserRepo(loadData());
        }
        return singleton;
    }

    @SuppressWarnings("unchecked")
    private static List<User> loadData() {
        try (
                InputStream is = new FileInputStream(PATH);
                ObjectInputStream inputStream = new ObjectInputStream(is);
        ) {
            return (List<User>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void uploadData() {
        try (
                OutputStream is = new FileOutputStream(PATH);
                ObjectOutputStream outputStream = new ObjectOutputStream(is);
        ) {
            outputStream.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(User user) {
        users.add(user);
        uploadData();
    }

    public List<User> getAll() {
        return users;
    }

    public void update(User user) {
        System.out.println("ENTER OLD EMAIL");
        String email = AuthService.checkEmail();
        if (!user.getEmail().equals(email)) {
            System.out.println("The email you entered is wrong!");
            return;
        }
        System.out.println("ENTER OLD PASSWORD");
        String password = AuthService.checkPassword();
        if (!password.equals(user.getPassword())) {
            System.out.println("The password you entered is wrong!");
            return;
        }

        System.out.println("\nENTER NEW EMAIL AND PASSWORD\n");
        String newEmail = AuthService.checkEmail();
        String newPassword = AuthService.checkPassword();
        if (!newPassword.equals(AuthService.checkPassword())) {
            System.out.println("The second password was wrong");
            return;
        }

        System.out.println("We have sent you a code to confirm");
        AuthService.twoStepAuthentication(user.getEmail());
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        uploadData();
        System.out.println("Account has been changed successfully");
    }

}
