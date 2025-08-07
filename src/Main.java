import java.util.Scanner;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

class Entry {
    String id;
    String name;
    String password;
}

public class Main {
    public static void main(String[] args) {
        System.out.println("""
                    What do you want to do?
                    1. Add a new password
                    2. View all entries
                    3. Search for an entry
                    4. Delete an entry
                    5. Exit
                """);
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        switch (input) {
            case 1:
                addPassword();
                break;
            case 2:
                fetchEntries();
                break;
            case 3:
                Entry result = findEntry();
                System.out.println(result.name);
                break;
            case 4:
                deleteEntry();
                break;
            case 5:
                break;
        }
        scanner.close();

    }

    public static boolean addPassword() {
        Entry entry = new Entry();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give this entry a unique name.");
        entry.id = scanner.nextLine();
        System.out.println("Give this entry a username/email.");
        entry.name = scanner.nextLine();
        System.out.println("Give this entry a password.");
        entry.password = scanner.nextLine();
        scanner.close();
        try {
            FileWriter fw = new FileWriter("data.txt", true);
            fw.write(entry.id + ":" + entry.name + ":" + entry.password + "\n");
            fw.close();
        } catch (Exception err) {
            System.err.println(err);
        }
        return true;

    }

    public static void fetchEntries() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            ArrayList<Entry> entries = new ArrayList<>();
            while (line != null) {
                if (line.length() == 0 && line != null) {
                    line = reader.readLine();
                    continue;
                }
                String[] splitEntry = line.split(":");
                if (splitEntry.length != 3) {
                    line = reader.readLine();
                    continue;
                }
                Entry entry = new Entry();
                entry.id = splitEntry[0];
                entry.name = splitEntry[1];
                entry.password = splitEntry[2];
                entries.add(entry);
                line = reader.readLine();
            }
            reader.close();
            for (Entry entry : entries) {
                System.out.println("ID: " + entry.id + ", Name: " + entry.name + ", Password: " + entry.password);
            }
        } catch (Exception err) {
            System.err.println(err);
        }

    }

    public static Entry findEntry() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which entry are you looking for?");
        String target = scanner.nextLine();
        scanner.close();
        Entry entry = new Entry();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            while (line != null) {
                if (line.length() == 0 && line != null) {
                    line = reader.readLine();
                    continue;
                }
                String[] splitEntry = line.split(":");
                if (splitEntry.length != 3) {
                    line = reader.readLine();
                    continue;
                }
                if (splitEntry[0].equals(target)) {
                    entry.id = splitEntry[0];
                    entry.name = splitEntry[1];
                    entry.password = splitEntry[2];
                    reader.close();
                    return entry;
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception err) {
            System.err.println(err);
        }
        return entry;

    }

    public static void deleteEntry() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which entry do you want to delete?");
        fetchEntries();
        String target = scanner.nextLine();
        scanner.close();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            String entries = "";
            while (line != null) {
                if (line.length() == 0 && line != null) {
                    line = reader.readLine();
                    continue;
                }
                String[] splitEntry = line.split(":");
                if (splitEntry.length != 3) {
                    line = reader.readLine();
                    continue;
                }
                if (splitEntry[0].equals(target)) {
                    line = reader.readLine();
                    continue;
                }
                entries += line + "\n";
                line = reader.readLine();
            }
            reader.close();
            BufferedWriter fw = new BufferedWriter(new FileWriter("data.txt"));
            fw.write(entries);
            fw.close();

        } catch (Exception err) {
            System.err.println(err);
        }

    }
}
