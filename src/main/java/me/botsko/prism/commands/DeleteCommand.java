package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.appliers.PrismProcessType;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.PreprocessArgs;
import me.botsko.prism.purge.PurgeTask;
import me.botsko.prism.purge.SenderPurgeCallback;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeleteCommand extends AbstractCommand {

    private final Prism plugin;
    private BukkitTask deleteTask;

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public DeleteCommand(Prism plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(final CallInfo call) {

        // Allow for canceling tasks
        if (call.getArgs().length > 1 && (call.getArg(1).equals("cancel") || call.getArg(1).equals("取消"))) {
            if (plugin.getPurgeManager().deleteTask != null) {
                plugin.getPurgeManager().deleteTask.cancel();
                call.getSender().sendMessage(Prism.messenger.playerMsg("已取消目前的数据清理任务."));
            } else {
                call.getSender().sendMessage(Prism.messenger.playerError("目前没有运行中的数据清理任务."));
            }
            return;
        }

        // Allow for wiping live queue
        if (call.getArgs().length > 1 && (call.getArg(1).equals("queue") || call.getArg(1).equals("队列"))) {
            if (RecordingQueue.getQueue().size() > 0) {
                Prism.log("用户 " + call.getSender().getName()
                        + " 在将实时队列写入数据库之前擦除了它们. "
                        + RecordingQueue.getQueue().size() + " 个事件已丢失.");
                RecordingQueue.getQueue().clear();
                call.getSender().sendMessage(Prism.messenger.playerSuccess("队列中未写入的数据已清除."));
            } else {
                call.getSender().sendMessage(Prism.messenger.playerError("事件队列为空, 没有可以擦除的数据."));
            }
            return;
        }

        // Process and validate all of the arguments
        final QueryParameters parameters = PreprocessArgs.process(plugin, call.getSender(), call.getArgs(),
                PrismProcessType.DELETE, 1, !plugin.getConfig().getBoolean("prism.queries.never-use-defaults"));
        if (parameters == null) {
            return;
        }
        parameters.setStringFromRawArgs(call.getArgs(), 1);

        StringBuilder defaultsReminder = checkIfDefaultUsed(parameters);
        if (parameters.getFoundArgs().size() > 0) {

            call.getSender().sendMessage(Prism.messenger.playerSubduedHeaderMsg("正在清理数据..." + defaultsReminder));
            call.getSender().sendMessage(Prism.messenger
                    .playerHeaderMsg("正在开始周期数据清理." + ChatColor.GRAY + " 没有人会知道..."));

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                int purgeTickDelay = plugin.getConfig().getInt("prism.purge.batch-tick-delay");
                if (purgeTickDelay < 1) {
                    purgeTickDelay = 20;
                }

                // build callback
                final SenderPurgeCallback callback = new SenderPurgeCallback();
                callback.setSender(call.getSender());

                // add to an arraylist so we're consistent
                final CopyOnWriteArrayList<QueryParameters> paramList = new CopyOnWriteArrayList<>();
                paramList.add(parameters);

                final ActionsQuery aq = new ActionsQuery(plugin);
                final long[] extents = aq.getQueryExtents(parameters);
                final long minId = extents[0];
                final long maxId = extents[1];
                Prism.log(
                        "正在进行 Prism 周期数据库数据清理. 清理将分批进行, 因此我们不会占用数据库...");
                deleteTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                        new PurgeTask(plugin, paramList, purgeTickDelay, minId, maxId, callback));
            });
        } else {
            call.getSender().sendMessage(Prism.messenger.playerError("您必须提供至少一个参数."));
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return PreprocessArgs.complete(call.getSender(), call.getArgs());
    }
}