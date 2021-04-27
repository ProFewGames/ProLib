package xyz.ufactions.prolib.gui;

import org.bukkit.entity.Player;
import xyz.ufactions.prolib.api.Module;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class GUIFuture<T extends Module, K> extends GUI<T> {

    private final CompletableFuture<K> future = new CompletableFuture<>();
    private boolean hasCompleted = false;

    public GUIFuture(T plugin, String name, GUIFiller filler) {
        super(plugin, name, filler);
    }

    public GUIFuture(T plugin, String name, int size, GUIFiller filler) {
        super(plugin, name, size, filler);
    }

    public final GUIFuture<T, K> complete(K k) {
        if (hasCompleted) throw new IllegalStateException("Already completed this future.");
        future.complete(k);
        hasCompleted = true;
        return this;
    }

    public boolean hasCompleted() {
        return hasCompleted;
    }

    public final GUIFuture<T, K> open(Player player) {
        Plugin.runSync(() -> openInventory(player));
        return this;
    }

    public final K getThrown() {
        try {
            try {
                return get();
            } catch (TimeoutException ignored) {
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final K getThrown(long timeout) {
        try {
            try {
                return get(timeout);
            } catch (TimeoutException ignored) {
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final K get() throws InterruptedException, ExecutionException, TimeoutException {
        return get(60000);
    }

    public final K get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, TimeUnit.MILLISECONDS);
    }
}