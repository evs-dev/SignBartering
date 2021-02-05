package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SignEditListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String line1 = e.getLine(0);
        if (line1 == null || (!line1.equalsIgnoreCase(SB.requiredFirstLine) && !line1.equalsIgnoreCase(SB.alternativeRequiredFirstLine))) return;
        String[] lines = e.getLines();

        // Line 2
        ItemAndQuantity sellingItemAndQuantity = LineChecker.parseItemAndQuantityLine(lines[1]);

        if (sellingItemAndQuantity == null) {
            SB.error(e, "Error on line 2");
            return;
        }

        // Line 3
        ItemAndQuantity priceItemAndQuantity = LineChecker.parseItemAndQuantityLine(lines[2]);

        if (priceItemAndQuantity == null) {
            SB.error(e, "Error on line 3");
            return;
        }

        // Is it on a container?
        if (!SB.isWallSign(e.getBlock().getType()) ||
                !ContainerUtil.isBlockStateContainer(SB.getBehindBlock(e.getBlock()).getState())) {

            SB.error(e, "Sign must be placed on a container");
            return;
        }

        // --- Stuff that should happen only when the sign is formatted properly by the player
        // Format first line
        e.setLine(0, ChatColor.GOLD + SB.requiredFirstLine);

        // Format second line
        e.setLine(
                1,
                ChatColor.WHITE
                + SB.cleanName(sellingItemAndQuantity.item.toString())
                + SB.formattedItemQuantitySeparator
                + Integer.toString(sellingItemAndQuantity.quantity)
                );

        // Format third line
        e.setLine(
                2,
                ChatColor.WHITE
                + SB.cleanName(priceItemAndQuantity.item.toString())
                + SB.formattedItemQuantitySeparator
                + Integer.toString(priceItemAndQuantity.quantity)
                );

        // Put player name on last line
        e.setLine(3, ChatColor.GRAY + "(" + e.getPlayer().getDisplayName() + ")");

        // Send confirmation messages
        Player player = e.getPlayer();
        player.sendMessage(
                SB.messagePrefix + ChatColor.GREEN + "Shop setup successfully!"
                );
        player.sendMessage(
                SB.messagePrefix + "Make sure there is enough room in your chest for payment."
                );
        Location playerLocation = player.getLocation();
        String command;

        /*
		command = String.format(
				"/tellraw @a [\"\",{\"text\": \"[SignBartering] \", \"color\":\"gold\"}, {\"text\":\"%s just set up a new shop at X: %s Y: %s Z: %s selling [%s]!\",\"color\":\"green\"}]",
				player.getDisplayName(),
				(int)Math.floor(playerLocation.getX()),
				(int)Math.floor(playerLocation.getY()),
				(int)Math.floor(playerLocation.getZ()),
				SB.cleanName(sellingItemAndQuantity.item.toString())
		);*/
        command = String.format("/me just set up a new shop at X: %s Y: %s Z: %s selling [%s]x%s for [%s]x%s!",
                (int)Math.floor(playerLocation.getX()),
                (int)Math.floor(playerLocation.getY()),
                (int)Math.floor(playerLocation.getZ()),
                SB.cleanName(sellingItemAndQuantity.item.toString()),
                Integer.toString(sellingItemAndQuantity.quantity),
                SB.cleanName(priceItemAndQuantity.item.toString()),
                Integer.toString(priceItemAndQuantity.quantity)
                );

        TextComponent message = new TextComponent("[Click to Announce]");
        message.setColor(ChatColor.LIGHT_PURPLE);
        message.setUnderlined(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to announce your shop to the server").color(ChatColor.WHITE).create()));
        player.spigot().sendMessage(message);
    }


}
