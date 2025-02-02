package me.botsko.prism.commands;

import me.botsko.prism.Il8nHelper;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionMessage;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.api.actions.Handler;
import me.botsko.prism.api.commands.Flag;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.SubHandler;
import me.botsko.prism.utils.MiscUtils;
import me.botsko.prism.utils.TypeUtils;

import java.util.List;

public class NearCommand implements SubHandler {

    private final Prism plugin;

    /**
     * Constructor.
     * @param plugin Prism
     */
    public NearCommand(Prism plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(final CallInfo call) {

        // Build params
        final QueryParameters parameters = new QueryParameters();
        parameters.setPerPage(plugin.getConfig().getInt("prism.queries.default-results-per-page"));
        parameters.setWorld(call.getPlayer().getWorld().getName());

        // allow a custom near radius
        int radius = plugin.getConfig().getInt("prism.near.default-radius");
        if (call.getArgs().length == 2) {
            if (TypeUtils.isNumeric(call.getArg(1))) {
                final int _tmp_radius = Integer.parseInt(call.getArg(1));
                if (_tmp_radius > 0) {
                    radius = _tmp_radius;
                } else {
                    Prism.messenger.sendMessage(call.getPlayer(), Prism.messenger.playerError(
                            "半径必须大于0. 或者忽略它以使用默认值.  使用 '/prism ?' 获取帮助."));
                    return;
                }
            } else {
                Prism.messenger.sendMessage(call.getPlayer(), Prism.messenger.playerError(
                        "半径必须为一个数值. 或者忽略它以使用默认值. 使用 '/prism ?' 获取帮助."));
                return;
            }
        }

        parameters.setRadius(radius);
        parameters.setMinMaxVectorsFromPlayerLocation(call.getPlayer().getLocation());
        parameters.setLimit(plugin.getConfig().getInt("prism.near.max-results"));

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            final ActionsQuery aq = new ActionsQuery(plugin);
            final QueryResult results = aq.lookup(parameters, call.getPlayer());
            if (!results.getActionResults().isEmpty()) {
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerSubduedHeaderMsg(
                                Il8nHelper.formatMessage("near-result-report", parameters.getRadius())));
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerHeaderMsg(Il8nHelper.formatMessage("lookup-header-message",
                                results.getTotalResults(), 1, results.getTotalPages())));
                final List<Handler> paginated = results.getPaginatedActionResults();
                if (paginated != null) {
                    int resultCount = results.getIndexOfFirstResult();
                    for (final Handler a : paginated) {
                        final ActionMessage am = new ActionMessage(a);
                        if (parameters.hasFlag(Flag.EXTENDED)
                                || plugin.getConfig().getBoolean("prism.messenger.always-show-extended")) {
                            am.showExtended();
                        }
                        am.setResultIndex(resultCount);
                        MiscUtils.sendClickableTpRecord(am, call.getPlayer());
                        resultCount++;
                    }
                    MiscUtils.sendPageButtons(results, call.getPlayer());

                    // Flush timed data
                    plugin.eventTimer.printTimeRecord();

                } else {
                    Prism.messenger.sendMessage(call.getPlayer(), Prism.messenger
                            .playerError("无法在此页码中找到任何东西. 请检查您是否输入了正确的页码."));
                }
            } else {
                Prism.messenger.sendMessage(call.getPlayer(),
                        Prism.messenger.playerError("没有找到任何数据记录."));
            }
        });
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-near")};
    }

    @Override
    public String getRef() {
        return "/lookups.html#near";
    }
}