package top.orderly.noticeexpress.util;

import net.minecraft.server.command.ServerCommandSource;

/**
 * Utility class for checking permissions.
 */
public class PermissionChecker {
    private static final int REQUIRED_OP_LEVEL = 3;

    /**
     * Checks if the command source has permission to manage notices.
     * Requires OP level 3 or higher.
     */
    public static boolean hasManagePermission(ServerCommandSource source) {
        return source.hasPermissionLevel(REQUIRED_OP_LEVEL);
    }

    /**
     * Checks if the command source can view notices.
     * All players can view notices.
     */
    public static boolean hasViewPermission(ServerCommandSource source) {
        return true;
    }
}