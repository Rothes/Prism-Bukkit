package me.botsko.prism.commands;

import me.botsko.prism.Prism;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.SubHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand implements SubHandler {

    private final boolean failed;

    public HelpCommand(boolean failed) {
        this.failed = failed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(CallInfo call) {
        help(call.getSender());
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    /**
     * Displays help.
     *
     * @param sender CommandSender
     */
    protected void help(CommandSender sender) {
        if (failed) {
            sender.sendMessage(Prism.messenger.playerHeaderMsg(ChatColor.GOLD + "--- Prism 已关闭 ---"));
            sender.sendMessage(Prism.messenger.playerMsg("Prism 正在运行于 禁用 模式, 由于无法连接到数据库."
                    + "\n 请通过使用 /prism debug 并细读&修复错误的内容来寻求帮助."));
            sender.sendMessage(
                    Prism.messenger.playerSubduedHeaderMsg("Discord: " + ChatColor.WHITE
                            + "https://discord.gg/Y9Qx3V"));
            sender.sendMessage(
                    Prism.messenger.playerSubduedHeaderMsg("维基: " + ChatColor.WHITE
                            + "https://github.com/Rothes/Prism-Bukkit/wiki\n"));
            return;
        }
        sender.sendMessage(Prism.messenger.playerHeaderMsg(ChatColor.GOLD + "--- 基础用法 ---"));

        sender.sendMessage(Prism.messenger.playerHelp("(i|检查)", "切换检查魔棒."));
        sender.sendMessage(Prism.messenger.playerHelp("(l|lookup|查找) (参数)", "搜索数据库."));
        sender.sendMessage(Prism.messenger.playerHelp("(tp|传送) (#|id:#)", "传送到一个查询结果."));
        sender.sendMessage(Prism.messenger.playerHelp("(near|附近)", "找出附近所有的变化."));
        sender.sendMessage(Prism.messenger.playerHelp("(pg|页码) (#|next|下|prev|上)", "浏览查询结果."));
        sender.sendMessage(Prism.messenger.playerHelp("(params|参数)", "列出参数帮助."));
        sender.sendMessage(Prism.messenger.playerHelp("(actions|行为)", "列出所有行为."));
        sender.sendMessage(Prism.messenger.playerHelp("(flags|标志)", "列出可用的应用标志."));
        sender.sendMessage(Prism.messenger.playerHelp("(preview|pv|预览) (rollback|rb|回滚) (参数)", "预览一次回滚操作."));
        sender.sendMessage(Prism.messenger.playerHelp("(preview|pv|预览) (restore|rs|还原) (参数)", "预览一次还原操作."));
        sender.sendMessage(Prism.messenger.playerHelp("(preview|pv|预览) 应用", "应用上一次的预览."));
        sender.sendMessage(Prism.messenger.playerHelp("(preview|pv|预览) 取消", "取消上一次的预览."));
        sender.sendMessage(Prism.messenger.playerHelp("(rollback|rb|回滚) (参数)", "回滚变化."));
        sender.sendMessage(Prism.messenger.playerHelp("(restore|rs|还原) (参数)", "重新应用变化, 即还原变化."));
        sender.sendMessage(Prism.messenger.playerHelp("(w|wand|魔棒) (profile|简介)", "切换简介魔棒."));
        sender.sendMessage(Prism.messenger.playerHelp("(w|wand|魔棒) (rollback|回滚)", "切换回滚魔棒."));
        sender.sendMessage(Prism.messenger.playerHelp("(w|wand|魔棒) (restore|还原)", "切换还原魔棒."));
        sender.sendMessage(Prism.messenger.playerHelp("(w|wand|魔棒) (off|关)", "关闭目前的魔棒."));
        sender.sendMessage(Prism.messenger.playerHelp("(undo|撤销)", "撤销一个排水操作."));
        sender.sendMessage(Prism.messenger.playerHelp("(ex|灭火) (半径)", "在半径内扑灭火焰."));
        sender.sendMessage(Prism.messenger.playerHelp("(drain|排水) (半径)", "在半径内排走水/熔岩."));
        sender.sendMessage(
                Prism.messenger.playerHelp("(delete|删除) (参数)", "基于参数清理数据记录. 此指令没有默认值!"));
        sender.sendMessage(
                Prism.messenger.playerHelp("(setmy wand mode|偏好 魔棒 模式) (空手|物品|方块)", "设置您个人的魔棒模式."));
        sender.sendMessage(
                Prism.messenger.playerHelp("(setmy wand item|偏好 魔棒 物品) (物品 id)", "设置您个人的魔棒 物品/方块 材料."));
        sender.sendMessage(
                Prism.messenger.playerHelp("(resetmy|重置偏好) (魔棒类型)", "重置您的自定义魔棒设定为服务器默认值."));
        sender.sendMessage(Prism.messenger.playerHelp("(rp|report|报告) queue", "显示目前队列的状态统计信息."));
        sender.sendMessage(Prism.messenger.playerHelp("(rp|report|报告) db", "显示基础数据库连接状态."));
        sender.sendMessage(Prism.messenger.playerHelp("(rp|report|报告) sum (方块|行为) (参数)",
                "显示玩家的报告概要"));
        sender.sendMessage(Prism.messenger.playerHelp("(about|关于)", "显示 Prism 鸣谢名单."));
        sender.sendMessage(Prism.messenger.playerHelp("(recorder cancel|记录器 取消)", "关闭记录器. 谨慎使用."));
        sender.sendMessage(Prism.messenger.playerHelp("(recorder start|记录器 开启)", "开启已关闭的记录器."));
        sender.sendMessage(Prism.messenger.playerHelp("(reload|重载)", "重新加载配置文件和语言文件."));

    }
}