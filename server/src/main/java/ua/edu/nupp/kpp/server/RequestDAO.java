package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public void add(Request request) throws SQLException {
        String sql = "INSERT INTO requests (user_id, equipment_id, category_id, urgency, impact, priority_index, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, request.getUserId());
            ps.setInt(2, request.getEquipmentId());
            ps.setInt(3, request.getCategoryId());
            ps.setInt(4, request.getUrgency());
            ps.setInt(5, request.getImpact());
            ps.setDouble(6, request.getPriorityIndex());
            ps.setString(7, request.getStatus().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    request.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Request> getAll() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT id, user_id, equipment_id, category_id, urgency, impact, priority_index, status FROM requests";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        }
        return requests;
    }

    public Request getById(int id) throws SQLException {
        String sql = "SELECT id, user_id, equipment_id, category_id, urgency, impact, priority_index, status FROM requests WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean update(Request request) throws SQLException {
        String sql = "UPDATE requests SET user_id = ?, equipment_id = ?, category_id = ?, " +
                "urgency = ?, impact = ?, priority_index = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getUserId());
            ps.setInt(2, request.getEquipmentId());
            ps.setInt(3, request.getCategoryId());
            ps.setInt(4, request.getUrgency());
            ps.setInt(5, request.getImpact());
            ps.setDouble(6, request.getPriorityIndex());
            ps.setString(7, request.getStatus().name());
            ps.setInt(8, request.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateState(int id, RequestState state) throws SQLException {
        String sql = "UPDATE requests SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, state.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM requests WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Request mapRow(ResultSet rs) throws SQLException {
        return new Request(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("equipment_id"),
                rs.getInt("category_id"),
                rs.getInt("urgency"),
                rs.getInt("impact"),
                rs.getDouble("priority_index"),
                RequestState.valueOf(rs.getString("status"))
        );
    }
}