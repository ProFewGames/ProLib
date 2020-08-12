package xyz.ufactions.prolib.recharge;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.UtilGear;
import xyz.ufactions.prolib.libs.UtilTextBottom;
import xyz.ufactions.prolib.libs.UtilTime;

public class RechargeData {
    public Recharge Host;
    public long Time;
    public long Recharge;
    public Player Player;
    public String Name;
    public ItemStack Item;
    public boolean DisplayForce = false;
    public boolean Countdown = false;
    public boolean AttachItem;
    public boolean AttachDurability;

    public RechargeData(Recharge host, Player player, String name, ItemStack stack, long rechargeTime, boolean attachitem, boolean attachDurability) {
        this.Host = host;

        this.Player = player;
        this.Name = name;
        this.Item = player.getItemInHand();
        this.Time = System.currentTimeMillis();
        this.Recharge = rechargeTime;

        this.AttachItem = attachitem;
        this.AttachDurability = attachDurability;
    }

    public boolean Update() {
        if ((this.DisplayForce || this.Item != null) && this.Name != null && this.Player != null) {


            double percent = (System.currentTimeMillis() - this.Time) / this.Recharge;

            if (this.DisplayForce || this.AttachItem) {

                try {

                    if (this.DisplayForce || (this.Item != null && UtilGear.isMat(this.Player.getItemInHand(), this.Item.getType()))) {
                        if (!UtilTime.elapsed(this.Time, this.Recharge)) {

                            UtilTextBottom.displayProgress(C.Bold + this.Name, percent, UtilTime.MakeStr(this.Recharge - System.currentTimeMillis() - this.Time), this.Countdown, new Player[]{this.Player});

                        } else {

                            if (!this.Countdown) {
                                UtilTextBottom.display(C.cGreen + C.Bold + this.Name + " Recharged", new Player[]{this.Player});
                            } else {
                                UtilTextBottom.display(C.cRed + C.Bold + this.Name + " Ended", new Player[]{this.Player});
                            }

                            if (this.Recharge > 4000L) {
                                this.Player.playSound(this.Player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.4F, 3.0F);
                            }
                        }
                    }
                } catch (Exception e) {

                    System.out.println("Recharge Indicator Error!");
                    e.printStackTrace();
                }
            }

            if (this.AttachDurability && this.Item != null) {
                this.Item.setDurability((short) (int) (this.Item.getType().getMaxDurability() - this.Item.getType().getMaxDurability() * percent));
            }
        }

        return UtilTime.elapsed(this.Time, this.Recharge);
    }

    public long GetRemaining() {
        return this.Recharge - System.currentTimeMillis() - this.Time;
    }

    public void debug(Player player) {
        player.sendMessage("Recharge: " + this.Recharge);
        player.sendMessage("Time: " + this.Time);
        player.sendMessage("Elapsed: " + (System.currentTimeMillis() - this.Time));
        player.sendMessage("Remaining: " + GetRemaining());
    }
}
