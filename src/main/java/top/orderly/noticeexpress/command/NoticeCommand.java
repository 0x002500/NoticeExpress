package top.orderly.noticeexpress.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.database.NoticeRepository;
import top.orderly.noticeexpress.model.Notice;
import top.orderly.noticeexpress.util.ChatNotificationFormatter;
import top.orderly.noticeexpress.util.PermissionChecker;

import java.util.List;
import java.util.UUID;

/**
 * Handles all notice-related commands.
 */
public class NoticeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("notice")
                .then(CommandManager.literal("publish")
                        .then(CommandManager.argument("content", StringArgumentType.greedyString())
                                .executes(NoticeCommand::publishNotice)))
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("id", StringArgumentType.string())
                                .executes(NoticeCommand::deleteNotice)))
                .then(CommandManager.literal("pin")
                        .then(CommandManager.argument("id", StringArgumentType.string())
                                .executes(NoticeCommand::pinNotice)))
                .then(CommandManager.literal("unpin")
                        .then(CommandManager.argument("id", StringArgumentType.string())
                                .executes(NoticeCommand::unpinNotice)))
                .then(CommandManager.literal("list")
                        .executes(NoticeCommand::listNotices))
                .then(CommandManager.literal("show")
                        .executes(NoticeCommand::showNotices)));
    }

    private static int publishNotice(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!PermissionChecker.hasManagePermission(source)) {
            source.sendError(Text.literal("You don't have permission to publish notices.").formatted(Formatting.RED));
            return 0;
        }

        String content = StringArgumentType.getString(context, "content");
        String publisher = source.getName();
        UUID publisherUuid = source.getPlayer() != null ? source.getPlayer().getUuid() : UUID.randomUUID();

        Notice notice = new Notice();
        notice.setPublisher(publisher);
        notice.setPublisherUuid(publisherUuid);
        notice.setContent(content);

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        if (repository.createNotice(notice)) {
            source.sendFeedback(() -> Text.literal("Notice published successfully!").formatted(Formatting.GREEN), true);
            
            // Broadcast to all players
            if (source.getServer() != null) {
                source.getServer().getPlayerManager().broadcast(
                        Text.literal("New notice from " + publisher + "!").formatted(Formatting.YELLOW), false);
            }
            
            return 1;
        } else {
            source.sendError(Text.literal("Failed to publish notice.").formatted(Formatting.RED));
            return 0;
        }
    }

    private static int deleteNotice(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!PermissionChecker.hasManagePermission(source)) {
            source.sendError(Text.literal("You don't have permission to delete notices.").formatted(Formatting.RED));
            return 0;
        }

        String idString = StringArgumentType.getString(context, "id");
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            source.sendError(Text.literal("Invalid notice ID format.").formatted(Formatting.RED));
            return 0;
        }

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        if (repository.deleteNotice(id)) {
            source.sendFeedback(() -> Text.literal("Notice deleted successfully!").formatted(Formatting.GREEN), true);
            return 1;
        } else {
            source.sendError(Text.literal("Notice not found or failed to delete.").formatted(Formatting.RED));
            return 0;
        }
    }

    private static int pinNotice(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!PermissionChecker.hasManagePermission(source)) {
            source.sendError(Text.literal("You don't have permission to pin notices.").formatted(Formatting.RED));
            return 0;
        }

        String idString = StringArgumentType.getString(context, "id");
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            source.sendError(Text.literal("Invalid notice ID format.").formatted(Formatting.RED));
            return 0;
        }

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        if (repository.pinNotice(id)) {
            source.sendFeedback(() -> Text.literal("Notice pinned successfully!").formatted(Formatting.GREEN), true);
            return 1;
        } else {
            source.sendError(Text.literal("Notice not found or failed to pin.").formatted(Formatting.RED));
            return 0;
        }
    }

    private static int unpinNotice(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!PermissionChecker.hasManagePermission(source)) {
            source.sendError(Text.literal("You don't have permission to unpin notices.").formatted(Formatting.RED));
            return 0;
        }

        String idString = StringArgumentType.getString(context, "id");
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            source.sendError(Text.literal("Invalid notice ID format.").formatted(Formatting.RED));
            return 0;
        }

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        if (repository.unpinNotice(id)) {
            source.sendFeedback(() -> Text.literal("Notice unpinned successfully!").formatted(Formatting.GREEN), true);
            return 1;
        } else {
            source.sendError(Text.literal("Notice not found or failed to unpin.").formatted(Formatting.RED));
            return 0;
        }
    }

    private static int listNotices(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        List<Notice> notices = repository.getAllNotices();

        if (notices.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No notices available.").formatted(Formatting.YELLOW), false);
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Notices ===").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        for (int i = 0; i < notices.size(); i++) {
            Notice notice = notices.get(i);
            int index = i + 1;
            source.sendFeedback(() -> ChatNotificationFormatter.formatNoticeListItem(notice, index), false);
            source.sendFeedback(() -> Text.literal("  ID: " + notice.getId()).formatted(Formatting.DARK_GRAY), false);
        }

        return notices.size();
    }

    private static int showNotices(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
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