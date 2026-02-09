package top.orderly.noticeexpress.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.database.NoticeRepository;
import top.orderly.noticeexpress.model.Notice;
import top.orderly.noticeexpress.util.ChatNotificationFormatter;

import java.util.List;

/**
 * Handles the /notice command (no arguments).
 * Opens GUI if client mod is installed, otherwise shows notices in chat.
 */
public class OpenGuiHandler {

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players.").formatted(Formatting.RED));
            return 0;
        }

        // TODO: Check if client mod is installed
        // For now, fallback to showing notices in chat
        return showNoticesInChat(source);
    }

    private static int showNoticesInChat(ServerCommandSource source) {
        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        List<Notice> notices = repository.getAllNotices();

        if (notices.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No notices available.").formatted(Formatting.YELLOW), false);
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Server Notices ===").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        for (Notice notice : notices) {
            source.sendFeedback(() -> Text.literal(""), false); // Empty line
            source.sendFeedback(() -> ChatNotificationFormatter.formatNotice(notice), false);
        }

        return notices.size();
    }
}