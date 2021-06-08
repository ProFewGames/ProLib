package xyz.ufactions.prolib.animation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    public enum AnimationType {
        WAVE
    }

    public interface Animatable {
        void draw(String string);
    }

    private final String string;
    private final AnimationType type;

    private Animatable animatable;

    private int runnableID = -1;
    private int index = 0;

    public Animation(String string, AnimationType type) {
        this(string, type, null);
    }

    public Animation(String string, AnimationType type, Animatable animatable) {
        this.string = string;
        this.type = type;
        this.animatable = animatable;
    }

    public void start(Plugin plugin) {
        stop();
        this.runnableID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 2);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.runnableID);
        this.runnableID = -1;
    }

    public boolean hasStarted() {
        return this.runnableID != -1;
    }

    public void setAnimatable(Animatable animatable) {
        this.animatable = animatable;
    }

    private void tick() {
        if (type == AnimationType.WAVE) {
            waveTick();
        }
    }

    private void waveTick() {
        if (animatable == null) return;
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (i == index) {
                out.append(ChatColor.BLUE);
            } else if (i == index + 1 || i == index - 1) {
                out.append(ChatColor.AQUA);
            } else {
                out.append(ChatColor.WHITE);
            }
            out.append(ChatColor.BOLD).append(c);
        }
        index++;
        if (index - 1 == string.length()) {
            index = 0;
        }
        animatable.draw(out.toString());
    }
}