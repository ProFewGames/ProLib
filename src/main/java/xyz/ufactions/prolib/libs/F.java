package xyz.ufactions.prolib.libs;

import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class F {

    public static String main(String module, String body) {
        return C.mHead + module + "> " + C.mBody + body;
    }

    public static String line() {
        return C.mHead + C.Strike + rawLine();
    }

    public static String rawLine() {
        return "--------------------------------------------";
    }

    public static String help(String help, String desc) {
        return C.mHead + help + " " + C.mBody + desc;
    }

    public static String error(String module, String body) {
        return main(module, C.mError + body);
    }

    public static String list(String string) {
        return "  " + C.mBody + "➥ " + C.cYellow + string;
    }

    public static String noPermission() {
        return C.cRed + "No Permission.";
    }

    public static String noPlayer() {
        return C.cRed + "You must be a player ingame to do this action.";
    }

    public static String capitalizeFirstLetter(String string) {
        if (string.contains(" ")) {
            StringBuilder toReturn = new StringBuilder();
            String[] array = string.split(" ");

            for (String s : array) {
                if (toReturn.length() == 0) {
                    toReturn.append(capitalizeFirstLetter(s));
                } else {
                    toReturn.append(" ").append(capitalizeFirstLetter(s));
                }
            }
            return toReturn.toString();
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String elem(String elem) {
        return C.mElem + elem + ChatColor.RESET + C.mBody;
    }

    public static String name(String elem) {
        return C.mElem + elem + C.mBody;
    }

    public static String skill(String elem) {
        return C.mSkill + elem + C.mBody;
    }

    public static String time(String elem) {
        return C.mTime + elem + C.mBody;
    }

    public static String value(String variable, String value) {
        return value(0, variable, value);
    }

    public static String cd(boolean var) {
        if (var) return C.listValueOn + "connected" + C.mBody;
        return C.listValueOff + "disconnected" + C.mBody;
    }

    public static String ed(boolean var) {
        if (var)
            return C.listValueOn + "enable" + C.mBody;
        return C.listValueOff + "disabled" + C.mBody;
    }

    public static String oo(boolean var) {
        if (var)
            return C.listValueOn + "on" + C.mBody;
        return C.listValueOff + "off" + C.mBody;
    }

    public static String value(Enum[] numerators) {
        String indent = "";
        for (Enum e : numerators) {
            if (indent.isEmpty()) {
                indent = "[" + e.toString();
            } else {
                indent += ", " + e.toString();
            }
        }
        indent += "]";
        return C.mElem + indent;
    }

    public static String value(int a, String variable, String value) {
        String indent = "";
        while (indent.length() < a)
            indent += ChatColor.GRAY + ">";

        return indent + C.listTitle + variable + ": " + C.listValue + value;
    }

    public static String value(String variable, String value, boolean on) {
        return value(0, variable, value, on);
    }

    public static String value(int a, String variable, String value, boolean on) {
        String indent = "";
        while (indent.length() < a)
            indent += ChatColor.GRAY + ">";

        if (on) return indent + C.listTitle + variable + ": " + C.listValueOn + value;
        else return indent + C.listTitle + variable + ": " + C.listValueOff + value;
    }

    public static String matchCase(Set<String> set, String starter) {
        for (String string : set) {
            if (string.equalsIgnoreCase(starter)) {
                return string;
            }
        }
        return starter;
    }

    public static <T> List<T> getMatches(T[] list, Operation<T, Boolean> operation) {
        List<T> matches = new ArrayList<>();
        for (T t : list)
            if (operation.execute(t))
                matches.add(t);
        return matches;
    }

    public static String concatenate(String splitter, List<String> list) {
        return concatenate(splitter, list.toArray(new String[0]));
    }

    public static String concatenate(String splitter, Enum<?>... enumerators) {
        String[] array = new String[enumerators.length];

        for (int i = 0; i < enumerators.length; i++) {
            array[i] = enumerators[i].name();
        }

        return concatenate(splitter, array);
    }

    public static String concatenate(String splitter, String[] array) {
        return concatenate(0, splitter, array);
    }

    public static String concatenate(int index, String splitter, String[] array) {
        String string = "";
        for (int i = index; i < array.length; i++) {
            if (string.isEmpty())
                string = array[i];
            else
                string += splitter + array[i];
        }
        return string;
    }

    private static String format(double paramDouble) {
        NumberFormat localNumberFormat = NumberFormat.getInstance(Locale.ENGLISH);

        localNumberFormat.setMaximumFractionDigits(2);

        localNumberFormat.setMinimumFractionDigits(0);

        return localNumberFormat.format(paramDouble);
    }

    public static String formatMoney(double paramDouble) {
        if (paramDouble < 1000.0D) {
            return format(paramDouble);
        }
        if (paramDouble < 1000000.0D) {
            return NumberFormat.getInstance().format(paramDouble);
        }
        if (paramDouble < 1.0E9D) {
            return format(paramDouble / 1000000.0D) + " M";
        }
        if (paramDouble < 1.0E12D) {
            return format(paramDouble / 1.0E9D) + " B";
        }
        if (paramDouble < 1.0E15D) {
            return format(paramDouble / 1.0E12D) + " T";
        }
        if (paramDouble < 1.0E18D) {
            return format(paramDouble / 1.0E15D) + " Q";
        }
        long l = (long) paramDouble;
        return String.valueOf(l);
    }
}
