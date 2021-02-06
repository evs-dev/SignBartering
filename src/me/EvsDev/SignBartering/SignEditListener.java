package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.block.Block;
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
        if (!validateFirstLine(e.getLine(0))) return;

        final Player player = e.getPlayer();

        if (!isSignOnContainer(e.getBlock())) {
            Errors.showUserError(Errors.SIGN_NOT_ON_CONTAINER, player);
            return;
        }

        final ItemStack sellingItemStack = LineChecker.parseItemAndQuantityLine(e.getLine(1), Main.itemQuantitySeparator);
        if (!validateItemStack(sellingItemStack, player, 2)) return;

        final ItemStack priceItemStack = LineChecker.parseItemAndQuantityLine(e.getLine(2), Main.itemQuantitySeparator);
        if (!validateItemStack(priceItemStack, player, 3)) return;

        // Parse the (valid!) player's formatting into readable + InteractListener-parseable text
        e.setLine(0, formatFirstLine());
        e.setLine(1, formatItemStackLine(sellingItemStack));
        e.setLine(2, formatItemStackLine(priceItemStack));
        e.setLine(3, formatLastLine(player));

        sendSetUpConfirmation(player);
        sendClickToAnnounceMessage(player, sellingItemStack, priceItemStack);
    }

    private boolean validateFirstLine(String line) {
        return LineChecker.sufficientFirstLine(line);
    }

    private boolean validateItemStack(ItemStack itemStack, Player signMaker, int line) {
        if (itemStack == null) {
            Errors.showUserError(Errors.INVALID_LINE, signMaker, line);
            return false;
        }
        return true;
    }

    private boolean isSignOnContainer(Block block) {
        return (SBUtil.isWallSign(block.getType())
            && SBUtil.isBlockStateContainer(SBUtil.getBehindBlock(block).getState()));
    }

    private String formatFirstLine() {
        return ChatColor.GOLD + LineChecker.requiredFirstLine;
    }

    private String formatLastLine(Player player) {
        return ChatColor.GRAY + "(" + player.getDisplayName() + ")";
    }

    private String formatItemStackLine(ItemStack itemStack) {
        return ChatColor.WHITE
            + SBUtil.cleanName(itemStack.getType().toString())
            + Main.formattedItemQuantitySeparator
            + Integer.toString(itemStack.getAmount());
    }

    private void sendSetUpConfirmation(Player player) {
        player.sendMessage(Main.MESSAGE_PREFIX + ChatColor.GREEN + "Shop setup successfully!");
        player.sendMessage(Main.MESSAGE_PREFIX + "Make sure there is enough room in your chest for payment.");
    }

    private TextComponent createClickToAnnounceMessage(String command) {
        TextComponent message = new TextComponent("[Click to Announce]");
        message.setColor(ChatColor.LIGHT_PURPLE);
        message.setUnderlined(true);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        message.setHoverEvent(
            new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to announce your shop to the server").color(ChatColor.WHITE).create()
            )
        );
        return message;
    }

    private String formatClickToAnnounceCommand(Location playerLocation, ItemStack selling, ItemStack price) {
        return String.format("/me just set up a new shop at X: %s Y: %s Z: %s selling [%s]x%s for [%s]x%s!",
            (int)Math.floor(playerLocation.getX()),
            (int)Math.floor(playerLocation.getY()),
            (int)Math.floor(playerLocation.getZ()),
            SBUtil.cleanName(selling.getType().toString()),
            Integer.toString(selling.getAmount()),
            SBUtil.cleanName(price.getType().toString()),
            Integer.toString(price.getAmount())
        );
    }

    private void sendClickToAnnounceMessage(Player player, ItemStack selling, ItemStack price) {
        player.spigot().sendMessage(
            createClickToAnnounceMessage(
                formatClickToAnnounceCommand(
                    player.getLocation(), selling, price
                )
            )
        );
    }

}
