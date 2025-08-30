package passwordmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class UserInput {
    public static void addEntry(Scanner scanner, char[] master) {
        Entry entry = new Entry();
        while (true) {
            System.out.println("Give this entry a unique ID.");
            entry.id = scanner.nextLine();
            if (Utils.isDuplicateID(entry.id, master)) {
                System.out.println("An entry with that ID already exists. Please give this entry a unique ID.");
                continue;
            }
            if (entry.id == null || entry.id.length() == 0 || entry.id.split(" ").length == 0) {
                System.out.println("Invalid ID. Please try another.");
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
            System.out.println("Do you want to use a randomly generated password? Will be echoed. (y/n)");
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
            System.out.println("Give this entry a password. Will not be echoed.");
            entry.password = new String(System.console().readPassword());
            if (entry.password == null || entry.password.length() == 0 || entry.password.split(" ").length == 0) {
                System.out.println("Invalid password. Please try another.");
                continue;
            }
            break;
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
                boolean result = FileManagement.deleteEntry(scanner, target);
                if (result) {
                    System.out.println("Entry deleted.");
                } else {
                    System.out.println(String.format("No entry found with the name %s, nothing was deleted.", target));
                }
                return;
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
                entry.password = new String(Crypto.decryptPassword(master, entry.password));
            } catch (Exception err) {
                System.out.println(err);
            }

            System.out.println("Entry found: " + entry);
        } else {
            System.out.println("Did not find entry with id: " + target);
        }
    }

    public static char[] initializeMasterPassword(Scanner scanner) {

        while (true) {
            System.out.println(
                    "Give a master password that will be used for encryption. Please write this password down in a secure location. If you lose it, you will not be able to access any of the stored passwords. ");
            char[] master = System.console().readPassword();
            if (master == null || master.length == 0) {
                System.out.println("Invalid password.");
                continue;
            }
            System.out.println("Please confirm your master password.");
            char[] masterconfirm = System.console().readPassword();
            if (!Arrays.equals(master, masterconfirm)) {
                System.out.println("Passwords do not match.\n");
                continue;
            } else {
                return master;
            }

        }

    }

    public static void updateEntry(Scanner scanner, char[] master) {
        System.out.println("Which entry do you want to update?");
        String target = scanner.nextLine();
        ArrayList<Entry> entries = Utils.fetchEntries(master);
        if (!entries.stream().anyMatch(entry -> entry.id.equals(target))) {
            System.out.println("No entry found with that ID.");
            return;
        }
        Entry targetEntry = entries.stream().filter(entry -> entry.id.equals(target)).findFirst().orElse(null);
        try {
            targetEntry.password = new String(Crypto.decryptPassword(master, targetEntry.password));
        } catch (Exception err) {
            System.out.println(err);
        }

        System.out.println(targetEntry.toString());
        String input;
        Entry newEntry = new Entry();
        while (true) {
            System.out.println("Type in the new id. (Leave empty if unchanged)");
            input = scanner.nextLine();
            if (input == null || input.length() == 0 || input.split(" ").length == 0) {
                newEntry.id = targetEntry.id;
                break;
            } else {
                newEntry.id = input;
                break;
            }
        }
        while (true) {
            System.out.println("Type in the new username/email. (Leave empty if unchanged)");
            input = scanner.nextLine();
            if (input == null || input.length() == 0 || input.split(" ").length == 0) {
                newEntry.name = targetEntry.name;
                break;
            } else {
                newEntry.name = input;
                break;
            }
        }
        while (true) {
            System.out.println("Type in the new password. (Leave empty if unchanged)");
            char[] password = System.console().readPassword();
            if (input == null || input.length() == 0 || input.split(" ").length == 0) {
                newEntry.password = targetEntry.password;
                break;
            } else {
                newEntry.password = new String(password);
                break;
            }
        }

        FileManagement.deleteEntry(scanner, targetEntry.id);
        FileManagement.writeEntry(newEntry);
        System.out.println(String.format("Entry with id %s updated.", newEntry.id));
    }

    public static void changeMaster(Scanner scanner, char[] master) {
        while (true) {
            System.out.println("Type in your old master password.");
            char[] temp = System.console().readPassword();
            if (!Arrays.equals(master, temp)) {
                System.out.println("Master password invalid.");
                continue;
            }
            break;
        }
        char[] newMaster;
        while (true) {
            System.out.println("Type in your new master password.");
            newMaster = System.console().readPassword();
            if (newMaster == null || newMaster.length == 0) {
                System.out.println("Invalid password, should have at least the length of 1.");
                continue;
            }
            System.out.println("Confirm your new master password.");
            char[] newMasterConfirm = System.console().readPassword();
            if (newMasterConfirm == null || newMasterConfirm.length == 0) {
                System.out.println("Invalid password, should have at least the length of 1.");
                continue;
            }
            if (!Arrays.equals(newMaster, newMasterConfirm)) {
                System.out.println("Passwords do not match.");
                continue;
            }
            break;
        }

        boolean createVerification = FileManagement.createDataFile(newMaster);
        if (createVerification) {
            System.out.println("Master password changed, reencrypting all entries.");
        } else {
            System.out.println("Master password change failed.");
            return;
        }
        try {
            FileManagement.reEncrypt(master, newMaster);
            System.out.println("Re-encryption successful.");
            return;
        } catch (Exception err) {
            System.out.println("Re-encryption unsuccessful with error message: " + err);
        }
    }

}
