/*
 * @author MJ
 * Created in 25.08.2018
 * Copyright (c) 2017 - 2018 by MJ. All rights reserved.
 *
 */

package de.mj.BattleBuild.core;

import de.mj.BattleBuild.core.utils.Data;
import de.mj.BattleBuild.core.utils.HookManager;
import de.mj.BattleBuild.core.utils.ServerManager;
import de.mj.BattleBuild.core.utils.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {


    private Core core;
    private ConsoleCommandSender sender;
    private ServerManager serverManager;
    private HookManager hookManager;

    private String prefix = new Data().getPrefix();

    @Override
    public void onEnable() {
        setSender(Bukkit.getConsoleSender());

        sender.sendMessage(prefix + "§eis starting...");
        hookManager = new HookManager(this);
        setCore(this);

        sender.sendMessage(prefix + "§edetec server version...");
        sender.sendMessage(prefix + "§adetected server §6" + Bukkit.getServerName());
        preInit();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        sender.sendMessage(prefix + "§awas successfully started!");
    }

    public void onDisable() {
        serverManager.stopServer();
    }

    private void preInit() {
        String server = Bukkit.getServerName();
        sender.sendMessage(prefix + "§eload hooks and modules for Server " + server + " ...");
        if (server.contains("Lobby")) {
            serverManager = new ServerManager(this, ServerType.LOBBY);
            hookManager.hook(ServerType.LOBBY);
            serverManager.init();
            return;
        }
        if (server.equalsIgnoreCase("CityBuild")) {
            serverManager = new ServerManager(this, ServerType.CITY_BUILD);
            hookManager.hook(ServerType.CITY_BUILD);
            serverManager.init();
            return;
        }
        if (server.equalsIgnoreCase("SkyPvP")) {
            serverManager = new ServerManager(this, ServerType.SKY_PVP);
            hookManager.hook(ServerType.SKY_PVP);
            serverManager.init();
            return;
        }
        if (server.equalsIgnoreCase("BauServer")) {
            serverManager = new ServerManager(this, ServerType.BAU_SERVER);
            hookManager.hook(ServerType.BAU_SERVER);
            serverManager.init();

        } else {
            serverManager = new ServerManager(this, ServerType.DEFAULT);
            hookManager.hook(ServerType.DEFAULT);
            serverManager.init();
        }
    }

    public ConsoleCommandSender getSender() {
        return sender;
    }

    private void setSender(ConsoleCommandSender consoleCommandSender) {
        this.sender = consoleCommandSender;
    }

    public void setListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void setCommand(CommandExecutor commandExecutor, String command) {
        getCommand(command).setExecutor(commandExecutor);
    }

    public Core getCore() {
        return core;
    }

    public void setCore(Core core) {
        this.core = core;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }
}
