package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.SubHandler;
import me.botsko.prism.events.BlockStateChange;
import me.botsko.prism.events.PrismBlocksExtinguishEvent;
import me.botsko.prism.utils.TypeUtils;
import me.botsko.prism.utils.block.Utilities;

import java.util.ArrayList;
import java.util.List;

public class ExtinguishCommand implements SubHandler {

    private final Prism plugin;

    /**
     * Constructor.
     * @param plugin Prism
     */
    public ExtinguishCommand(Prism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command.
     */
    @Override
    public void handle(CallInfo call) {

        int radius = plugin.getConfig().getInt("prism.ex.default-radius");
        if (call.getArgs().length == 2) {
            if (TypeUtils.isNumeric(call.getArg(1))) {
                final int _tmp_radius = Integer.parseInt(call.getArg(1));
                if (_tmp_radius > 0) {
                    if (_tmp_radius > plugin.getConfig().getInt("prism.ex.max-radius")) {
                        call.getPlayer().sendMessage(Prism.messenger.playerError("半径超过了配置中设定的最大值."));
                        return;
                    } else {
                        radius = _tmp_radius;
                    }
                } else {
                    call.getPlayer().sendMessage(Prism.messenger.playerError(
                            "半径必须大于0. 或者忽略它以使用默认值.  使用 '/prism ?' 获取帮助."));
                    return;
                }
            } else {
                call.getPlayer().sendMessage(Prism.messenger.playerError(
                        "半径必须为一个数值. 或者忽略它以使用默认值. 使用 '/prism ?' 获取帮助."));
                return;
            }
        }

        final ArrayList<BlockStateChange> blockStateChanges = Utilities.extinguish(call.getPlayer().getLocation(),
                radius);
        if (!blockStateChanges.isEmpty()) {

            call.getPlayer().sendMessage(Prism.messenger.playerHeaderMsg("周围的火都被扑灭了! 真凉快!"));

            // Trigger the event
            final PrismBlocksExtinguishEvent event = new PrismBlocksExtinguishEvent(blockStateChanges, call.getPlayer(),
                    radius);
            plugin.getServer().getPluginManager().callEvent(event);

        } else {
            call.getPlayer()
                    .sendMessage(Prism.messenger.playerError("没有在这个半径内找到任何可扑灭的东西."));
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }
}