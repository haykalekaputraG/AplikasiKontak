package com.mycompany.aplikasikontak;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // inisialisasi DB
        DBHelper.initDatabase();

        SwingUtilities.invokeLater(() -> {
            MainFrame mf = new MainFrame();
            mf.setVisible(true);
        });
    }
}
