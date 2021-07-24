package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.SubHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Objects;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 5/06/2017.
 */
public class PurgeCommand implements SubHandler {

    private final Prism plugin;

    public PurgeCommand(Prism plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CallInfo call) {
        if (call.getArgs().length < 1) {
            call.getSender().sendMessage(
                    Prism.messenger.playerHeaderMsg("Prism" + ChatColor.GRAY + " v"
                            + plugin.getPrismVersion()));
            call.getSender().sendMessage(Prism.messenger.playerSubduedHeaderMsg("数据清理已计划: " + ChatColor.WHITE + plugin.getSchedulePool().getTaskCount()));
            call.getSender().sendMessage(
                    Prism.messenger.playerSubduedHeaderMsg("数据清理进程 : " + ChatColor.WHITE + plugin.getSchedulePool().getCompletedTaskCount()));
            call.getSender().sendMessage(
                    Prism.messenger.playerSubduedHeaderMsg("连接池字符串: " + ChatColor.WHITE + plugin.getSchedulePool().toString()));
        } else {
            if (Objects.equals(call.getArgs()[0], "execute") || Objects.equals(call.getArgs()[1], "执行")) {
                call.getSender().sendMessage(
                        Prism.messenger.playerHeaderMsg("Prism" + ChatColor.GRAY + " 正在执行数据清理进程."));
                Bukkit.getScheduler().runTaskAsynchronously(plugin, plugin.getPurgeManager());
            }
        }

    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }
}
