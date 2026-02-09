package top.orderly.noticeexpress.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.database.NoticeRepository;
import top.orderly.noticeexpress.model.Notice;
import top.orderly.noticeexpress.util.ChatNotificationFormatter;

/**
 * Handles the /notice show <id> command.
 * Shows the full content of a specific notice.
 */
public class ShowHandler {

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int id = IntegerArgumentType.getInteger(context, "id");

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        Notice notice = repository.getNoticeById(id);

        if (notice == null) {
            source.sendError(Text.literal("✗ Notice #" + id + " not found.")
                .formatted(Formatting.RED));
            return 0;
        }

        // Display the full notice
        source.sendFeedback(() -> Text.literal("=== Notice #" + id + " ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);
        source.sendFeedback(() -> Text.literal(""), false); // Empty line
        source.sendFeedback(() -> ChatNotificationFormatter.formatNotice(notice), false);

        return 1;
    }
}