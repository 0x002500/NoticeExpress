package top.orderly.noticeexpress;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.orderly.noticeexpress.database.DatabaseManager;
import top.orderly.noticeexpress.database.NoticeRepository;

import java.nio.file.Path;

public class NoticeExpress implements ModInitializer {
	public static final String MOD_ID = "noticeexpress";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static NoticeRepository noticeRepository;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing NoticeExpress...");

		// Initialize database
		Path configDir = FabricLoader.getInstance().getConfigDir().resolve("noticeexpress");
		String databasePath = configDir.resolve("notices.db").toString();

		DatabaseManager databaseManager = DatabaseManager.getInstance(databasePath);
		databaseManager.initialize();

		// Initialize repository
		noticeRepository = new NoticeRepository(databaseManager);

		LOGGER.info("NoticeExpress initialized successfully!");
	}

	public static NoticeRepository getNoticeRepository() {
		return noticeRepository;
	}
}
