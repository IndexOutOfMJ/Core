package main.de.mj.bb.core.commands;

import main.de.mj.bb.core.CoreSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BorderCommand implements CommandExecutor {

    private final CoreSpigot coreSpigot;

    public BorderCommand(@NotNull CoreSpigot coreSpigot) {
        this.coreSpigot = coreSpigot;
        coreSpigot.setCommand(this, "rand");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission("citybuild.border"))
                player.openInventory(coreSpigot.getServerManager().getBorderListener().getBorderInv());
            else
                player.sendMessage(coreSpigot.getServerManager().getData().getNoPerm());
        } else commandSender.sendMessage(coreSpigot.getServerManager().getData().getNoPerm());
        return false;
    }
}