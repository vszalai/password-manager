package passwordmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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

    public static void writePassword(Entry entry) {
        try {
            FileWriter fw = new FileWriter("data.txt", true);
            fw.write(entry.id + "\t" + entry.name + "\t" + entry.password + "\n");
            fw.close();
        } catch (Exception err) {
            System.err.println(err);
        }
    }

    public static void deleteEntry(Scanner scanner, String target) {
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

}
