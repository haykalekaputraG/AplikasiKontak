package com.mycompany.aplikasikontak;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    public static void insert(Contact c) throws SQLException {
        String sql = "INSERT INTO contacts(name, phone, category) VALUES (?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getCategory());
            ps.executeUpdate();
        }
    }

    public static void update(Contact c) throws SQLException {
        String sql = "UPDATE contacts SET name=?, phone=?, category=? WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getCategory());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<Contact> getAll() throws SQLException {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts ORDER BY name";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Contact(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("category")));
            }
        }
        return list;
    }

    public static List<Contact> search(String query) throws SQLException {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE name LIKE ? OR phone LIKE ? ORDER BY name";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Contact(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("category")));
                }
            }
        }
        return list;
    }
}
