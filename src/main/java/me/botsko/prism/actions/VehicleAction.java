package me.botsko.prism.actions;

import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.appliers.ChangeResult;
import me.botsko.prism.appliers.ChangeResultType;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.*;

public class VehicleAction extends GenericAction {
    private String vehicleName;

    /**
     * @param vehicle
     */
    public void setVehicle(Entity vehicle) {

        if (vehicle instanceof CommandMinecart) {
            vehicleName = "命令方块矿车";
        } else if (vehicle instanceof ExplosiveMinecart) {
            vehicleName = "TNT矿车";
        } else if (vehicle instanceof HopperMinecart) {
            vehicleName = "漏斗矿车";
        } else if (vehicle instanceof PoweredMinecart) {
            vehicleName = "动力矿车";
        } else if (vehicle instanceof RideableMinecart) {
            vehicleName = "矿车";
        } else if (vehicle instanceof SpawnerMinecart) {
            vehicleName = "刷怪笼矿车";
        } else if (vehicle instanceof StorageMinecart) {
            vehicleName = "运输矿车";
        } else if (vehicle instanceof Boat) {
            vehicleName = "船";
        } else {
            vehicleName = vehicle.getType().name().toLowerCase();
        }
    }

    /**
     * @return
     */
    @Override
    public String getNiceName() {
        return vehicleName;
    }

    @Override
    public boolean hasExtraData() {
        return vehicleName != null;
    }

    @Override
    public String serialize() {
        return vehicleName;
    }

    @Override
    public void deserialize(String data) {
        vehicleName = data;
    }

    /**
     *
     */
    @Override
    public ChangeResult applyRollback(Player player, QueryParameters parameters, boolean isPreview) {
        Entity vehicle = null;
        switch (vehicleName) {
            case "command block minecart":
                vehicle = getWorld().spawn(getLoc(), CommandMinecart.class);
                break;
            case "命令方块矿车":
            case "动力矿车":
                vehicle = getWorld().spawn(getLoc(), PoweredMinecart.class);
                break;
            case "运输矿车":
                vehicle = getWorld().spawn(getLoc(), StorageMinecart.class);
                break;
            case "TNT矿车":
                vehicle = getWorld().spawn(getLoc(), ExplosiveMinecart.class);
                break;
            case "刷怪笼矿车":
                vehicle = getWorld().spawn(getLoc(), SpawnerMinecart.class);
                break;
            case "漏斗矿车":
                vehicle = getWorld().spawn(getLoc(), HopperMinecart.class);
                break;
            case "矿车":
                vehicle = getWorld().spawn(getLoc(), Minecart.class);
                break;
            case "船":
                vehicle = getWorld().spawn(getLoc(), Boat.class);
                break;
        }
        if (vehicle != null) {
            return new ChangeResult(ChangeResultType.APPLIED, null);
        }
        return new ChangeResult(ChangeResultType.SKIPPED, null);
    }
}
