package me.botsko.prism.serializers.entity;

import com.google.gson.annotations.SerializedName;
import me.botsko.prism.Il8nHelper;
import me.botsko.prism.serializers.items.ItemStackSerializer;
import me.botsko.prism.utils.EntityUtils;
import me.botsko.prism.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class EntitySerializer {
    //@todo remove alternates after 2.1.7 release
    protected Boolean isAdult = null;
    protected Boolean sitting = null;
    
    @SerializedName(value = "entityName", alternate = "entity_name")
    protected String entityName = null;
    
    protected Map<String, ItemStackSerializer> equipment = new HashMap<>();
    
    @SerializedName(value = "customName", alternate = "custom_name")
    protected String customName = null;

    @SerializedName(value = "tamingOwner", alternate = "taming_owner")
    protected String tamingOwner = null;

    protected String newColor = null;

    @SerializedName(value = "customDesc", alternate = "custom_desc")
    protected String customDesc = null;

    public final String getEntityName() {
        return entityName;
    }

    public final String customDesc() {
        return customDesc;
    }

    // Le sigh
    public final void setNewColor(String color) {
        newColor = color;
    }

    private final static transient String NAME_FORMAT = Il8nHelper.getRawMessage("entity-name-format");
    /**
     * Serialize entity.
     * @param entity Entity.
     */
    public final void serialize(Entity entity) {
        entityName = entity.getType().name().toLowerCase();

        // Get custom name
        customName = entity.getCustomName();
        if (entity instanceof LivingEntity) {
            EntityEquipment inv = ((LivingEntity) entity).getEquipment();
            if (inv != null) {
                for (EquipmentSlot slot: EquipmentSlot.values()) {
                    ItemStack s = inv.getItem(slot);
                    if (s.getType() == Material.AIR) {
                        continue;
                    }
                    equipment.put(slot.name(),ItemStackSerializer.createItemStackSerialized(inv.getItem(slot)));
                }
            }
        }
        // Get animal age
        if (entity instanceof Ageable) {
            isAdult = ((Ageable) entity).isAdult();
        }

        // Owner
        if (entity instanceof Tameable) {
            final Tameable mob = (Tameable) entity;
            if (mob.getOwner() != null) {
                tamingOwner = mob.getOwner().getUniqueId().toString();
            } else if (mob.isTamed()) {
                tamingOwner = "-none-";
            }
        }

        // Sitting
        if (entity instanceof Sittable) {
            sitting = ((Sittable) entity).isSitting();
        }

        EntityDamageEvent damageEvent = entity.getLastDamageCause();

        // Saves us the null check
        if (damageEvent instanceof EntityDamageByEntityEvent && !damageEvent.isCancelled()
                && damageEvent.getDamage() > ((LivingEntity) entity).getHealth()) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) damageEvent;

            if (e.getDamager() instanceof Projectile) {
                customDesc = EntityUtils.getCustomProjectileDescription((Projectile) e.getDamager());
            }
        }
        
        serializer(entity);
    }

    protected void serializer(Entity entity) {
    }

    /**
     * Deserialize.
     * @param entity Entity
     */
    public final void deserialize(Entity entity) {
        // Get custom name
        if (customName != null) {
            entity.setCustomName(customName);
        }
        if (entity instanceof LivingEntity) {
            EntityEquipment inv = ((LivingEntity) entity).getEquipment();
            if (inv != null) {
                equipment.forEach(
                      (s, prismItemStack) -> {
                          if (prismItemStack != null) {
                              inv.setItem(EquipmentSlot.valueOf(s), prismItemStack.toBukkit());
                          }
                      });
            }
        }

        // Get animal age
        if (entity instanceof Ageable) {
            final Ageable age = (Ageable) entity;
            if (Boolean.FALSE.equals(isAdult)) {
                age.setBaby();
            } else {
                age.setAdult();
            }
        }
        // Owner
        if (entity instanceof Tameable) {
            ((Tameable) entity).setOwner(EntityUtils.offlineOf(tamingOwner));
        }

        // Sitting
        if (entity instanceof Sittable) {
            ((Sittable) entity).setSitting(Boolean.TRUE.equals(sitting));
        }

        deserializer(entity);
    }

    protected void deserializer(Entity entity) {
    }

    @Override
    public final String toString() {
        String format = NAME_FORMAT;
        if (tamingOwner != null) {
            OfflinePlayer player = EntityUtils.offlineOf(tamingOwner);
            if (player != null) {
                format = format.replace("<owner>",player.getName() + "'s");
            }
        }

        if (Boolean.FALSE.equals(isAdult)) {
            format = format.replace("<isAdult>","baby");
        }
        format = format.replace("<type>",MiscUtils.niceName(entityName));
        format = format.replace("<colour>",MiscUtils.niceName(newColor));
        if (customName != null) {
            format = format.replace("<customName>",customName);
        }
        AtomicReference<String> name = new AtomicReference<>(format);
        niceName(name);
        format = name.get()
                .replace("<isAdult>","")
                .replace("<colour>","")
                .replace("<owner>","")
                .replace("<prefix>","")
                .replace("<suffix>","")
                .replace("<customName>","");
        return format;
    }

    protected void niceName(AtomicReference<String> name) {
    }
}