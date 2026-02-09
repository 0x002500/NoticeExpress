package top.orderly.noticeexpress.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
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
        final int finalPage = page;
        final int finalTotalPages = totalPages;
        source.sendFeedback(() -> Text.literal("=== Notices (Page " + finalPage + "/" + finalTotalPages + ") ===")
            .formatted(Formatting.GOLD, Formatting.BOLD), false);

        // Display notices
        for (Notice notice : pageNotices) {
            MutableText noticeText = formatNoticeListItem(notice);
            source.sendFeedback(() -> noticeText, false);
        }

        // Display pagination controls
        if (totalPages > 1) {
            MutableText pagination = buildPaginationControls(page, totalPages);
            source.sendFeedback(() -> Text.literal(""), false); // Empty line
            source.sendFeedback(() -> pagination, false);
        }

        return pageNotices.size();
    }

    /**
     * Formats a single notice for list display.
     * Format: [ID] [Publisher] [Time] [Title] [PINNED]
     */
    private static MutableText formatNoticeListItem(Notice notice) {
        MutableText result = Text.literal("[#" + notice.getId() + "] ")
            .formatted(Formatting.DARK_GRAY)
            .append(Text.literal(notice.getPublisher() + " ").formatted(Formatting.AQUA))
            .append(Text.literal(TimeFormatter.formatDateTime(notice.getTimestamp()) + " ").formatted(Formatting.GRAY))
            .append(Text.literal(notice.getTitle()).formatted(Formatting.WHITE));

        if (notice.isPinned()) {
            result.append(Text.literal(" [PINNED]").formatted(Formatting.GOLD, Formatting.BOLD));
        }

        return result;
    }

    /**
     * Builds pagination controls with page numbers.
     * Note: Interactive click events will be added in a future update.
     */
    private static MutableText buildPaginationControls(int currentPage, int totalPages) {
        MutableText controls = Text.literal("Pages: ").formatted(Formatting.GRAY);

        // Previous button
        if (currentPage > 1) {
            controls = controls.append(Text.literal("[<] ").formatted(Formatting.AQUA));
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
                controls = controls.append(Text.literal("[" + i + "] ").formatted(Formatting.AQUA));
            }
        }

        // Next button
        if (currentPage < totalPages) {
            controls = controls.append(Text.literal("[>]").formatted(Formatting.AQUA));
        } else {
            controls = controls.append(Text.literal("[>]").formatted(Formatting.DARK_GRAY));
        }

        return controls;
    }
}