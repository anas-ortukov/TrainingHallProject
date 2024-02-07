package org.example.entity;

import org.example.entity.enums.SubStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SubRepo {

    private static final String PATH = "src/main/java/org/example/db/subs_db.txt";
    private static SubRepo singleton;
    private final List<Subscription> subs;

    private SubRepo(List<Subscription> subs) {
        this.subs = subs;
    }

    public static SubRepo getInstance() {
        if (singleton == null) {
            singleton = new SubRepo(loadData());
        }
        return singleton;
    }

    @SuppressWarnings("unchecked")
    private static List<Subscription> loadData() {
        try (
                InputStream is = new FileInputStream(PATH);
                ObjectInputStream inputStream = new ObjectInputStream(is)
        ) {
            return (List<Subscription>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void uploadData() {
        try (
                OutputStream is = new FileOutputStream(PATH);
                ObjectOutputStream outputStream = new ObjectOutputStream(is)
        ) {
            outputStream.writeObject(subs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Subscription sub) {
        subs.add(sub);
        uploadData();
    }

    public List<Subscription> getAll() {
        return subs;
    }

    public void update(Subscription sub) {
        sub.setStatus(SubStatus.IN_ACTIVE);
        uploadData();
    }

}
