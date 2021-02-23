package me.botsko.prism.monitors;

import me.botsko.prism.Prism;
import me.botsko.prism.utils.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UseMonitor {
    protected final List<Material> blocksToAlertOnPlace;
    protected final List<Material> blocksToAlertOnBreak;
    private final Prism plugin;
    private ConcurrentHashMap<String, Integer> countedEvents = new ConcurrentHashMap<>();

    /**
     * Constructor.
     *
     * @param plugin Prism
     */
    public UseMonitor(Prism plugin) {
        this.plugin = plugin;
        blocksToAlertOnPlace = plugin.config.alertConfig.uses.monitorItems;
        blocksToAlertOnBreak = plugin.config.alertConfig.uses.breakItems;
        resetEventsQueue();
    }

    protected void incrementCount(String playername, String msg) {

        int count = 0;
        if (countedEvents.containsKey(playername)) {
            count = countedEvents.get(playername);
        }
        count++;
        countedEvents.put(playername, count);
        TextComponent out = Component.text(playername + " " + msg)
                .color(NamedTextColor.GRAY);
        if (count == 5) {
            out.append(Component.text(playername + " continues - pausing warnings.")
                    .color(NamedTextColor.GRAY));
        }
        if (count <= 5) {
            if (plugin.config.alertConfig.uses.logToConsole) {
                plugin.alertPlayers(null, out);
                me.botsko.prism.PrismLogHandler.log(PlainComponentSerializer.plain().serialize(out));
            }

            // Log to commands
            List<String> commands = plugin.config.alertConfig.uses.logCommands;
            MiscUtils.dispatchAlert(msg, commands);
        }
    }

    private boolean checkFeatureShouldCancel(Player player) {

        // Ensure enabled
        if (!plugin.config.alertConfig.uses.enabled) {
            return true;
        }

        // Ignore players who would see the alerts
        if (plugin.config.alertConfig.uses.ignoreStaff
                && player.hasPermission("prism.alerts")) {
            return true;
        }

        // Ignore certain ranks
        return player.hasPermission("prism.bypass-use-alerts");
    }

    /**
     * Alert on block place.
     *
     * @param player Player
     * @param block  Block.
     */
    public void alertOnBlockPlacement(Player player, Block block) {

        // Ensure enabled
        if (checkFeatureShouldCancel(player)) {
            return;
        }

        final String playername = player.getName();
        // Ensure we're tracking this block
        if (blocksToAlertOnPlace.contains(block.getType())) {
            final String alias = Prism.getItems().getAlias(block.getType(), block.getBlockData());
            incrementCount(playername, "placed " + alias);
        }
    }

    /**
     * Alert on break.
     *
     * @param player Player
     * @param block  block.
     */
    public void alertOnBlockBreak(Player player, Block block) {

        // Ensure enabled
        if (checkFeatureShouldCancel(player)) {
            return;
        }

        final String playername = player.getName();
        final String blockType = "" + block.getType();

        // Ensure we're tracking this block
        if (blocksToAlertOnBreak.contains(block.getType())) {
            final String alias = Prism.getItems().getAlias(block.getType(), block.getBlockData());
            incrementCount(playername, "broke " + alias);
        }
    }

    /**
     * Alert on use.
     *
     * @param player Player.
     * @param useMsg msg/
     */
    public void alertOnItemUse(Player player, String useMsg) {

        // Ensure enabled
        if (checkFeatureShouldCancel(player)) {
            return;
        }

        final String playerName = player.getName();
        incrementCount(playerName, useMsg);

    }

    /**
     * Alert on xray.
     *
     * @param player Player
     * @param useMsg message
     */
    public void alertOnVanillaXray(Player player, String useMsg) {

        if (checkFeatureShouldCancel(player)) {
            return;
        }

        final String playerName = player.getName();
        incrementCount(playerName, useMsg);

    }

    private void resetEventsQueue() {
        plugin.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, () -> countedEvents = new ConcurrentHashMap<>(),
                        7000L, 7000L);
    }
}