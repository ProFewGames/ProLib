package xyz.ufactions.prolib.libs;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class UtilMath {

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String string) {
        return isInteger(null, string);
    }

    public static boolean isInteger(CommandSender sender, String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            if (sender != null)
                sender.sendMessage(F.error("Math", F.elem(string) + C.mError + " is not a real number."));
            return false;
        }
    }

    public static String format(double paramDouble) {
        NumberFormat localNumberFormat = NumberFormat.getInstance(Locale.ENGLISH);

        localNumberFormat.setMaximumFractionDigits(3);

        localNumberFormat.setMinimumFractionDigits(0);

        return localNumberFormat.format(paramDouble);
    }

    public static String fixMoney(double paramDouble) {
        if (paramDouble < 1000000.0D) {
            return format(paramDouble);
        }
        if (paramDouble < 1.0E9D) {
            return format(paramDouble / 1000000.0D) + "M";
        }
        if (paramDouble < 1.0E12D) {
            return format(paramDouble / 1.0E9D) + "B";
        }
        if (paramDouble < 1.0E15D) {
            return format(paramDouble / 1.0E12D) + "T";
        }
        if (paramDouble < 1.0E18D) {
            return format(paramDouble / 1.0E15D) + "Q";
        }

        return String.valueOf(paramDouble);
    }

    public static int round(int num) {
        return (num / 9 + ((num % 9 == 0) ? 0 : 1)) * 9;
    }

    public static double trim(int degree, double d) {
        String format = "#.#";

        for (int i = 1; i < degree; i++)
            format += "#";

        DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.US);
        DecimalFormat twoDForm = new DecimalFormat(format, symb);
        return Double.valueOf(twoDForm.format(d));
    }

    public static Random random = new Random();

    public static int r(int i) {
        return random.nextInt(i);
    }

    public static double offset2d(Entity a, Entity b) {
        return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset2d(Location a, Location b) {
        return offset2d(a.toVector(), b.toVector());
    }

    public static double offset2d(Vector a, Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }

    public static double offset(Entity a, Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset(Location a, Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static double offset(Vector a, Vector b) {
        return a.subtract(b).length();
    }

    public static double offsetSquared(Entity a, Entity b) {
        return offsetSquared(a.getLocation(), b.getLocation());
    }

    public static double offsetSquared(Location a, Location b) {
        return offsetSquared(a.toVector(), b.toVector());
    }

    public static double offsetSquared(Vector a, Vector b) {
        return a.distanceSquared(b);
    }

    public static double rr(double d, boolean bidirectional) {
        if (bidirectional)
            return Math.random() * (2 * d) - d;

        return Math.random() * d;
    }
}
