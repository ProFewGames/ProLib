package xyz.ufactions.prolib.gui.button;

import xyz.ufactions.prolib.api.Module;

public abstract class UpdatableButton<T extends Module> extends IButton<T> {

    private final int position;

    public UpdatableButton(T plugin, int position) {
        super(plugin);

        this.position = position;
    }

    @Override
    public int getSlot() {
        return position;
    }
}