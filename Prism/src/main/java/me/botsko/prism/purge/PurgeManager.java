package me.botsko.prism.purge;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.commandlibs.PreprocessArgs;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PurgeManager implements Runnable {

    private final List<String> purgeRules;
    private final Prism plugin;
    public BukkitTask deleteTask;

    /**
     * Create a purge manager.
     *
     * @param plugin     Prism.
     * @param purgeRules list of rules.
     */
    public PurgeManager(Prism plugin, List<String> purgeRules) {
        this.plugin = plugin;
        this.purgeRules = purgeRules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        Prism.log("数据清理执行器计划任务已新一次开始执行...");

        if (!purgeRules.isEmpty()) {

            final CopyOnWriteArrayList<QueryParameters> paramList = new CopyOnWriteArrayList<>();

            for (final String purgeArgs : purgeRules) {

                // Process and validate all of the arguments
                final QueryParameters parameters = PreprocessArgs.process(plugin, null, purgeArgs.split(" "),
                      PrismProcessType.DELETE, 0, false);

                if (parameters == null) {
                    Prism.log("未知的数据库数据清理参数: " + purgeArgs);
                    continue;
                }

                if (parameters.getFoundArgs().size() > 0) {
                    parameters.setStringFromRawArgs(purgeArgs.split(" "), 0);
                    paramList.add(parameters);
                    Prism.log("数据库数据清理执行参数: " + purgeArgs);
                }
            }

            if (paramList.size() > 0) {


                int purgeTickDelay = plugin.getConfig().getInt("prism.purge.batch-tick-delay");
                if (purgeTickDelay < 1) {
                    purgeTickDelay = 20;
                }

                /*
                  We're going to cycle through the param rules, one rule at a time in a single
                  async task. This task will reschedule itself when each purge cycle has
                  completed and records remain
                 */
                Prism.log(
                        "正在开始 Prism 数据库周期数据清理. "
                                + "清理将分批进行, 因此我们不会占用数据库...");
                deleteTask = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin,
                        new PurgeTask(plugin, paramList, purgeTickDelay, new LogPurgeCallback()),
                        purgeTickDelay);

            }
        } else {
            Prism.log("数据清理规则为空, 不会清理任何数据.");
        }
    }
}