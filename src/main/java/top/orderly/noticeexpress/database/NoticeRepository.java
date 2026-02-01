package top.orderly.noticeexpress.database;

import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for CRUD operations on Notice entities.
 */
public class NoticeRepository {
    private final DatabaseManager databaseManager;

    public NoticeRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Creates a new notice in the database.
     */
    public boolean createNotice(Notice notice) {
        String sql = "INSERT INTO notices (id, publisher, publisher_uuid, content, timestamp, is_pinned, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, notice.getId().toString());
            pstmt.setString(2, notice.getPublisher());
            pstmt.setString(3, notice.getPublisherUuid().toString());
            pstmt.setString(4, notice.getContent());
            pstmt.setLong(5, notice.getTimestamp());
            pstmt.setInt(6, notice.isPinned() ? 1 : 0);
            pstmt.setLong(7, notice.getCreatedAt());

            pstmt.executeUpdate();
            NoticeExpress.LOGGER.info("Notice created: {}", notice.getId());
            return true;

        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to create notice", e);
            return false;
        }
    }

    /**
     * Deletes a notice by its ID.
     */
    public boolean deleteNotice(UUID id) {
        String sql = "DELETE FROM notices WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                NoticeExpress.LOGGER.info("Notice deleted: {}", id);
                return true;
            } else {
                NoticeExpress.LOGGER.warn("Notice not found: {}", id);
                return false;
            }

        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to delete notice", e);
            return false;
        }
    }

    /**
     * Pins a notice.
     */
    public boolean pinNotice(UUID id) {
        return updatePinStatus(id, true);
    }

    /**
     * Unpins a notice.
     */
    public boolean unpinNotice(UUID id) {
        return updatePinStatus(id, false);
    }

    /**
     * Updates the pin status of a notice.
     */
    private boolean updatePinStatus(UUID id, boolean pinned) {
        String sql = "UPDATE notices SET is_pinned = ? WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pinned ? 1 : 0);
            pstmt.setString(2, id.toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                NoticeExpress.LOGGER.info("Notice {} {}: {}", pinned ? "pinned" : "unpinned", id);
                return true;
            } else {
                NoticeExpress.LOGGER.warn("Notice not found: {}", id);
                return false;
            }

        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to update pin status", e);
            return false;
        }
    }

    /**
     * Gets all notices, ordered by pinned status and timestamp (newest first).
     */
    public List<Notice> getAllNotices() {
        String sql = "SELECT * FROM notices ORDER BY is_pinned DESC, timestamp DESC";
        List<Notice> notices = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notices.add(mapResultSetToNotice(rs));
            }

            NoticeExpress.LOGGER.debug("Retrieved {} notices", notices.size());

        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to retrieve notices", e);
        }

        return notices;
    }

    /**
     * Gets notices created after a specific timestamp.
     */
    public List<Notice> getNoticesSince(long timestamp) {
        String sql = "SELECT * FROM notices WHERE created_at > ? ORDER BY is_pinned DESC, timestamp DESC";
        List<Notice> notices = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, timestamp);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notices.add(mapResultSetToNotice(rs));
            }

            NoticeExpress.LOGGER.debug("Retrieved {} notices since {}", notices.size(), timestamp);

        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to retrieve notices since timestamp", e);
        }

        return notices;
    }

    /**
     * Gets a notice by its ID.
     */
    public Notice getNoticeById(UUID id) {
        String sql = "SELECT * FROM notices WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNotice(rs);
            }

        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to retrieve notice by ID", e);
        }

        return null;
    }

    /**
     * Maps a ResultSet row to a Notice object.
     */
    private Notice mapResultSetToNotice(ResultSet rs) throws SQLException {
        return new Notice(
                UUID.fromString(rs.getString("id")),
                rs.getString("publisher"),
                UUID.fromString(rs.getString("publisher_uuid")),
                rs.getString("content"),
                rs.getLong("timestamp"),
                rs.getInt("is_pinned") == 1,
                rs.getLong("created_at")
        );
    }
}