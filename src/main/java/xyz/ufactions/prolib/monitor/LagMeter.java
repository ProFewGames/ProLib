package xyz.ufactions.prolib.monitor;

import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

public class LagMeter {
    private long lastRun;

    private double ticksPerSecond;

    public static LagMeter instance;

    public static void initialize() {
        if (instance == null)
            instance = new LagMeter();
    }

    private LagMeter() {
        this.lastRun = System.currentTimeMillis();
    }

    public void update(UpdateEvent event) {
        if (event.getType() != UpdateType.SEC)
            return;
        long now = System.currentTimeMillis();
        this.ticksPerSecond = 1000.0D / (now - this.lastRun) * 20.0D;
        this.lastRun = now;
    }

    public double getTicksPerSecond() {
        return this.ticksPerSecond;
    }
}
