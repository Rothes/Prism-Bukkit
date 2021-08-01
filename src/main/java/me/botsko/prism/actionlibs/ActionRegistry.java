package me.botsko.prism.actionlibs;

import me.botsko.prism.Prism;
import me.botsko.prism.actions.BlockAction;
import me.botsko.prism.actions.BlockChangeAction;
import me.botsko.prism.actions.BlockShiftAction;
import me.botsko.prism.actions.EntityAction;
import me.botsko.prism.actions.EntityTravelAction;
import me.botsko.prism.actions.GrowAction;
import me.botsko.prism.actions.HangingItemAction;
import me.botsko.prism.actions.ItemStackAction;
import me.botsko.prism.actions.PlayerAction;
import me.botsko.prism.actions.PlayerDeathAction;
import me.botsko.prism.actions.PrismProcessAction;
import me.botsko.prism.actions.PrismRollbackAction;
import me.botsko.prism.actions.SignAction;
import me.botsko.prism.actions.UseAction;
import me.botsko.prism.actions.VehicleAction;
import me.botsko.prism.exceptions.InvalidActionException;
import me.botsko.prism.utils.TypeUtils;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ActionRegistry {

    private final TreeMap<String, ActionType> registeredActions = new TreeMap<>();

    public ActionRegistry() {
        registerPrismDefaultActions();
    }

    /**
     * Register a new action type for event recording, lookups, etc.
     *
     * @param actionType type
     */
    private void registerAction(ActionType actionType) {
        registeredActions.put(actionType.getName(), actionType);
    }

    /**
     * Register a new action type for event recording, lookups, etc.  Actions must have a name with
     * 2 hyphens.  And the plugin must be on the allowed list of plugins.
     *
     * @param actionType type
     * @throws InvalidActionException if action not allowed.
     */
    @SuppressWarnings("unused")
    public void registerCustomAction(Plugin apiPlugin, ActionType actionType) throws InvalidActionException {
        final List<String> allowedPlugins = Prism.config.getStringList("prism.tracking.api.allowed-plugins");
        if (!allowedPlugins.contains(apiPlugin.getName())) {
            throw new InvalidActionException("尝试注册不允许的操作类型. 插件 '" + apiPlugin.getName()
                    + "' 不会列入在允许的插件列表中.");
        }
        if (TypeUtils.subStrOccurences(actionType.getName(), "-") != 2) {
            throw new InvalidActionException("未知的操作类型. 自定义操作必须包含2个连接号.");
        }
        Prism.getPrismDataSource().addActionName(actionType.getName());
        registeredActions.put(actionType.getName(), actionType);
    }

    public TreeMap<String, ActionType> getRegisteredAction() {
        return registeredActions;
    }

    public ActionType getAction(String name) {
        return registeredActions.get(name);
    }

    /**
     * Retrieve a list of actions based on a search criteria.
     *
     * @param name to search by
     * @return {@code List<ActionType>}
     */
    public ArrayList<ActionType> getActionsByShortName(String name) {
        final ArrayList<ActionType> actions = new ArrayList<>();
        for (final Entry<String, ActionType> entry : registeredActions.entrySet()) {
            // Match either the name or the short name
            if (entry.getValue().getFamilyName().equals(name) || entry.getValue().getShortName().equals(name)
                    || entry.getValue().getName().equals(name)) {
                actions.add(entry.getValue());
            }
        }
        return actions;
    }

    /**
     * List all.
     *
     * @return list
     */
    public String[] listAll() {
        final String[] names = new String[registeredActions.size()];
        int i = 0;
        for (final Entry<String, ActionType> entry : registeredActions.entrySet()) {
            names[i] = entry.getKey();
            i++;
        }
        return names;
    }

    /**
     * List that allow rollback.
     *
     * @return list
     */
    @SuppressWarnings("unused")
    public ArrayList<String> listActionsThatAllowRollback() {
        final ArrayList<String> names = new ArrayList<>();
        for (final Entry<String, ActionType> entry : registeredActions.entrySet()) {
            if (entry.getValue().canRollback()) {
                names.add(entry.getKey());
            }
        }
        return names;
    }

    /**
     * List of actions that allow restore.
     *
     * @return list
     */
    @SuppressWarnings("unused")
    public ArrayList<String> listActionsThatAllowRestore() {
        final ArrayList<String> names = new ArrayList<>();
        for (final Entry<String, ActionType> entry : registeredActions.entrySet()) {
            if (entry.getValue().canRestore()) {
                names.add(entry.getKey());
            }
        }
        return names;
    }

    private void registerPrismDefaultActions() {

        registerAction(new ActionType("block-break", false, true, true, BlockAction.class, "破坏了"));
        registerAction(new ActionType("block-burn", false, true, true, BlockAction.class, "烧掉了"));
        registerAction(new ActionType("block-dispense", false, false, false, ItemStackAction.class, "发射了"));
        registerAction(new ActionType("block-fade", false, true, true, BlockChangeAction.class, "消亡了"));
        registerAction(new ActionType("block-fall", false, true, true, BlockAction.class, "坠落了"));
        registerAction(new ActionType("block-form", false, true, true, BlockChangeAction.class, "形成了"));
        registerAction(new ActionType("block-place", true, true, true, BlockChangeAction.class, "放置了"));
        registerAction(new ActionType("block-shift", true, false, false, BlockShiftAction.class, "移动了"));
        registerAction(new ActionType("block-spread", true, true, true, BlockChangeAction.class, "传播了"));
        registerAction(new ActionType("block-use", false, false, false, BlockAction.class, "使用了"));
        registerAction(new ActionType("bonemeal-use", false, false, false, UseAction.class, "使用了"));
        registerAction(new ActionType("bucket-fill", false, false, false, PlayerAction.class, "填装了"));
        registerAction(new ActionType("cake-eat", false, false, false, UseAction.class, "吃了"));
        registerAction(new ActionType("container-access", false, false, false, BlockAction.class, "存取了"));
        registerAction(new ActionType("craft-item", false, false, false, ItemStackAction.class, "合成了"));
        registerAction(new ActionType("creeper-explode", false, true, true, BlockAction.class, "炸掉了"));
        registerAction(new ActionType("crop-trample", false, true, true, BlockAction.class, "践踏了"));
        registerAction(new ActionType("dragon-eat", false, true, true, BlockAction.class, "吃掉了"));
        registerAction(new ActionType("enchant-item", false, false, false, ItemStackAction.class, "附魔了"));
        registerAction(new ActionType("enderman-pickup", false, true, true, BlockAction.class, "拾起了"));
        registerAction(new ActionType("enderman-place", true, true, true, BlockAction.class, "放置了"));
        registerAction(new ActionType("entity-break", true, true, true, BlockAction.class, "破坏了"));
        registerAction(new ActionType("entity-dye", false, false, false, EntityAction.class, "染色了"));
        registerAction(new ActionType("entity-explode", false, true, true, BlockAction.class, "炸掉了"));
        registerAction(new ActionType("entity-follow", false, false, false, EntityAction.class, "吸引了"));
        registerAction(new ActionType("entity-form", true, true, true, BlockChangeAction.class, "形成了"));
        registerAction(new ActionType("entity-kill", false, true, false, EntityAction.class, "杀死了"));
        registerAction(new ActionType("entity-leash", true, false, false, EntityAction.class, "拴住了"));
        registerAction(new ActionType("entity-shear", false, false, false, EntityAction.class, "修剪了"));
        registerAction(new ActionType("entity-spawn", false, false, false, EntityAction.class, "生成了"));
        registerAction(new ActionType("entity-unleash", false, false, false, EntityAction.class, "解拴了"));
        registerAction(new ActionType("fireball", false, false, false, BlockAction.class, "点燃了"));
        registerAction(new ActionType("fire-spread", true, true, true, BlockChangeAction.class, "蔓延了"));
        registerAction(new ActionType("firework-launch", false, false, false, ItemStackAction.class, "发射了"));
        registerAction(new ActionType("hangingitem-break", false, true, true, HangingItemAction.class, "破坏了"));
        registerAction(new ActionType("hangingitem-place", true, true, true, HangingItemAction.class, "挂上了"));
        registerAction(new ActionType("item-drop", false, true, true, ItemStackAction.class, "掉落了"));
        registerAction(new ActionType("item-insert", false, true, true, ItemStackAction.class, "放入了"));
        registerAction(new ActionType("item-pickup", false, true, true, ItemStackAction.class, "拾起了"));
        registerAction(new ActionType("item-remove", false, true, true, ItemStackAction.class, "拿出了"));
        registerAction(new ActionType("item-break",false,false,false,ItemStackAction.class,"破坏了"));
        registerAction(new ActionType("item-rotate", false, false, false, UseAction.class, "旋转了物品"));
        registerAction(new ActionType("lava-break", false, true, true, BlockAction.class, "破坏了"));
        registerAction(new ActionType("lava-bucket", true, true, true, BlockChangeAction.class, "倒出了"));
        registerAction(new ActionType("lava-flow", true, true, true, BlockAction.class, "流入了"));
        registerAction(new ActionType("lava-ignite", false, false, false, BlockAction.class, "点燃了"));
        registerAction(new ActionType("leaf-decay", false, true, true, BlockAction.class, "凋落了"));
        registerAction(new ActionType("lighter", false, false, false, BlockAction.class, "点燃了火焰"));
        registerAction(new ActionType("lightning", false, false, false, BlockAction.class, "点燃了"));
        registerAction(new ActionType("mushroom-grow", true, true, true, GrowAction.class, "传播了"));
        registerAction(new ActionType("player-chat", false, false, false, PlayerAction.class, "说了"));
        registerAction(new ActionType("player-command", false, false, false, PlayerAction.class, "执行了指令"));
        registerAction(new ActionType("player-death", false, false, false, PlayerDeathAction.class, "死了"));
        registerAction(new ActionType("player-join", false, false, false, PlayerAction.class, "进入了服务器"));
        registerAction(new ActionType("player-kill", false, true, false, EntityAction.class, "杀死了"));
        registerAction(new ActionType("player-quit", false, false, false, PlayerAction.class, "离开了服务器"));
        registerAction(new ActionType("player-teleport", false, false, false, EntityTravelAction.class, "传送了"));
        registerAction(new ActionType("potion-splash", false, false, false, PlayerAction.class, "掷出了药水"));
        registerAction(new ActionType("prism-drain", false, true, true, PrismRollbackAction.class, "排水了"));
        registerAction(new ActionType("prism-extinguish", false, true, true,
                PrismRollbackAction.class, "扑灭了"));
        registerAction(new ActionType("prism-process", false, false, false, PrismProcessAction.class, "运行了进程"));
        registerAction(new ActionType("prism-rollback", true, false, false, PrismRollbackAction.class, "回滚了"));
        registerAction(new ActionType("sheep-eat", false, false, false, BlockAction.class, "吃掉了"));
        registerAction(new ActionType("sign-change", false, false, true, SignAction.class, "写下了"));
        registerAction(new ActionType("spawnegg-use", false, false, false, UseAction.class, "使用了"));
        registerAction(new ActionType("tnt-explode", false, true, true, BlockAction.class, "炸掉了"));
        registerAction(new ActionType("tnt-prime", false, false, false, UseAction.class, "起爆了"));
        registerAction(new ActionType("tree-grow", true, true, true, GrowAction.class, "生长了"));
        registerAction(new ActionType("vehicle-break", false, true, false, VehicleAction.class, "破坏了"));
        registerAction(new ActionType("vehicle-enter", false, false, false, VehicleAction.class, "进入了"));
        registerAction(new ActionType("vehicle-exit", false, false, false, VehicleAction.class, "离开了"));
        registerAction(new ActionType("vehicle-place", true, false, false, VehicleAction.class, "放置了"));
        registerAction(new ActionType("water-break", false, true, true, BlockAction.class, "破坏了"));
        registerAction(new ActionType("water-bucket", true, true, true, BlockChangeAction.class, "倒出了"));
        registerAction(new ActionType("water-flow", true, true, true, BlockAction.class, "流入了"));
        registerAction(new ActionType("world-edit", true, true, true, BlockChangeAction.class, "编辑了"));
        registerAction(new ActionType("xp-pickup", false, false, false, PlayerAction.class, "拾起了"));
    }
}