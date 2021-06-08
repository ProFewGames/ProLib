package xyz.ufactions.prolib.npc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilLoc;
import xyz.ufactions.prolib.libs.UtilMath;
import xyz.ufactions.prolib.npc.NPCModule;
import xyz.ufactions.prolib.npc.data.NPC;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NPCCommand extends CommandBase<NPCModule> {

    public NPCCommand(NPCModule plugin) {
        super(plugin, "npc");

        setPermission("prolib.npc.command");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("types")) {
                displayEntityTypes(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reload();
                sender.sendMessage(F.main(plugin.getName(), "Reloaded."));
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (plugin.getNPCs().isEmpty()) {
                    sender.sendMessage(F.error(plugin.getName(), "No Available NPCs"));
                    return;
                }
                sender.sendMessage(F.main(plugin.getName(), "Listing NPCs:"));
                for (NPC npc : plugin.getNPCs()) {
                    sender.sendMessage(F.list(C.mHead + "#" + npc.getID() + " " +
                            F.capitalizeFirstLetter(npc.getType().toString()) +
                            (npc.getName() != null ? " " + C.color(npc.getName()) : "") +
                            C.mBody + " (" +
                            C.mElem + UtilLoc.toString(npc.getLocation()) +
                            C.mBody + ")"));
                }
                return;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("move")) {
                if (!isPlayer(sender)) return;
                Player player = ((Player) sender);
                NPC npc = getNPC(sender, args[1]);
                if (npc == null) return;
                npc.despawn();
                npc.setLocation(player.getLocation());
                npc.spawn();
                plugin.saveNPC(npc);
                return;
            }
        }
        if (args.length >= 2) {
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("command")) {
                    NPC npc = getNPC(sender, args[1]);
                    if (npc == null) return;
                    plugin.addCommand(npc, F.concatenate(2, " ", args));
                    sender.sendMessage(F.main(plugin.getName(), "Command added to NPC."));
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (!isPlayer(sender)) return;
                Player player = (Player) sender;
                EntityType type;
                try {
                    type = EntityType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(F.error(plugin.getName(), "Invalid Entity Type."));
                    displayEntityTypes(player);
                    return;
                }
                NPC npc = plugin.createNPC(type, player.getLocation(), true);
                if (args.length >= 3)
                    npc.setName(F.concatenate(2, " ", args));
                npc.spawn();
                plugin.saveNPC(npc);
                player.sendMessage(F.main(plugin.getName(), "NPC Created."));
                return;
            }
        }
        sender.sendMessage(F.line("NPC"));
        sender.sendMessage(F.help("/" + AliasUsed + " create <Entity Type> [NPC Name...]", "Create an NPC with desired [name]"));
        sender.sendMessage(F.help("/" + AliasUsed + " command <NPC ID> <Command...>", "Add a command to an NPC to execute a command"));
        sender.sendMessage(F.help("/" + AliasUsed + " move <NPC ID>", "Move said NPC to your location"));
        sender.sendMessage(F.help("/" + AliasUsed + " types", "View available entity types"));
        sender.sendMessage(F.help("/" + AliasUsed + " list", "View all available NPCs"));
        sender.sendMessage(F.help("/" + AliasUsed + " reload", "Reload this module"));
        sender.sendMessage(F.line());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!permissionCheck(sender, false)) return super.onTabComplete(sender, cmd, label, args);
        if (args.length == 1) {
            return getMatches(args[0], Arrays.asList("reload", "create", "move", "list", "command"));
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                return getMatches(args[1], getEntityTypes().toArray(new EntityType[0]));
            }
        }
        return Collections.emptyList();
    }

    private NPC getNPC(CommandSender sender, String string) {
        if (!UtilMath.isInteger(sender, string)) return null;
        NPC npc = plugin.getNPC(Integer.parseInt(string));
        if (npc == null) {
            sender.sendMessage(F.error(plugin.getName(), "There is no NPC with that ID."));
        }
        return npc;
    }

    private void displayEntityTypes(CommandSender sender) {
        sender.sendMessage(C.mBody + C.Bold + "Available Entity Types:");
        String validEntityTypesString = F.concatenate(getEntityTypes(), ", ", Enum::name);
        sender.sendMessage(F.elem(validEntityTypesString));
    }

    private List<EntityType> getEntityTypes() {
        return F.getMatches(EntityType.values(), EntityType::isAlive);
    }
}