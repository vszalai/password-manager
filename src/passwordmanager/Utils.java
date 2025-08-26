package passwordmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.SecureRandom;
import java.util.Collections;

public class Utils {
    public static ArrayList<Entry> fetchEntries(char[] master) {
        ArrayList<Entry> entries = new ArrayList<>();
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
                Entry entry = new Entry();
                entry.id = splitEntry[0];
                entry.name = splitEntry[1];
                entry.password = splitEntry[2];

                entries.add(entry);
                line = reader.readLine();
            }
            reader.close();
            return entries;
        } catch (Exception err) {
            System.err.println(err);

        }
        return entries;
    }

    public static void printEntries(ArrayList<Entry> entries, char[] master) {
        for (Entry entry : entries) {
            try {
                entry.password = new String(Encryption.decryptPassword(master, entry.password));
            } catch (Exception err) {
                System.out.println(err);
            }

            System.out.println(entry);
        }
    }

    public static Entry findEntry(Scanner scanner, String target, char[] master) {
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

    public static boolean isDuplicateID(String id, char[] master) {
        ArrayList<Entry> entries = fetchEntries(master);
        return entries.stream().anyMatch(entry -> entry.id.equals(id));
    }

    public static String generatePassword(char[] master, int length) {
        SecureRandom random = new SecureRandom();
        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?/";
        String ALL = UPPER + LOWER + DIGITS + SPECIAL;
        ArrayList<Character> generatedPassword = new ArrayList<>();
        generatedPassword.add(UPPER.charAt(random.nextInt(UPPER.length())));
        generatedPassword.add(LOWER.charAt(random.nextInt(LOWER.length())));
        generatedPassword.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        generatedPassword.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        for (int i = 4; i < length; i++) {
            generatedPassword.add(ALL.charAt(random.nextInt(ALL.length())));
        }
        Collections.shuffle(generatedPassword, random);
        StringBuilder shuffledPassword = new StringBuilder();
        for (Character c : generatedPassword) {
            shuffledPassword.append(c);
        }
        return shuffledPassword.toString();
    }

}
