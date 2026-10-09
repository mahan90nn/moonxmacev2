package com.moonx.macev2;

import org.jetbrains.annotations.NotNull;

public final class Settings {

    // Plugin
    public boolean enabled;

    // Action bar
    public boolean actionbarEnabled;
    public String actionbarMessage;
    public boolean actionbarBroadcast;

    // Damage
    public int damageDecimals;
    public String damagePrefix;
    public String damageSuffix;

    // Colors
    public String colorMain;
    public String colorDamage;
    public String colorPlayer;
    public String colorAttacker;

    // Sound
    public boolean soundEnabled;
    public String soundName;
    public double soundVolume;
    public float soundPitch;

    // MiniMessage
    public boolean minimessageParse;

    public void load(@NotNull Config config) {
        enabled = config.getBoolean("enabled", true);

        actionbarEnabled = config.getBoolean("actionbar.enabled", true);
        actionbarMessage = config.getString("actionbar.message", "<gold>Mace Damage: <red>{damage}</red> <white>to</white> <yellow>{player}</yellow>");
        actionbarBroadcast = config.getBoolean("actionbar.broadcast", false);

        damageDecimals = config.getInt("damage.decimals", 1);
        damagePrefix = config.getString("damage.prefix", "");
        damageSuffix = config.getString("damage.suffix", "");

        colorMain = config.getString("colors.main", "<gold>");
        colorDamage = config.getString("colors.damage", "<red>");
        colorPlayer = config.getString("colors.player", "<yellow>");
        colorAttacker = config.getString("colors.attacker", "<green>");

        soundEnabled = config.getBoolean("sound.enabled", false);
        soundName = config.getString("sound.name", "ENTITY_PLAYER_HURT");
        soundVolume = config.getDouble("sound.volume", 1.0);
        soundPitch = config.getFloat("sound.pitch", 1.0);

        minimessageParse = config.getBoolean("minimessage.parse", true);
    }
}