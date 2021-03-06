package main.de.mj.bb.core;

import main.de.mj.bb.core.gameapi.GameAPI;
import main.de.mj.bb.core.managers.HookManager;
import main.de.mj.bb.core.managers.ModuleManager;
import main.de.mj.bb.core.managers.MongoManager;
import main.de.mj.bb.core.sql.ColumnType;
import main.de.mj.bb.core.utils.BanProcess;
import main.de.mj.bb.core.utils.Data;
import main.de.mj.bb.core.utils.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/*
 * @author MJ
 * Created in 25.08.2018
 * Copyright (c) 2017 - 2018 by MJ. All rights reserved.
 */

public class CoreSpigot extends JavaPlugin {

    private CoreSpigot coreSpigot;
    private static CoreSpigot instance;
    private GameAPI gameAPI;
    private ColumnType columnType;
    private ConsoleCommandSender sender;
    private ModuleManager moduleManager;
    private HookManager hookManager;
    private MongoManager mongoManager;

    private String prefix = new Data().getPrefix();

    public static CoreSpigot getInstance() {
        return instance;
    }

    public void setListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void setCommand(CommandExecutor commandExecutor, String command) {
        getCommand(command).setExecutor(commandExecutor);
    }

    private void infoScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage("§6  ____        _   _   _      ____        _ _     _         _____               ");
                sender.sendMessage("§6 |  _ \\      | | | | | |    |  _ \\      (_) |   | |       / ____|              ");
                sender.sendMessage("§6 | |_) | __ _| |_| |_| | ___| |_) |_   _ _| | __| |______| |     ___  _ __ ___ ");
                sender.sendMessage("§6 |  _ < / _` | __| __| |/ _ \\  _ <| | | | | |/ _` |______| |    / _ \\| '__/ _ \\");
                sender.sendMessage("§6 | |_) | (_| | |_| |_| |  __/ |_) | |_| | | | (_| |      | |___| (_) | | |  __/");
                sender.sendMessage("§6 |____/ \\__,_|\\__|\\__|_|\\___|____/ \\__,_|_|_|\\__,_|       \\_____\\___/|_|  \\___|");
                sender.sendMessage("§8§m---------------§8[§9ServerInfo§8]§8§m---------------§r");
                long cram = Runtime.getRuntime().freeMemory() / 1048576L;
                long mram = Runtime.getRuntime().maxMemory() / 1048576L;
                List<String> names = new ArrayList<>();
                for (Player all : Bukkit.getOnlinePlayers()) names.add(all.getName());
                sender.sendMessage("§9§lCurrent RAM-Usage: §a" + cram + " MB §9 of §3" + mram + " MB " + "(" + mram / 100 * cram / 100 + "%)");
                sender.sendMessage("§e§lPort: §6" + Bukkit.getPort());
                sender.sendMessage("§bServername: §3" + Bukkit.getServerName());
                sender.sendMessage("§5§lVersion: §b" + Bukkit.getBukkitVersion());
                sender.sendMessage("§6§lPlugin-Version: §e" + coreSpigot.getDescription().getVersion());
                sender.sendMessage("§4§lTPS: §c" + moduleManager.getTicksPerSecond().getTPS());
                sender.sendMessage("§6§lOnline: §e" + getOnlinePlayersNameList(names));
                try {
                    sender.sendMessage("§9§lCPU-Load: §a" + getProcessCpuLoad());
                } catch (Exception ignored) {
                }
                sender.sendMessage("§8§m--------------------------------------------§r");
            }
        }.runTaskTimer(this, 0L, 20L * 60 * 5);
    }

    private String getOnlinePlayersNameList(List<String> names) {
        String finalList = "";
        for (String name : names) {
            finalList += " §e" + name;
        }
        return finalList;
    }

    public static void setInstance(CoreSpigot instance) {
        CoreSpigot.instance = instance;
    }

    private void preInit() {
        String server = Bukkit.getServerName();
        sender.sendMessage(prefix + "§eload hooks and modules for Server " + server + " ...");
        this.mongoManager = new MongoManager();
        this.mongoManager.connect();
        if (server.contains("Lobby")) {
            moduleManager = new ModuleManager(this, ServerType.LOBBY);
            hookManager.hook(ServerType.LOBBY);
            moduleManager.init();
            return;
        }
        if (server.contains("BedWars")) {
            moduleManager = new ModuleManager(this, ServerType.BED_WARS);
            hookManager.hook(ServerType.BED_WARS);
            moduleManager.init();
            return;
        }
        if (server.equalsIgnoreCase("CityBuild")) {
            moduleManager = new ModuleManager(this, ServerType.CITY_BUILD);
            hookManager.hook(ServerType.CITY_BUILD);
            moduleManager.init();
            return;
        }
        if (server.equalsIgnoreCase("SkyPvP")) {
            moduleManager = new ModuleManager(this, ServerType.SKY_PVP);
            hookManager.hook(ServerType.SKY_PVP);
            moduleManager.init();
            return;
        }
        if (server.equalsIgnoreCase("BauServer")) {
            moduleManager = new ModuleManager(this, ServerType.BAU_SERVER);
            hookManager.hook(ServerType.BAU_SERVER);
            moduleManager.init();
            return;
        }
        if (server.equalsIgnoreCase("Vorbauen")) {
            moduleManager = new ModuleManager(this, ServerType.VORBAUEN);
            hookManager.hook(ServerType.DEFAULT);
            moduleManager.init();
            return;
        }
        if (server.equalsIgnoreCase("PlguinTestServer")) {
            moduleManager = new ModuleManager(this, ServerType.PLUGIN_TEST_SERVER);
            hookManager.hook(ServerType.DEFAULT);
            moduleManager.init();
            return;
        }
        moduleManager = new ModuleManager(this, ServerType.DEFAULT);
        hookManager.hook(ServerType.DEFAULT);
        moduleManager.init();
    }

    @Override
    public void onDisable() {
        moduleManager.stopServer();
    }

    @Override
    public void onEnable() {
        setCoreSpigot(this);
        setSender(Bukkit.getConsoleSender());
        setInstance(this);

        sender.sendMessage("§6  ____        _   _   _      ____        _ _     _         _____               ");
        sender.sendMessage("§6 |  _ \\      | | | | | |    |  _ \\      (_) |   | |       / ____|              ");
        sender.sendMessage("§6 | |_) | __ _| |_| |_| | ___| |_) |_   _ _| | __| |______| |     ___  _ __ ___ ");
        sender.sendMessage("§6 |  _ < / _` | __| __| |/ _ \\  _ <| | | | | |/ _` |______| |    / _ \\| '__/ _ \\");
        sender.sendMessage("§6 | |_) | (_| | |_| |_| |  __/ |_) | |_| | | | (_| |      | |___| (_) | | |  __/");
        sender.sendMessage("§6 |____/ \\__,_|\\__|\\__|_|\\___|____/ \\__,_|_|_|\\__,_|       \\_____\\___/|_|  \\___|");

        sender.sendMessage(prefix + "§eis starting...");
        hookManager = new HookManager(this);
        if (getConfig().getBoolean("gameapi")) gameAPI = new GameAPI(this);

        sender.sendMessage(prefix + "§edetect server...");
        sender.sendMessage(prefix + "§adetected server §6" + Bukkit.getServerName());
        preInit();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "ban", new BanProcess(this));

        infoScheduler();
        sender.sendMessage(prefix + "§awas successfully started!");
    }

    public CoreSpigot getCoreSpigot() {
        return coreSpigot;
    }

    public void setCoreSpigot(CoreSpigot coreSpigot) {
        this.coreSpigot = coreSpigot;
    }

    public GameAPI getGameAPI() {
        return gameAPI;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public ConsoleCommandSender getSender() {
        return sender;
    }

    public void setSender(ConsoleCommandSender sender) {
        this.sender = sender;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public double getProcessCpuLoad() throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

        if (list.isEmpty()) return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0) return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }
}
