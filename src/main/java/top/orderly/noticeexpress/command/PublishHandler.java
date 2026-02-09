package top.orderly.noticeexpress.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.database.NoticeRepository;
import top.orderly.noticeexpress.model.Notice;
import top.orderly.noticeexpress.util.PermissionChecker;

import java.util.UUID;

/**
 * Handles the /notice publish <title> <content> command.
 * Publishes a new notice with detailed feedback.
 */
public class PublishHandler {

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Permission check
        if (!PermissionChecker.hasManagePermission(source)) {
            source.sendError(Text.literal("✗ You don't have permission to publish notices.")
                    .formatted(Formatting.RED));
            return 0;
        }

        String title = StringArgumentType.getString(context, "title");
        String content = StringArgumentType.getString(context, "content");
        String publisher = source.getName();
        UUID publisherUuid = source.getPlayer() != null ? 
            source.getPlayer().getUuid() : UUID.randomUUID();

        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setPublisher(publisher);
        notice.setPublisherUuid(publisherUuid);
        notice.setContent(content);

        NoticeRepository repository = NoticeExpress.getNoticeRepository();

        try {
            if (repository.createNotice(notice)) {
                // Success: return notice ID
                source.sendFeedback(() -> 
                    Text.literal("✓ Notice published successfully!")
                        .formatted(Formatting.GREEN)
                        .append(Text.literal("\nNotice ID: " + notice.getId())
                            .formatted(Formatting.GOLD)), 
                    true);

                // Broadcast to all players
                if (source.getServer() != null) {
                    source.getServer().getPlayerManager().broadcast(
                        Text.literal("📢 New notice from " + publisher + ": " + title)
                            .formatted(Formatting.YELLOW), 
                        false);
                }

                return 1;
            } else {
                // Failure: generic error
                source.sendError(Text.literal("✗ Failed to publish notice: Unknown error")
                    .formatted(Formatting.RED));
                return 0;
            }
        } catch (Exception e) {
            // Failure: specific error message
            source.sendError(Text.literal("✗ Failed to publish notice: " + e.getMessage())
                .formatted(Formatting.RED));
            NoticeExpress.LOGGER.error("Failed to publish notice", e);
            return 0;
        }
    }
}