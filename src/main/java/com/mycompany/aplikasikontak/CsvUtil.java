package com.mycompany.aplikasikontak;

import java.io.*;
import java.util.List;

public class CsvUtil {

    public static void exportToCsv(List<Contact> contacts, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // header
            bw.write("id,name,phone,category");
            bw.newLine();
            for (Contact c : contacts) {
                bw.write(String.format("%d,%s,%s,%s",
                        c.getId(),
                        escape(c.getName()),
                        escape(c.getPhone()),
                        escape(c.getCategory())));
                bw.newLine();
            }
        }
    }

    public static void importFromCsv(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length >= 4) {
                    String name = unescape(cols[1]);
                    String phone = unescape(cols[2]);
                    String category = unescape(cols[3]);
                    try {
                        ContactDAO.insert(new Contact(name, phone, category));
                    } catch (Exception ex) {
                        ex.printStackTrace(); // bisa ditingkatkan: logging
                    }
                }
            }
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " "); // simple escaping: ganti koma dengan spasi
    }
    private static String unescape(String s) {
        return s == null ? "" : s;
    }
}
