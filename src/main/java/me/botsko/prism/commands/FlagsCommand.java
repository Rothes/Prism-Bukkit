package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.Flag;
import me.botsko.prism.commandlibs.SubHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FlagsCommand implements SubHandler {

    /**
     * Handle the command.
     */
    @Override
    public void handle(CallInfo call) {
        help(call.getSender());
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    /**
     * Display param help.
     *
     * @param sender CommandSender
     */
    private void help(CommandSender sender) {

        sender.sendMessage(Prism.messenger.playerHeaderMsg(ChatColor.GOLD + "--- 标志帮助 ---"));

        sender.sendMessage(Prism.messenger.playerMsg(
                ChatColor.GRAY + "标志能控制 Prism 如何应用一次回滚/还原, 或者格式化查询结果."));
        sender.sendMessage(Prism.messenger
                .playerMsg(ChatColor.GRAY + "在参数后使用它们, 例如 /pr l p:<玩家名> -扩展参数"));
        for (final Flag flag : Flag.values()) {
            sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + flag.getUsage().replace("_", "-")
                    + ChatColor.WHITE + " " + flag.getDescription()));
        }
    }
}