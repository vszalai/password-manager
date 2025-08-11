package passwordmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class FileManagement {
    private static char[] master;

    public static char[] StartupCheck(Scanner scanner) {
        File dataFile = new File("verification.txt");
        if (dataFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("verification.txt"));
                String line = reader.readLine();
                while (line != null) {
                    String[] splitLine = line.split("\t");
                    if (splitLine[0].equals("verification")) {
                        System.out.println("Type in your master password: ");
                        master = System.console().readPassword();
                        Encryption.VerifyMasterPassword(master, splitLine[1]);
                        reader.close();
                        return master;
                    }
                }
                reader.close();
            } catch (Exception err) {
                System.err.println(err);
                System.exit(0);
            }

        } else {
            System.out.println(
                    "Give a master password that will be used for encryption. Please write this password down. If you lose it, you will not be able to access any of the stored passwords. ");
            master = System.console().readPassword();
            CreateDataFile(master);
            return master;
        }
        return master;
    }

    private static void CreateDataFile(char[] master) {
        try {
            FileWriter fw = new FileWriter("verification.txt", true);
            String verificationToken = Encryption.CreateVerificationToken(master);
            fw.write("verification" + "\t" + verificationToken + "\n");
            fw.close();
        } catch (Exception err) {
            System.err.println(err);
        }

    }

    public static boolean addPassword(Scanner scanner, char[] master) {
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

        try {
            FileWriter fw = new FileWriter("data.txt", true);
            fw.write(entry.id + "\t" + entry.name + "\t" + entry.password + "\n");
            fw.close();
        } catch (Exception err) {
            System.err.println(err);
        }
        return true;
    }

    public static void deleteEntry(Scanner scanner) {
        System.out.println("Which entry do you want to delete?");
        String target = scanner.nextLine();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            String entries = "";
            while (line != null) {
                if (line.length() == 0 && line != null) {
                    line = reader.readLine();
                    continue;
                }
                String[] splitEntry = line.split("\t");
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

    public static void findEntry(Scanner scanner) {
        System.out.println("Which entry are you looking for?");
        String target = scanner.nextLine();
        Entry entry = new Entry();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            while (line != null) {
                if (line.length() == 0 && line != null) {
                    line = reader.readLine();
                    continue;
                }
                String[] splitEntry = line.split("\t");
                if (splitEntry.length != 3) {
                    line = reader.readLine();
                    continue;
                }
                if (splitEntry[0].equals(target)) {
                    entry.id = splitEntry[0];
                    entry.name = splitEntry[1];
                    entry.password = new String(Encryption.DecryptPassword(master, splitEntry[2]));
                    System.out.println("Entry found: " + entry);
                    reader.close();
                    return;
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception err) {
            System.err.println(err);
        }
        System.out.println("Did not find entry with id: " + target);
        return;

    }

    public static void fetchEntries(char[] master) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            ArrayList<Entry> entries = new ArrayList<>();
            while (line != null) {
                if (line.length() == 0 && line != null) {
                    line = reader.readLine();
                    continue;
                }
                String[] splitEntry = line.split("\t");
                if (splitEntry.length != 3) {
                    line = reader.readLine();
                    continue;
                }
                Entry entry = new Entry();
                entry.id = splitEntry[0];
                entry.name = splitEntry[1];
                try {
                    entry.password = new String(Encryption.DecryptPassword(master, splitEntry[2]));
                } catch (Exception err) {
                    System.out.println(err);
                }

                entries.add(entry);
                line = reader.readLine();
            }
            reader.close();
            for (Entry entry : entries) {
                System.out.println(entry);
            }
        } catch (Exception err) {
            System.err.println(err);
        }
    }

}
