package xyz.ufactions.prolib.libs;

import org.bukkit.ChatColor;

public class C {

    public static String Scramble = "§k";
    public static String Bold = "§l";
    public static String Strike = "§m";
    public static String BoldStrike = "§l§m";
    public static String Line = "§n";
    public static String Italics = "§o";

    public static String cAqua = "" + ChatColor.AQUA;
    public static String cBlack = "" + ChatColor.BLACK;
    public static String cBlue = "" + ChatColor.BLUE;
    public static String cDAqua = "" + ChatColor.DARK_AQUA;
    public static String cDBlue = "" + ChatColor.DARK_BLUE;
    public static String cDGray = "" + ChatColor.DARK_GRAY;
    public static String cDGreen = "" + ChatColor.DARK_GREEN;
    public static String cDPurple = "" + ChatColor.DARK_PURPLE;
    public static String cDRed = "" + ChatColor.DARK_RED;
    public static String cGold = "" + ChatColor.GOLD;
    public static String cGray = "" + ChatColor.GRAY;
    public static String cGreen = "" + ChatColor.GREEN;
    public static String cPurple = "" + ChatColor.LIGHT_PURPLE;
    public static String cRed = "" + ChatColor.RED;
    public static String cWhite = "" + ChatColor.WHITE;
    public static String cYellow = "" + ChatColor.YELLOW;

    public static String mHead = "" + ChatColor.DARK_PURPLE;
    public static String mBody = "" + ChatColor.LIGHT_PURPLE;
    public static String mElem = "" + ChatColor.YELLOW;
    public static String mError = "" + ChatColor.RED;
    public static String mTime = "" + ChatColor.GREEN;
    public static String mSkill = "" + ChatColor.GREEN;

    public static String listTitle = "" + ChatColor.WHITE;
    public static String listValue = "" + ChatColor.YELLOW;
    public static String listValueOn = "" + ChatColor.GREEN;
    public static String listValueOff = "" + ChatColor.RED;

    public static String chat = "" + ChatColor.WHITE;

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
