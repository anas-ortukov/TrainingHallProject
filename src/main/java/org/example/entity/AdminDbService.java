package org.example.entity;

import org.example.entity.enums.Role;
import org.example.service.AuthService;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDbService {
    private static final String PATH = "src/main/java/org/example/db/admin_db.txt";
    private static AdminDbService singleton;
    private final List<User> admin;
    private AdminDbService(List<User> admin) {
        this.admin = admin;
    }

    public static AdminDbService getInstance() {
        if (singleton == null) {
            singleton = new AdminDbService(loadData());
        }
        return singleton;
    }

    @SuppressWarnings("unchecked")
    private static List<User> loadData() {
        try (
                InputStream is = new FileInputStream(PATH);
                ObjectInputStream inputStream = new ObjectInputStream(is)
        ) {
            return (List<User>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    private void uploadData() {
        try (
                OutputStream is = new FileOutputStream(PATH);
                ObjectOutputStream outputStream = new ObjectOutputStream(is)
        ) {
            outputStream.writeObject(admin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> get() {
        return admin;
    }
    public void save(User newAdmin) {
        admin.add(newAdmin);
        uploadData();
    }
    public static void check() {
        AdminDbService service = AdminDbService.getInstance();
        if (service.get().isEmpty()) {
            System.out.println("\nENTER EMAIL AND PASSWORD OF ADMIN\n");
            String email = AuthService.checkEmail();
            String password = AuthService.checkPassword();
            User newAdmin = User.builder()
                    .email(email)
                    .password(password)
                    .role(Role.ADMIN)
                    .blocked(false)
                    .build();
            service.save(newAdmin);
            System.out.println("\nADMIN HAS BEEN SAVED. PROGRAM STARTED\n");
        }
    }

}
