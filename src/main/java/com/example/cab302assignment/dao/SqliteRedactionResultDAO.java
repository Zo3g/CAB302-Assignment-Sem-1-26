package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.sql.*;
import java.util.EnumMap;
import java.util.Map;

public class SqliteRedactionResultDAO implements RedactionResultDAO {
    private final Connection connection;

    public SqliteRedactionResultDAO() { this.connection = DatabaseConnection.getInstance(); }
    public SqliteRedactionResultDAO(Connection connection) { this.connection = connection; }

    @Override
    public void addResult(RedactionResult r) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO redaction_results (promptId, redactedText, totalDetections, typeCountsJson) VALUES (?, ?, ?, ?)"
            );
            stmt.setInt(1, r.getPromptId());
            stmt.setString(2, r.getRedactedText());
            stmt.setInt(3, r.getTotalDetections());
            stmt.setString(4, serialise(r.getTypeCounts()));
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) r.setResultId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    @Override
    public RedactionResult getByPromptId(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM redaction_results WHERE promptId = ?");
            stmt.setInt(1, promptId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                RedactionResult r = new RedactionResult(rs.getString("redactedText"), rs.getInt("totalDetections"), deserialise(rs.getString("typeCountsJson")));
                r.setResultId(rs.getInt("resultId"));
                r.setPromptId(promptId);
                return r;
            }
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    @Override
    public void deleteByPromptId(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM redaction_results WHERE promptId = ?");
            stmt.setInt(1, promptId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    static String serialise(Map<SensitiveDataType, Integer> counts) {
        if (counts == null || counts.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<SensitiveDataType, Integer> e : counts.entrySet()) {
            if (!first) sb.append(';');
            sb.append(e.getKey().name()).append('=').append(e.getValue());
            first = false;
        }
        return sb.toString();
    }

    static Map<SensitiveDataType, Integer> deserialise(String s) {
        Map<SensitiveDataType, Integer> out = new EnumMap<>(SensitiveDataType.class);
        if (s == null || s.isEmpty()) return out;
        for (String pair : s.split(";")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                try {
                    out.put(SensitiveDataType.valueOf(kv[0]), Integer.parseInt(kv[1]));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return out;
    }
}
