package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.sql.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * The real {@link RedactionResultDAO} backed by SQLite.
 *
 * <p>Handles the redaction_results table. The tricky bit here is the map of
 * "how many of each sensitive data type were found" - SQLite can't store a
 * Map directly, so we squash it into a single text string when saving and
 * unpack it again when loading. The serialise/deserialise helpers do that, and
 * they're package-private so other DAOs (like the risk analysis one) can reuse
 * the same format.</p>
 */
public class SqliteRedactionResultDAO implements RedactionResultDAO {
    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteRedactionResultDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteRedactionResultDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a redaction result, serialising the type counts map into text
     * first, then reads back the generated result ID.
     *
     * @param r the result to add
     */
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

    /**
     * Gets the redaction result for a given prompt, unpacking the type counts
     * text back into a map as it builds the object.
     *
     * @param promptId the ID of the prompt
     * @return the result for that prompt, or null if there isn't one
     */
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

    /**
     * Deletes the redaction result tied to a prompt.
     *
     * @param promptId the ID of the prompt whose result we want removed
     */
    @Override
    public void deleteByPromptId(int promptId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM redaction_results WHERE promptId = ?");
            stmt.setInt(1, promptId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Turns a map of data-type counts into a single string we can store in one
     * column. The format is "TYPE=count" pairs separated by semicolons, e.g.
     * "EMAIL=2;PHONE=1". An empty or null map just becomes an empty string.
     *
     * @param counts the map of how many of each type were detected
     * @return the serialised string version of that map
     */
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

    /**
     * The opposite of {@link #serialise} - takes the stored string and rebuilds
     * the map. It splits on semicolons, then on '=', and quietly ignores
     * anything that doesn't parse properly so one bad entry won't break the lot.
     *
     * @param s the serialised string (may be null or empty)
     * @return the rebuilt map of data-type counts (empty if there was nothing)
     */
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
