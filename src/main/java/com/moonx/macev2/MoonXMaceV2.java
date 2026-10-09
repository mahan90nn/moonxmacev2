package com.moonx.macev2;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.Listener;
import org.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org Sound;

import java.io.File;

public final class MoonXMaceV2 extends JavaPlugin {

    private Config config;
    private Settings settings;
    private MiniMessageUtil miniMessageUtil;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        config = new Config(new File(getDataFolder(), "config.yml"));
        if (!config.getFile().exists()) {
            saveResource("config.yml", false);
            config.reload();
        }
        settings = new Settings();
        settings.load(config);
        miniMessageUtil = new MiniMessageUtil(settings.minimessageParse);

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);

        getLogger().info("MoonXMaceV2 has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MoonXMaceV2 has been disabled!");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (config != null) {
            config.reload();
            settings.load(config);
        }
    }

    public Config getConfigManager() {
        return config;
    }

    public Settings getSettings() {
        return settings;
    }

    public MiniMessageUtil getMiniMessageUtil() {
        return miniMessageUtil;
    }

    public void handleMaceDamage(org.bukkit.entity.Player attacker,
                                 org.bukkit.entity.Player target,
                                 double damage) {
        if (!settings.enabled || !settings.actionbarEnabled) return;

        String damageStr = formatDamage(damage);

        String message = settings.actionbarMessage
                .replace("{damage}", damageStr)
                .replace("{player}", target.getName())
                .replace("{attacker}", attacker.getName());

        String coloredMessage = settings.colorMain + message;

        net.kyori.adventure.text.Component component = miniMessageUtil.deserialize(coloredMessage);

        if (settings.actionbarBroadcast) {
            getServer().getAllPlayers().forEach(p -> p.sendActionbar(component));
        } else {
            attacker.sendActionbar(component);
        }

        if (settings.soundEnabled) {
            try {
                Sound sound = Sound.valueOf(settings.soundName);
                attacker.playSound(attacker.getLocation(), sound, (float) settings.soundVolume, (float) settings.soundPitch);
            } catch (Exception e) {
                // Invalid sound name, ignore
            }
        }
    }

    private String formatDamage(double damage) {
        String format;
        if (settings.damageDecimals <= 0) {
            format = "%.0f";
        } else {
            format = "%." + settings.damageDecimals + "f";
        }
        String number = String.format(format, damage);
        return settings.damagePrefix + number + settings.damageSuffix;
    }

    private static final class DamageListener implements Listener {

        private final MoonXMaceV2 plugin;

        DamageListener(MoonXMaceV2 plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onEntityDamage(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof org.bukkit.entity.Player)) return;
            if (!(event.getDamager() instanceof org.bukkit.entity.Player)) return;

            org.bukkit.entity.Player target = (org.bukkit.entity.Player) event.getEntity();
            org.bukkit.entity.Player attacker = (org.bukkit.entity.Player) event.getDamager();

            org.bukkit.inventory.ItemStack mainHand = attacker.getInventory().getItemInMainHand();
            if (mainHand == null || !isMace(mainHand)) return;

            plugin.handleMaceDamage(attacker, target, event.getDamage());
        }

        private boolean isMace(org.bukkit.inventory.ItemStack item) {
            if (item == null) return false;
            return item.getType() == ItemType.MACE;
        }
    }
}