package top.orderly.noticeexpress.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.database.NoticeRepository;
import top.orderly.noticeexpress.util.PermissionChecker;

/**
 * Handles the /notice delete <id> command.
 * Deletes a notice by ID.
 */
public class DeleteHandler {

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        // Permission check
        if (!PermissionChecker.hasManagePermission(source)) {
            source.sendError(Text.literal("✗ You don't have permission to delete notices.")
                    .formatted(Formatting.RED));
            return 0;
        }

        int id = IntegerArgumentType.getInteger(context, "id");

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        
        try {
            if (repository.deleteNotice(id)) {
                source.sendFeedback(() -> 
                    Text.literal("✓ Notice #" + id + " deleted successfully!")
                        .formatted(Formatting.GREEN), 
                    true);
                return 1;
            } else {
                source.sendError(Text.literal("✗ Notice #" + id + " not found.")
                    .formatted(Formatting.RED));
                return 0;
            }
        } catch (Exception e) {
            source.sendError(Text.literal("✗ Failed to delete notice: " + e.getMessage())
                .formatted(Formatting.RED));
            NoticeExpress.LOGGER.error("Failed to delete notice #" + id, e);
            return 0;
        }
    }
}