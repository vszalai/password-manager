package passwordmanager;

import java.util.Scanner;

class Entry {
    String id;
    String name;
    String password;

    @Override
    public String toString() {
        return "ID: " + id + " Name: " + name + " Password: " + password;
    }

    public boolean isValid() {
        if (id != null && name != null && password != null) {
            return true;
        } else {
            return false;
        }
    }
}

public class Main {
    private static char[] master;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        master = FileManagement.startupCheck(scanner);
        while (true) {
            System.out.println("""
                        What do you want to do?
                        1. Add a new password
                        2. View all entries
                        3. Search for an entry
                        4. Delete an entry
                        5. Exit
                    """);
            int input = scanner.nextInt();
            scanner.nextLine();
            switch (input) {
                case 1:
                    UserInput.addEntry(scanner, master);
                    break;
                case 2:
                    Utils.printEntries(Utils.fetchEntries(master), master);
                    break;
                case 3:
                    UserInput.findEntry(scanner, master);
                    break;
                case 4:
                    UserInput.deleteEntry(scanner);
                    break;
                case 5:
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 5");
                    break;
            }

        }

    }

}
