package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public void add(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipment (name, serial_number, criticality) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipment.getName());
            ps.setString(2, equipment.getSerialNumber());
            ps.setInt(3, equipment.getCriticality());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    equipment.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Equipment> getAll() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT id, name, serial_number, criticality FROM equipment";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("serial_number"),
                        rs.getInt("criticality")
                ));
            }
        }
        return list;
    }

    public Equipment getById(int id) throws SQLException {
        String sql = "SELECT id, name, serial_number, criticality FROM equipment WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("serial_number"),
                            rs.getInt("criticality")
                    );
                }
            }
        }
        return null;
    }

    public boolean update(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipment SET name = ?, serial_number = ?, criticality = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipment.getName());
            ps.setString(2, equipment.getSerialNumber());
            ps.setInt(3, equipment.getCriticality());
            ps.setInt(4, equipment.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}