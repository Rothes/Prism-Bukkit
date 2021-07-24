package me.botsko.prism.purge;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import org.bukkit.command.CommandSender;

public class SenderPurgeCallback implements PurgeCallback {

    /**
     *
     */
    private CommandSender sender;

    /**
     * Simply log the purges, being done automatically
     */
    @Override
    public void cycle(QueryParameters param, int cycle_rows_affected, int total_records_affected,
                      boolean cycle_complete, long max_cycle_time) {
        if (sender == null)
            return;
        sender.sendMessage(
                Prism.messenger.playerSubduedHeaderMsg("周期数据清理任务清理了 " + cycle_rows_affected + " 个记录."));
        if (cycle_complete) {
            sender.sendMessage(Prism.messenger.playerHeaderMsg("一共清理了" + total_records_affected + " 个数据. 最大周期时间为 " + max_cycle_time + " msec."));
        }
    }

    /**
     * @param sender
     */
    public void setSender(CommandSender sender) {
        this.sender = sender;
    }
}