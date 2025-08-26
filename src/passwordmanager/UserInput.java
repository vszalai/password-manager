package passwordmanager;

import java.util.Scanner;

public class UserInput {
    public static void addEntry(Scanner scanner, char[] master) {
        Entry entry = new Entry();
        while (true) {
            System.out.println("Give this entry a unique name.");
            entry.id = scanner.nextLine();
            if (Utils.isDuplicateID(entry.id, master)) {
                System.out.println("An entry with that name already exists. Please give this entry a unique name.");
                continue;
            }
            if (entry.id == null || entry.id.length() == 0 || entry.id.split(" ").length == 0) {
                System.out.println("Invalid name. Please try another.");
                continue;
            }
            if (!Utils.isDuplicateID(entry.id, master)) {
                break;
            }
        }
        while (true) {
            System.out.println("Give this entry a username/email.");
            entry.name = scanner.nextLine();
            if (entry.name == null || entry.name.length() == 0 || entry.name.split(" ").length == 0) {
                System.out.println("Invalid name/email. Please try another.");
                continue;
            }
            break;

        }
        String answer;
        int pwLength;
        while (true) {
            System.out.println("Do you want to use a randomly generated password? (y/n)");
            answer = scanner.nextLine().toLowerCase();
            if (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Invalid answer.");
                continue;
            }
            break;
        }
        while (answer.equals("y")) {
            System.out.println("How many characters should the password be? 16-64 characters");
            try {
                pwLength = Integer.parseInt(scanner.nextLine());
            } catch (Exception err) {
                System.out.println("Not an integer.");
                continue;
            }
            if (pwLength < 16 || pwLength > 64) {
                System.out.println("Too large/small of a number.");
                continue;
            }
            entry.password = Utils.generatePassword(master, pwLength);
            System.out.println("Your generated password: " + entry.password);
            break;
        }
        while (answer.equals("n")) {
            System.out.println("Give this entry a password.");
            entry.password = scanner.nextLine();
            if (entry.password == null || entry.password.length() == 0 || entry.password.split(" ").length == 0) {
                System.out.println("Invalid password. Please try another.");
                continue;
            }
            break;
        }

        try {
            entry.password = Encryption.encryptPassword(master, entry.password.getBytes());
        } catch (Exception err) {
            System.err.println(err);
        }
        FileManagement.writeEntry(entry);
    }

    public static void deleteEntry(Scanner scanner, char[] master) {
        System.out.println("Which entry do you want to delete?");
        String target = scanner.nextLine();
        Entry targetEntry = Utils.findEntry(scanner, target, master);
        while (true) {
            System.out.println("Are you sure you want to delete this entry? (y/n) \n" + targetEntry.toString());
            String answer = scanner.nextLine().toLowerCase();
            if (answer.equals("y")) {
                FileManagement.deleteEntry(scanner, target);
            } else if (answer.equals("n")) {
                System.out.println("The entry was not deleted.");
                return;
            }
        }

    }

    public static void findEntry(Scanner scanner, char[] master) {
        System.out.println("Which entry are you looking for?");
        String target = scanner.nextLine();
        Entry entry = Utils.findEntry(scanner, target, master);
        if (entry.isValid()) {
            try {
                entry.password = new String(Encryption.decryptPassword(master, entry.password));
            } catch (Exception err) {
                System.out.println(err);
            }

            System.out.println("Entry found: " + entry);
        } else {
            System.out.println("Did not find entry with id: " + target);
        }
    }

}
