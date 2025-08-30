package passwordmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

public class FileManagement {
    private static char[] master;

    public static char[] startupCheck(Scanner scanner) {
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
                        Crypto.VerifyMasterPassword(master, splitLine[1]);
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
            master = UserInput.initializeMasterPassword(scanner);
            createDataFile(master);
            return master;
        }
        return master;
    }

    public static boolean createDataFile(char[] master) {
        try {
            FileWriter fw = new FileWriter("verification.txt");
            String verificationToken = Crypto.CreateVerificationToken(master);
            fw.write("verification" + "\t" + verificationToken + "\n");
            fw.close();
        } catch (Exception err) {
            System.err.println(err);
            return false;
        }
        return true;

    }

    public static void writeEntry(Entry entry) {
        try {
            entry.password = Crypto.encryptPassword(master, entry.password.getBytes());
            FileWriter fw = new FileWriter("data.txt", true);
            fw.write(entry.id + "\t" + entry.name + "\t" + entry.password + "\n");
            fw.close();
        } catch (Exception err) {
            System.err.println(err);
        }
    }

    public static boolean deleteEntry(Scanner scanner, String target) {
        boolean deleted = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = reader.readLine();
            StringBuilder entries = new StringBuilder();
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
                    deleted = true;
                    continue;
                }

                entries.append(line + "\n");
                line = reader.readLine();
            }
            if (deleted) {
                BufferedWriter fw = new BufferedWriter(new FileWriter("data.txt"));
                fw.write(entries.toString());
                fw.close();
            }

            reader.close();

        } catch (Exception err) {
            System.err.println(err);
        }
        return deleted;
    }

    public static void reEncrypt(char[] oldMaster, char[] newMaster) throws Exception {
        ArrayList<Entry> entries = Utils.fetchEntries(oldMaster);
        try {
            FileWriter fw = new FileWriter("data.txt");
            for (Entry entry : entries) {
                entry.password = new String(Crypto.decryptPassword(oldMaster, entry.password));
                entry.password = Crypto.encryptPassword(newMaster, entry.password.getBytes());
                fw.write(entry.id + "\t" + entry.name + "\t" + entry.password + "\n");
            }
            fw.close();
        } catch (Exception err) {
            return;
        }
        return;
    }

}
