package me.botsko.prism.wands;

import me.botsko.prism.Prism;
import me.botsko.prism.utils.block.Utilities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProfileWand extends WandBase {

    /**
     *
     */
    @Override
    public void playerLeftClick(Player player, Location loc) {
        if (loc != null) {
            showLocationProfile(player, loc);
        }
    }

    /**
     *
     */
    @Override
    public void playerRightClick(Player player, Location loc) {
        if (loc != null) {
            showLocationProfile(player, loc);
        }
    }

    /**
     * @param player
     * @param loc
     */
    protected void showLocationProfile(Player player, Location loc) {

        final Block block = loc.getBlock();

        player.sendMessage(Prism.messenger.playerHeaderMsg("坐标简介"));

        BlockData data = block.getBlockData();

        player.sendMessage(Prism.messenger.playerMsg("名称: " + block.getType().toString().toLowerCase()));
        player.sendMessage(Prism.messenger.playerMsg("别名: " + Prism.getItems().getAlias(block.getType(), data)));
        player.sendMessage(Prism.messenger.playerMsg("ID: " + block.getType() + " " + Utilities.dataString(data)));
        player.sendMessage(
                Prism.messenger.playerMsg("坐标: " + block.getX() + " " + block.getY() + " " + block.getZ()));

    }

    /**
     *
     */
    @Override
    public void playerRightClick(Player player, Entity entity) {
        if (entity != null) {
            player.sendMessage(Prism.messenger.playerHeaderMsg("实体简介"));
            player.sendMessage(Prism.messenger.playerMsg("名称: " + entity.getType().toString().toLowerCase()));
            player.sendMessage(Prism.messenger.playerMsg("ID: " + entity.getEntityId()));
            player.sendMessage(Prism.messenger.playerMsg("坐标: " + entity.getLocation().getBlockX() + " "
                    + entity.getLocation().getBlockY() + " " + entity.getLocation().getBlockZ()));
        }
    }
}