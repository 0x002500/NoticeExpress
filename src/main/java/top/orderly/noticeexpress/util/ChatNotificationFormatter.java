package top.orderly.noticeexpress.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.orderly.noticeexpress.model.Notice;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats notices for display in chat.
 */
public class ChatNotificationFormatter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    /**
     * Formats a notice as chat text.
     * Format: [Publisher] [YYYY/MM/DD HH:mm] [Pinned]
     *         Title
     *         Content (multi-line)
     */
    public static Text formatNotice(Notice notice) {
        MutableText header = Text.literal("[")
                .formatted(Formatting.GRAY)
                .append(Text.literal(notice.getPublisher()).formatted(Formatting.YELLOW))
                .append(Text.literal("] ").formatted(Formatting.GRAY))
                .append(Text.literal("[").formatted(Formatting.GRAY))
                .append(Text.literal(formatTimestamp(notice.getTimestamp())).formatted(Formatting.AQUA))
                .append(Text.literal("]").formatted(Formatting.GRAY));

        if (notice.isPinned()) {
            header.append(Text.literal(" ").formatted(Formatting.GRAY))
                    .append(Text.literal("[").formatted(Formatting.GRAY))
                    .append(Text.literal("PINNED").formatted(Formatting.RED, Formatting.BOLD))
                    .append(Text.literal("]").formatted(Formatting.GRAY));
        }

        MutableText title = Text.literal("\n")
                .append(Text.literal(notice.getTitle()).formatted(Formatting.GOLD, Formatting.BOLD));

        MutableText content = Text.literal("\n")
                .append(Text.literal(notice.getContent()).formatted(Formatting.WHITE));

        return header.append(title).append(content);
    }

    /**
     * Formats a notice as a simple chat message for list display.
     */
    public static Text formatNoticeListItem(Notice notice, int index) {
        String timestamp = formatTimestamp(notice.getTimestamp());
        
        MutableText text = Text.literal(String.format("%d. ", index))
                .formatted(Formatting.GRAY)
                .append(Text.literal("[" + notice.getPublisher() + "] ").formatted(Formatting.YELLOW))
                .append(Text.literal(notice.getTitle()).formatted(Formatting.GOLD));

        if (notice.isPinned()) {
            text.append(Text.literal(" [PINNED]").formatted(Formatting.RED));
        }

        return text;
    }

    /**
     * Formats a timestamp to string.
     */
    private static String formatTimestamp(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }
}