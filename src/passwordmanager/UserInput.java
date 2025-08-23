package passwordmanager;

import java.util.Scanner;

public class UserInput {
    public static void addPassword(Scanner scanner, char[] master) {
        Entry entry = new Entry();
        System.out.println("Give this entry a unique name.");
        entry.id = scanner.nextLine();
        System.out.println("Give this entry a username/email.");
        entry.name = scanner.nextLine();
        System.out.println("Give this entry a password.");
        entry.password = scanner.nextLine();
        try {
            entry.password = Encryption.EncryptPassword(master, entry.password.getBytes());
        } catch (Exception err) {
            System.err.println(err);
        }

        FileManagement.writePassword(entry);
    }

    public static void deleteEntry(Scanner scanner) {
        System.out.println("Which entry do you want to delete?");
        String target = scanner.nextLine();
        FileManagement.deleteEntry(scanner, target);
    }

    public static void findEntry(Scanner scanner, char[] master) {
        System.out.println("Which entry are you looking for?");
        String target = scanner.nextLine();
        Entry entry = Utils.findEntry(scanner, target, master);
        if (entry.isValid()) {
            System.out.println("Entry found: " + entry);
        } else {
            System.out.println("Did not find entry with id: " + target);
        }

    }

}
