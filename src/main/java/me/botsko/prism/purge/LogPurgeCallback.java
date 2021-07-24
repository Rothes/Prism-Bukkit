package me.botsko.prism.purge;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;

public class LogPurgeCallback implements PurgeCallback {

    /**
     * Simply log the purges, being done automatically
     */
    @Override
    public void cycle(QueryParameters param, int cycle_rows_affected, int total_records_affected,
                      boolean cycle_complete, long max_cycle_time) {
        Prism.debug("周期数据清理任务清理了 " + cycle_rows_affected + " 行.");
        if (cycle_complete) {
            Prism.log("一共清理了 " + total_records_affected + " 行. 最大周期时间为 " + max_cycle_time + " msec. 使用:"
                    + param.getOriginalCommand());
        }
    }
}