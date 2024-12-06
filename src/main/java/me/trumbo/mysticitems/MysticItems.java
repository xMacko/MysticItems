package me.trumbo.mysticitems;

import me.trumbo.mysticitems.commands.MainCommand;
import me.trumbo.mysticitems.commands.TabComplete;
import me.trumbo.mysticitems.listeners.PlayerInteractListener;
import me.trumbo.mysticitems.utils.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MysticItems extends JavaPlugin {

    @Override
    public void onEnable() {

        Metrics metrics = new Metrics(this, 23996);

        saveDefaultConfig();

        getCommand("mitems").setExecutor(new MainCommand(this));
        getCommand("mitems").setTabCompleter(new TabComplete(this));

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);

    }

}
