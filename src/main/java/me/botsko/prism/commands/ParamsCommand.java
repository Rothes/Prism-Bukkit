package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.SubHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ParamsCommand implements SubHandler {

    @Override
    public void handle(CallInfo call) {
        help(call.getSender());
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }


    private void help(CommandSender sender) {

        sender.sendMessage(Prism.messenger.playerHeaderMsg(ChatColor.GOLD + "--- 参数帮助 ---"));

        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "r:[半径]" + ChatColor.WHITE
                + " 例如 20, 或 100. 默认值为配置中定义的 default-radius."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "r:(global|全局)" + ChatColor.WHITE
                + " 可以强制在整个世界中搜索, 只能在查询中使用 (除非已为回滚配置了配置文件)."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "r:<玩家|x,y,z>:[半径]"
                + ChatColor.WHITE
                + " 可以基于另一地点来指定半径, 例如 r:<玩家>:20 或 r:20,35,10:5 [x,y,z]格式"));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "r:we" + ChatColor.WHITE
                + " 可以使用 WorldEdit 选区."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.GRAY + "---"));

        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "a:[行为]" + ChatColor.WHITE
                + " 例如 'block-break' (如果需要完整的列表, 请看底部). 没有默认值."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "b:[方块]" + ChatColor.WHITE
                + " 例如 'grass' 或 '2', '2:0'. 没有默认值."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "before:[时长]" + ChatColor.WHITE
                + " 在 x 时长前发生的事件."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "e:[实体]" + ChatColor.WHITE
                + " 例如 'pig'. 没有默认值."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "id:[#]" + ChatColor.WHITE
                + " 记录 ID. 在未开启魔棒时回滚/还原单个项目时很有用."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "k:[文本]" + ChatColor.WHITE
                + " 搜索关键字. 主要用于指令/聊天记录."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "p:[玩家]" + ChatColor.WHITE
                + " 例如 'Rothes'. 没有默认值."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "since:[时长]" + ChatColor.WHITE
                + " 从 x 时长后发生的事件 (等同于 t:)."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "t:[时长]" + ChatColor.WHITE
                + " 从 x 时长后发生的事件. 例如(seconds|秒), 20m(minutes|分), 1h(hour|时), 7d(days|天), 2w(weeks|周). 默认值在配置中设定."));
        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.LIGHT_PURPLE + "w:[世界]" + ChatColor.WHITE
                + " 默认值为当前所处的世界."));
        sender.sendMessage(Prism.messenger.playerMsg("在行为、玩家、或者实体名前加上一个 '!' 可以排除它们.   例如 p:!Rothes"));
        sender.sendMessage(Prism.messenger.playerMsg("在玩家前加上一个 '~' 可以用于部分匹配. 例如 p:~vive"));

        sender.sendMessage(Prism.messenger.playerMsg(ChatColor.GRAY + "使用 " + ChatColor.WHITE + "/pr 行为(actions)"
                + ChatColor.GRAY + " 来查看所有行为的列表."));

    }
}