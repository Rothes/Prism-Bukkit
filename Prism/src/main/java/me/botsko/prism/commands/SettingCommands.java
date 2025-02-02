package me.botsko.prism.commands;

import me.botsko.prism.Il8nHelper;
import me.botsko.prism.actionlibs.RecordingTask;
import me.botsko.prism.commandlibs.CallInfo;

import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/08/2020.
 */
public class SettingCommands extends AbstractCommand {
    @Override
    public void handle(CallInfo call) {
        if (call.getArgs().length > 1) {
            switch (call.getArg(0).toLowerCase()) {
                case "batchsize":
                case "批次大小":
                    int actions = Integer.parseInt(call.getArg(1));
                    RecordingTask.setActionsPerInsert(actions);
                    //todo add some feedback
                    return;
                default:
                    //todo add some feedback
            }
        }
        //todo add feedback
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-settings")};
    }

    @Override
    public String getRef() {
        return "/settings.html";
    }
}
