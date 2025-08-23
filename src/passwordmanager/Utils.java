package passwordmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

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
                try {
                    entry.password = new String(Encryption.DecryptPassword(master, splitEntry[2]));
                } catch (Exception err) {
                    System.out.println(err);
                }

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

    public static void printEntries(ArrayList<Entry> entries) {
        for (Entry entry : entries) {
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
                    entry.password = new String(Encryption.DecryptPassword(master, splitEntry[2]));

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

}
