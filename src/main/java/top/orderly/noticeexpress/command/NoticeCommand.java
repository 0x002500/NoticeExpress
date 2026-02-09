package top.orderly.noticeexpress.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Main command registration for /notice commands.
 * Delegates execution to individual handler classes for better separation of concerns.
 */
public class NoticeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, 
                                CommandRegistryAccess registryAccess, 
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("notice")
                // /notice - Open GUI or show notices
                .executes(OpenGuiHandler::execute)
                
                // /notice publish <title> <content>
                .then(CommandManager.literal("publish")
                        .then(CommandManager.argument("title", StringArgumentType.string())
                                .then(CommandManager.argument("content", StringArgumentType.greedyString())
                                        .executes(PublishHandler::execute))))
                
                // /notice delete <id>
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .executes(DeleteHandler::execute)))
                
                // /notice pin <id>
                .then(CommandManager.literal("pin")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .executes(PinHandler::execute)))
                
                // /notice unpin <id>
                .then(CommandManager.literal("unpin")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .executes(UnpinHandler::execute)))
                
                // /notice list [page]
                .then(CommandManager.literal("list")
                        .executes(ListHandler::execute)
                        .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                                .executes(ListHandler::execute)))
                
                // /notice show <id>
                .then(CommandManager.literal("show")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .executes(ShowHandler::execute))));
    }
}