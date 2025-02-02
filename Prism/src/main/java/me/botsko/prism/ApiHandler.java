package me.botsko.prism;

import au.com.addstar.dripreporter.DripGauge;
import au.com.addstar.dripreporter.DripReporterApi;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.actions.ActionMeter;
import me.botsko.prism.bridge.PrismBlockEditHandler;
import me.botsko.prism.measurement.QueueStats;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created for the Prism Project.
 * Created by Narimm on 20/05/2020.
 */
public class ApiHandler {
    private static final Collection<String> enabledPlugins = new ArrayList<>();
    public static DripReporterApi monitor = null;
    public static WorldEditPlugin worldEditPlugin = null;

    private ApiHandler() {
    }

    /**
     * Setup Drip Reporter.
     */
    static void configureMonitor() {
        Plugin drip = Prism.getInstance().getServer().getPluginManager().getPlugin("DripReporter");
        if (drip != null && drip.isEnabled()) {
            monitor = (DripReporterApi) drip;
            Prism.log("Prism 已挂钩到 DripReporterApi 实例: " + drip.getName() + " "
                    + drip.getDescription().getVersion());
            enabledPlugins.add(drip.getName());
            Prism.getInstance().monitoring = true;
            ActionMeter.setupActionMeter();
            DripGauge<Integer> recordingQ = RecordingQueue::getQueueSize;
            ApiHandler.monitor.addGauge(Prism.class, recordingQ, "RecordingQueueSize");
            DripGauge<Double> batchInsertNum = QueueStats::getPerMinuteInsertAverage;
            ApiHandler.monitor.addGauge(Prism.class, batchInsertNum, "BatchInsertSize");
            DripGauge<Double> batchProcessTime = QueueStats::getPerMinuteBatchProcessAverage;
            ApiHandler.monitor.addGauge(Prism.class, batchProcessTime, "BatchProcessingTime");
            DripGauge<Double> batchBuildAverage = QueueStats::getPerMinuteBatchBuildAverage;
            ApiHandler.monitor.addGauge(Prism.class, batchBuildAverage, "BatchBuildTime");


        }
    }

    static void hookWorldEdit() {
        final Plugin we = Prism.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
        if (we != null) {
            worldEditPlugin = (WorldEditPlugin) we;
            enabledPlugins.add(we.getName());
            // Easier and foolproof way.
            try {
                WorldEdit.getInstance().getEventBus().register(new PrismBlockEditHandler());
                Prism.log("检测到 WorldEdit. 关联的功能已启用.");
            } catch (Throwable error) {
                Prism.log("需要 WorldEdit 7.1.0 或更高的版本!"
                        + " 一些 Prism 的可选功能已禁用.");
                Prism.debug(error.getMessage());
            }

        } else {
            Prism.log("未检测到 WorldEdit. 一些 Prism 的可选功能已禁用.");
        }
    }

    static boolean checkDependency(String pluginName) {
        return ApiHandler.enabledPlugins.contains(pluginName);
    }

    private static boolean disableDripReporterHook() {
        if (monitor != null) {
            monitor.getRegistry().remove("Prism.RecordingQueueSize");
            try {
                enabledPlugins.remove(((Plugin) monitor).getName());
            } catch (ClassCastException ignore) {
                return false;
            }
        }
        monitor = null;
        Prism.getInstance().monitoring = false;
        return true;
    }

    private static boolean disableWorldEditHook() {
        if (worldEditPlugin != null) {
            try {
                WorldEdit.getInstance().getEventBus().unregister(new PrismBlockEditHandler());
                Prism.log("已取消挂钩 WorldEdit");
                enabledPlugins.remove(worldEditPlugin.getName());
                worldEditPlugin = null;
                return true;
            } catch (Throwable error) {
                Prism.log("无法取消挂钩 worldEdit...它是否正常启用???");
                Prism.debug(error.getMessage());
                return false;
            }
        } else {
            return true;
        }
    }

    static boolean disable() {
        return disableDripReporterHook() && disableDripReporterHook();
    }

}
