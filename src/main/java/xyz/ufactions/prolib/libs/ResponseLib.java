package xyz.ufactions.prolib.libs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.ButtonBuilder;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.GUIBuilder;
import xyz.ufactions.prolib.gui.button.Button;

import java.util.concurrent.CompletableFuture;

public class ResponseLib {

    public static CompletableFuture<String> getString(final Module module, final Player player, final int timeoutSeconds) {
        CompletableFuture<String> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskLater(module.getPlugin(), () -> future.complete(""), timeoutSeconds);
        // TODO
        return future;
    }

    public static CompletableFuture<Boolean> getBoolean(final Module module, final Player player, final long timeoutTicks) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Button<?> confirm = ButtonBuilder.instance(module)
                .item(ColorLib.cw(ChatColor.GREEN).name(C.cGreen + C.Bold + "CONFIRM"))
                .slot(11)
                .onClick((p, clickType) -> {
                    player.closeInventory();
                    future.complete(true);
                })
                .build();
        Button<?> deny = ButtonBuilder.instance(module)
                .item(ColorLib.cw(ChatColor.RED).name(C.cRed + C.Bold + "DENY"))
                .slot(15)
                .onClick((p, clickType) -> {
                    player.closeInventory();
                    future.complete(false);
                })
                .build();
        GUIBuilder.instance(module, C.mHead + C.Bold + "Confirmation", GUI.GUIFiller.PANE)
                .onActionPerformed(GUI.GUIAction.CLOSE, p -> {
                    Bukkit.getScheduler().runTaskLater(module.getPlugin(), () -> {
                        if (!future.isDone()) {
                            future.complete(false);
                        }
                    }, 1L);
                })
                .addButton(confirm, deny)
                .color(ChatColor.AQUA)
                .size(27)
                .build().openInventory(player);
        Bukkit.getScheduler().runTaskLater(module.getPlugin(), () -> {
            if (!future.isDone()) {
                player.closeInventory();
                future.complete(false);
                UtilPlayer.message(player, F.error("Response", "Boolean response time-out."));
            }
        }, timeoutTicks);
        return future;
    }
}