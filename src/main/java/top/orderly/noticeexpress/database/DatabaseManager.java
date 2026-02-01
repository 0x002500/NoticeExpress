package top.orderly.noticeexpress.database;

import top.orderly.noticeexpress.NoticeExpress;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connection and initialization.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String databasePath;

    private DatabaseManager(String databasePath) {
        this.databasePath = databasePath;
    }

    public static DatabaseManager getInstance(String databasePath) {
        if (instance == null) {
            instance = new DatabaseManager(databasePath);
        }
        return instance;
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized. Call getInstance(String) first.");
        }
        return instance;
    }

    /**
     * Initializes the database connection and creates tables if they don't exist.
     */
    public void initialize() {
        try {
            // Ensure the directory exists
            File dbFile = new File(databasePath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish connection
            String url = "jdbc:sqlite:" + databasePath;
            connection = DriverManager.getConnection(url);

            NoticeExpress.LOGGER.info("Database connection established: {}", databasePath);

            // Create tables
            createTables();

        } catch (ClassNotFoundException e) {
            NoticeExpress.LOGGER.error("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to initialize database", e);
        }
    }

    /**
     * Creates the necessary database tables.
     */
    private void createTables() throws SQLException {
        String createNoticesTable = """
                CREATE TABLE IF NOT EXISTS notices (
                    id TEXT PRIMARY KEY,
                    publisher TEXT NOT NULL,
                    publisher_uuid TEXT NOT NULL,
                    content TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    is_pinned INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createNoticesTable);
            NoticeExpress.LOGGER.info("Database tables created successfully");
        }
    }

    /**
     * Gets the active database connection.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initialize();
            }
        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to check connection status", e);
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                NoticeExpress.LOGGER.info("Database connection closed");
            }
        } catch (SQLException e) {
            NoticeExpress.LOGGER.error("Failed to close database connection", e);
        }
    }
}