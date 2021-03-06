/*
 * @author MJ
 * Created in 25.08.2018
 * Copyright (c) 2017 - 2018 by MJ. All rights reserved.
 *
 */

package main.de.mj.bb.core.listener;

import main.de.mj.bb.core.CoreSpigot;
import main.de.mj.bb.core.utils.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StopReloadRestartListener implements Listener {

    private boolean isRestarting = false;
    private final CoreSpigot coreSpigot;
    String prefix = new Data().getPrefix();

    public StopReloadRestartListener(@NotNull CoreSpigot coreSpigot) {
        this.coreSpigot = coreSpigot;
        coreSpigot.setListener(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OverrideCommand(PlayerCommandPreprocessEvent preprocessEvent) {
        Player player = preprocessEvent.getPlayer();
        String msg = preprocessEvent.getMessage().toLowerCase();
        if (player.hasPermission("group.administrator")) {
            if (msg.equalsIgnoreCase("/stop") || msg.equalsIgnoreCase("/reload") || msg.equalsIgnoreCase("/restart")) {
                preprocessEvent.setCancelled(true);
                Inventory inv = Bukkit.createInventory(null, 9, "§c" + coreSpigot.getServer().getServerName() + " neustarten?");
                //for (int i = inv.getSize()-1; i >= 0; i--) {
                //    inv.setItem(i, coreSpigot.getModuleManager().getItemCreator().createItemWithMaterial(Material.STAINED_GLASS_PANE, coreSpigot.getModuleManager().getSettingsListener().getDesign().get(player), 1));
                //}
                ItemStack ClayNein = new ItemStack(Material.INK_SACK, 1, (short) 1);
                ItemMeta ClayNeinMeta = ClayNein.getItemMeta();
                ClayNeinMeta.setDisplayName("§cNein");
                ClayNein.setItemMeta(ClayNeinMeta);
                inv.setItem(3, ClayNein);

                ItemStack ClayJa = new ItemStack(Material.INK_SACK, 1, (short) 10);
                ItemMeta ClayJaMeta = ClayJa.getItemMeta();
                ClayJaMeta.setDisplayName("§aJa");
                ClayJa.setItemMeta(ClayJaMeta);
                inv.setItem(5, ClayJa);

                player.openInventory(inv);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent clickEvent) {
        if (clickEvent.getClickedInventory() == null) return;
        if (clickEvent.getClickedInventory().getType() == null) return;
        if (clickEvent.getCurrentItem() == null) return;
        if (clickEvent.getCurrentItem().getType() == null) return;
        if (clickEvent.getCurrentItem().getType().equals(Material.AIR)) return;
        if (clickEvent.getInventory().getTitle().contains("neustarten")) {
            Player player = (Player) clickEvent.getWhoClicked();
            if (clickEvent.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aJa")) {
                player.closeInventory();
                player.sendMessage("§aDu hast einen Serverneustart initialisiert!");
                if (!isRestarting) shutdown();
                isRestarting = true;
            } else if (clickEvent.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cNein")) {
                player.closeInventory();
            }
        }
    }

    public void shutdown() {
        Bukkit.broadcastMessage(prefix + "§c§lDer Server muss neu gestartet werden.");
        Bukkit.broadcastMessage(prefix + "§c" + Bukkit.getServerName() + " wird gleich wieder verf\u00FCgbar sein.");
        new BukkitRunnable() {
            int time = 10;

            @Override
            public void run() {
                if (time == 10) Bukkit.broadcastMessage(prefix + "§7Neustart in §6§l" + time + " Sekunden§7.");
                if (time <= 5 && time != 1)
                    Bukkit.broadcastMessage(prefix + "§7Neustart in §6§l" + time + " Sekunden§7.");
                if (time == 1) Bukkit.broadcastMessage(prefix + "§7Neustart in §6§l" + time + " Sekunde§7.");
                time--;
                if (time == 0) {
                    cancel();
                    Bukkit.shutdown();
                }

            }
        }.runTaskTimer(coreSpigot, 0L, 20L);
    }

    public boolean isRestarting() {
        return this.isRestarting;
    }

    public void setRestarting(boolean isRestarting) {
        this.isRestarting = isRestarting;
    }

    public CoreSpigot getCoreSpigot() {
        return this.coreSpigot;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
