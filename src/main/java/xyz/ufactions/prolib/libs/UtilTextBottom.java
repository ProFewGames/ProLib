package xyz.ufactions.prolib.libs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UtilTextBottom
{
	public static void display(String text, Player... players)
	{
		for(Player player : players) {
			TitleAPI.sendActionBar(player, text, 2);
		}
	}
	
	public static void displayProgress(double amount, Player... players)
	{
		displayProgress(null, amount, null, players);
	}
	
	public static void displayProgress(String prefix, double amount, Player... players)
	{
		displayProgress(prefix, amount, null, players);
	}
	
	public static void displayProgress(String prefix, double amount, String suffix, Player... players)
	{
		displayProgress(prefix, amount, suffix, false, players);
	}
	
	public static void displayProgress(String prefix, double amount, String suffix, boolean progressDirectionSwap, Player... players)
	{
		if (progressDirectionSwap)
			amount = 1 - amount;
		
		//Generate Bar
		int bars = 24;
		StringBuilder progressBar = new StringBuilder(C.cGreen + "");
		boolean colorChange = false;
		for (int i=0 ; i<bars ; i++)
		{
			if (!colorChange && (float)i/(float)bars >= amount)
			{
				progressBar.append(C.cRed);
				colorChange = true;
			}
			
			progressBar.append("▌");
		}
		
		//Send to Player
		for (Player player : players)
		{
				display((prefix == null ? "" : prefix + ChatColor.RESET + " ") + progressBar + (suffix == null ? "" : ChatColor.RESET + " " + suffix), players);
		}
	} 
}
