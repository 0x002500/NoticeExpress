package top.orderly.noticeexpress.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import top.orderly.noticeexpress.NoticeExpress;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Configuration management for the mod.
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig instance;

    private String serverTitle = "Server Announcements";
    private String databasePath = "config/noticeexpress/notices.db";

    private ModConfig() {
    }

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    /**
     * Loads configuration from file.
     */
    public void load(File configFile) {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    this.serverTitle = loaded.serverTitle;
                    this.databasePath = loaded.databasePath;
                }
                NoticeExpress.LOGGER.info("Configuration loaded from: {}", configFile.getPath());
            } catch (IOException e) {
                NoticeExpress.LOGGER.error("Failed to load configuration", e);
            }
        } else {
            NoticeExpress.LOGGER.info("Configuration file not found, using defaults");
        }
    }

    /**
     * Saves configuration to file.
     */
    public void save(File configFile) {
        try {
            // Ensure parent directory exists
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(this, writer);
                NoticeExpress.LOGGER.info("Configuration saved to: {}", configFile.getPath());
            }
        } catch (IOException e) {
            NoticeExpress.LOGGER.error("Failed to save configuration", e);
        }
    }

    public String getServerTitle() {
        return serverTitle;
    }

    public void setServerTitle(String serverTitle) {
        this.serverTitle = serverTitle;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }
}