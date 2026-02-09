package top.orderly.noticeexpress.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.database.NoticeRepository;
import top.orderly.noticeexpress.model.Notice;
import top.orderly.noticeexpress.util.TimeFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /notice list [page] command.
 * Lists notices with pagination (5 per page).
 * Pinned notices always appear first, then sorted by newest first.
 */
public class ListHandler {

    private static final int NOTICES_PER_PAGE = 5;

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        // Get page number (default to 1)
        int page = 1;
        try {
            page = IntegerArgumentType.getInteger(context, "page");
        } catch (IllegalArgumentException e) {
            // No page argument provided, use default
        }

        if (page < 1) {
            source.sendError(Text.literal("✗ Page number must be at least 1.")
                .formatted(Formatting.RED));
            return 0;
        }

        NoticeRepository repository = NoticeExpress.getNoticeRepository();
        List<Notice> allNotices = repository.getAllNotices();

        if (allNotices.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No notices available.")
                .formatted(Formatting.YELLOW), false);
            return 0;
        }

        // Sort: pinned first, then by timestamp descending (newest first)
        List<Notice> sortedNotices = allNotices.stream()
            .sorted(Comparator
                .comparing(Notice::isPinned).reversed()
                .thenComparing(Notice::getTimestamp).reversed())
            .collect(Collectors.toList());

        // Calculate pagination
        int totalNotices = sortedNotices.size();
        int totalPages = (int) Math.ceil((double) totalNotices / NOTICES_PER_PAGE);
        
        if (page > totalPages) {
            source.sendError(Text.literal("✗ Page " + page + " does not exist. Total pages: " + totalPages)
                .formatted(Formatting.RED));
            return 0;
        }

        int startIndex = (page - 1) * NOTICES_PER_PAGE;
        int endIndex = Math.min(startIndex + NOTICES_PER_PAGE, totalNotices);
        List<Notice> pageNotices = sortedNotices.subList(startIndex, endIndex);

        // Display header
        source.sendFeedback(() -> Text.literal("=== Notices (Page " + page + "/" + totalPages + ") ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);

        // Display notices
        for (Notice notice : pageNotices) {
            Text noticeText = formatNoticeListItem(notice);
            source.sendFeedback(() -> noticeText, false);
        }

        // Display pagination controls
        if (totalPages > 1) {
            Text pagination = buildPaginationControls(page, totalPages);
            source.sendFeedback(() -> Text.literal(""), false); // Empty line
            source.sendFeedback(() -> pagination, false);
        }

        return pageNotices.size();
    }

    /**
     * Formats a single notice for list display.
     * Format: [ID] [Publisher] [Time] [Title] [PINNED]
     */
    private static Text formatNoticeListItem(Notice notice) {
        Text idText = Text.literal("[#" + notice.getId() + "] ")
            .formatted(Formatting.DARK_GRAY);

        Text publisherText = Text.literal(notice.getPublisher() + " ")
            .formatted(Formatting.AQUA);

        Text timeText = Text.literal(TimeFormatter.format(notice.getTimestamp()) + " ")
            .formatted(Formatting.GRAY);

        Text titleText = Text.literal(notice.getTitle())
            .formatted(Formatting.WHITE)
            .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notice show " + notice.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    Text.literal("Click to view full notice").formatted(Formatting.YELLOW))));

        Text result = Text.empty()
            .append(idText)
            .append(publisherText)
            .append(timeText)
            .append(titleText);

        if (notice.isPinned()) {
            Text pinnedBadge = Text.literal(" [PINNED]")
                .formatted(Formatting.GOLD, Formatting.BOLD);
            result = result.append(pinnedBadge);
        }

        return result;
    }

    /**
     * Builds pagination controls with clickable page numbers.
     */
    private static Text buildPaginationControls(int currentPage, int totalPages) {
        Text controls = Text.literal("Pages: ").formatted(Formatting.GRAY);

        // Previous button
        if (currentPage > 1) {
            Text prevButton = Text.literal("[<] ")
                .formatted(Formatting.AQUA)
                .styled(style -> style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notice list " + (currentPage - 1)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                        Text.literal("Previous page").formatted(Formatting.YELLOW))));
            controls = controls.append(prevButton);
        } else {
            controls = controls.append(Text.literal("[<] ").formatted(Formatting.DARK_GRAY));
        }

        // Page numbers (show current and nearby pages)
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        for (int i = startPage; i <= endPage; i++) {
            if (i == currentPage) {
                controls = controls.append(Text.literal("[" + i + "] ")
                    .formatted(Formatting.GOLD, Formatting.BOLD));
            } else {
                Text pageButton = Text.literal("[" + i + "] ")
                    .formatted(Formatting.AQUA)
                    .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notice list " + i))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                            Text.literal("Go to page " + i).formatted(Formatting.YELLOW))));
                controls = controls.append(pageButton);
            }
        }

        // Next button
        if (currentPage < totalPages) {
            Text nextButton = Text.literal("[>]")
                .formatted(Formatting.AQUA)
                .styled(style -> style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notice list " + (currentPage + 1)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                        Text.literal("Next page").formatted(Formatting.YELLOW))));
            controls = controls.append(nextButton);
        } else {
            controls = controls.append(Text.literal("[>]").formatted(Formatting.DARK_GRAY));
        }

        return controls;
    }
}