package me.botsko.prism.commands;

import me.botsko.prism.Il8nHelper;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.PreprocessArgs;
import me.botsko.prism.purge.PurgeTask;
import me.botsko.prism.purge.SenderPurgeCallback;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

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
                Prism.messenger.sendMessage(call.getSender(),
                        Prism.messenger.playerMsg(Il8nHelper.getMessage("cancel-purge")));
            } else {
                Prism.messenger.sendMessage(call.getSender(),
                        Prism.messenger.playerError(Il8nHelper.getMessage("no-purge-running")));
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
                Prism.messenger.sendMessage(call.getSender(),
                        Prism.messenger.playerSuccess(Il8nHelper.getMessage("clear-queue")));
            } else {
                Prism.messenger.sendMessage(call.getSender(),
                        Prism.messenger.playerError(
                                Il8nHelper.getMessage("event-queue-clear")));
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

            Prism.messenger.sendMessage(call.getSender(),
                    Prism.messenger.playerSubduedHeaderMsg(Il8nHelper.getMessage("purge-data")
                            .replaceFirstText(Pattern.compile("<defaults>"), builder ->
                                    Component.text()
                                            .content(defaultsReminder.toString()))));
            Prism.messenger.sendMessage(call.getSender(), Prism.messenger
                    .playerHeaderMsg(Il8nHelper.getMessage("start-purge")));
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
            Prism.messenger.sendMessage(call.getSender(),
                    Prism.messenger.playerError(Il8nHelper.getMessage("no-parameter")));
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return PreprocessArgs.complete(call.getSender(), call.getArgs());
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-delete")};
    }

    @Override
    public String getRef() {
        return null;
    }
}