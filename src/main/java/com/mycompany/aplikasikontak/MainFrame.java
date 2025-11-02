package com.mycompany.aplikasikontak;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private JTextField tfName, tfPhone, tfSearch;
    private JComboBox<String> cbCategory;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnExport, btnImport, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public MainFrame() {
        setTitle("Aplikasi Pengelolaan Kontak");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel pTop = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; pTop.add(new JLabel("Nama:"), c);
        tfName = new JTextField(20);
        c.gridx = 1; c.gridy = 0; pTop.add(tfName, c);

        c.gridx = 0; c.gridy = 1; pTop.add(new JLabel("Nomor Telepon:"), c);
        tfPhone = new JTextField(15);
        c.gridx = 1; c.gridy = 1; pTop.add(tfPhone, c);

        c.gridx = 0; c.gridy = 2; pTop.add(new JLabel("Kategori:"), c);
        cbCategory = new JComboBox<>(new String[]{"Keluarga","Teman","Kerja","Lainnya"});
        c.gridx = 1; c.gridy = 2; pTop.add(cbCategory, c);

        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        JPanel pButtons = new JPanel();
        pButtons.add(btnAdd); pButtons.add(btnEdit); pButtons.add(btnDelete); pButtons.add(btnRefresh);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; pTop.add(pButtons, c);

        // Search & CSV buttons
        tfSearch = new JTextField(20);
        btnSearch = new JButton("Cari");
        btnExport = new JButton("Ekspor CSV");
        btnImport = new JButton("Impor CSV");
        JPanel pBottomTop = new JPanel();
        pBottomTop.add(new JLabel("Cari (nama/phone):"));
        pBottomTop.add(tfSearch);
        pBottomTop.add(btnSearch);
        pBottomTop.add(btnExport);
        pBottomTop.add(btnImport);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID","Nama","Telepon","Kategori"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // sembunyikan kolom ID dari tampilan tapi tetap ada di model
        JScrollPane scroll = new JScrollPane(table);

        // layout
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(pTop, BorderLayout.NORTH);
        cp.add(pBottomTop, BorderLayout.CENTER);
        cp.add(scroll, BorderLayout.SOUTH);

        // Event listeners
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnSearch.addActionListener(e -> onSearch());
        btnRefresh.addActionListener(e -> loadTable());
        btnExport.addActionListener(e -> onExport());
        btnImport.addActionListener(e -> onImport());

        // Double-click row to load into form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) loadSelectedToForm();
            }
        });
    }

    private void onAdd() {
        String name = tfName.getText().trim();
        String phone = tfPhone.getText().trim();
        String category = (String) cbCategory.getSelectedItem();

        String err = validateInput(name, phone);
        if (!err.isEmpty()) { JOptionPane.showMessageDialog(this, err, "Validation", JOptionPane.WARNING_MESSAGE); return; }

        try {
            ContactDAO.insert(new Contact(name, phone, category));
            JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan.");
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kontak: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        int modelRow = table.getSelectedRow();
        if (modelRow == -1) { JOptionPane.showMessageDialog(this, "Pilih data di tabel untuk diedit."); return; }
        int id = (int) tableModel.getValueAt(modelRow, 0);
        String name = tfName.getText().trim();
        String phone = tfPhone.getText().trim();
        String category = (String) cbCategory.getSelectedItem();

        String err = validateInput(name, phone);
        if (!err.isEmpty()) { JOptionPane.showMessageDialog(this, err, "Validation", JOptionPane.WARNING_MESSAGE); return; }

        try {
            Contact c = new Contact(id, name, phone, category);
            ContactDAO.update(c);
            JOptionPane.showMessageDialog(this, "Kontak berhasil diupdate.");
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal update: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int modelRow = table.getSelectedRow();
        if (modelRow == -1) { JOptionPane.showMessageDialog(this, "Pilih data di tabel untuk dihapus."); return; }
        int id = (int) tableModel.getValueAt(modelRow, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Hapus kontak terpilih?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                ContactDAO.delete(id);
                loadTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSearch() {
        String q = tfSearch.getText().trim();
        try {
            List<Contact> list = ContactDAO.search(q);
            fillTable(list);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void onExport() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                List<Contact> list = ContactDAO.getAll();
                CsvUtil.exportToCsv(list, f);
                JOptionPane.showMessageDialog(this, "Ekspor selesai.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal ekspor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onImport() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                CsvUtil.importFromCsv(f);
                loadTable();
                JOptionPane.showMessageDialog(this, "Impor selesai.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal impor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSelectedToForm() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);
        String phone = (String) tableModel.getValueAt(modelRow, 2);
        String category = (String) tableModel.getValueAt(modelRow, 3);
        tfName.setText(name);
        tfPhone.setText(phone);
        cbCategory.setSelectedItem(category);
    }

    private String validateInput(String name, String phone) {
        if (name.isEmpty()) return "Nama tidak boleh kosong.";
        if (phone.isEmpty()) return "Nomor telepon tidak boleh kosong.";
        if (!phone.matches("\\d+")) return "Nomor telepon harus berisi angka saja.";
        if (phone.length() < 8 || phone.length() > 15) return "Panjang nomor telepon harus antara 8 sampai 15 angka.";
        return "";
    }

    private void clearForm() {
        tfName.setText("");
        tfPhone.setText("");
        cbCategory.setSelectedIndex(0);
    }

    private void loadTable() {
        try {
            List<Contact> list = ContactDAO.getAll();
            fillTable(list);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void fillTable(List<Contact> list) {
        tableModel.setRowCount(0);
        for (Contact c : list) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getCategory()});
        }
    }

}
