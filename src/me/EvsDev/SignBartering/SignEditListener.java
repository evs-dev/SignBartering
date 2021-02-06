package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SignEditListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String line1 = e.getLine(0);
        if (line1 == null || (!line1.equalsIgnoreCase(SBUtil.requiredFirstLine) && !line1.equalsIgnoreCase(SBUtil.alternativeRequiredFirstLine))) return;
        String[] lines = e.getLines();

        // Line 2
        ItemStack sellingItemAndQuantity = LineChecker.parseItemAndQuantityLine(lines[1], SBUtil.itemQuantitySeparator);

        if (sellingItemAndQuantity == null) {
            Errors.showUserError(Errors.INVALID_LINE, e.getPlayer(), 2);
            return;
        }

        // Line 3
        ItemStack priceItemAndQuantity = LineChecker.parseItemAndQuantityLine(lines[2], SBUtil.itemQuantitySeparator);

        if (priceItemAndQuantity == null) {
            Errors.showUserError(Errors.INVALID_LINE, e.getPlayer(), 3);
            return;
        }

        // Is it on a container?
        if (!SBUtil.isWallSign(e.getBlock().getType())
            || !SBUtil.isBlockStateContainer(SBUtil.getBehindBlock(e.getBlock()).getState())) {

            Errors.showUserError(Errors.SIGN_NOT_ON_CONTAINER, e.getPlayer());
            return;
        }

        // --- Stuff that should happen only when the sign is formatted properly by the player
        // Format first line
        e.setLine(0, ChatColor.GOLD + SBUtil.requiredFirstLine);

        // Format second line
        e.setLine(
            1,
            ChatColor.WHITE
            + SBUtil.cleanName(sellingItemAndQuantity.getType().toString())
            + SBUtil.formattedItemQuantitySeparator
            + Integer.toString(sellingItemAndQuantity.getAmount())
        );

        // Format third line
        e.setLine(
            2,
            ChatColor.WHITE
            + SBUtil.cleanName(priceItemAndQuantity.getType().toString())
            + SBUtil.formattedItemQuantitySeparator
            + Integer.toString(priceItemAndQuantity.getAmount())
        );

        // Put player name on last line
        e.setLine(3, ChatColor.GRAY + "(" + e.getPlayer().getDisplayName() + ")");

        // Send confirmation messages
        Player player = e.getPlayer();
        player.sendMessage(Main.MESSAGE_PREFIX + ChatColor.GREEN + "Shop setup successfully!");
        player.sendMessage(Main.MESSAGE_PREFIX + "Make sure there is enough room in your chest for payment.");

        Location playerLocation = player.getLocation();
        String command;

        command = String.format("/me just set up a new shop at X: %s Y: %s Z: %s selling [%s]x%s for [%s]x%s!",
            (int)Math.floor(playerLocation.getX()),
            (int)Math.floor(playerLocation.getY()),
            (int)Math.floor(playerLocation.getZ()),
            SBUtil.cleanName(sellingItemAndQuantity.getType().toString()),
            Integer.toString(sellingItemAndQuantity.getAmount()),
            SBUtil.cleanName(priceItemAndQuantity.getType().toString()),
            Integer.toString(priceItemAndQuantity.getAmount())
        );

        TextComponent message = new TextComponent("[Click to Announce]");
        message.setColor(ChatColor.LIGHT_PURPLE);
        message.setUnderlined(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to announce your shop to the server").color(ChatColor.WHITE).create()));
        player.spigot().sendMessage(message);
    }


}
