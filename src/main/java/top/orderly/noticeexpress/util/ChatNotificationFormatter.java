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

        MutableText content = Text.literal("\n")
                .append(Text.literal(notice.getContent()).formatted(Formatting.WHITE));

        return header.append(content);
    }

    /**
     * Formats a notice as a simple chat message for list display.
     */
    public static Text formatNoticeListItem(Notice notice, int index) {
        String timestamp = formatTimestamp(notice.getTimestamp());
        String preview = notice.getContent().length() > 30
                ? notice.getContent().substring(0, 30) + "..."
                : notice.getContent();

        MutableText text = Text.literal(String.format("%d. ", index))
                .formatted(Formatting.GRAY)
                .append(Text.literal("[" + notice.getPublisher() + "] ").formatted(Formatting.YELLOW))
                .append(Text.literal(preview).formatted(Formatting.WHITE));

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