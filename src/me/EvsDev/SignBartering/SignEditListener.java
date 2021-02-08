package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import me.EvsDev.SignBartering.LineFormatters.IItemStackLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.PriceLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.SellingLineFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SignEditListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        FirstLine firstLine = FirstLine.interpretFirstLine(e.getLine(0), false);
        if (firstLine == null) return;

        final Player player = e.getPlayer();

        if (!isSignOnContainer(e.getBlock())) {
            Errors.showUserError(Errors.SIGN_NOT_ON_CONTAINER, player);
            return;
        }

        if (firstLine.onlyOp() && !player.isOp()) {
            Errors.showUserError(Errors.NO_PERMISSION, player);
            return;
        }

        final SellingLineFormatter sellingLineFormatter = firstLine.getSellingLineFormatter();
        final Object sellingLineResult = sellingLineFormatter.interpretPlayerFormattedLine(e.getLine(1));
        if (!validateItemStackLineResult(sellingLineResult, sellingLineFormatter, player, 2)) return;

        // Special case - if they have set price to free, make it a FirstLine.FREE sign
        if (firstLine == FirstLine.SELL && e.getLine(2).endsWith(Main.ITEM_QUANTITY_SEPARATOR + "0")) firstLine = FirstLine.FREE;
        final PriceLineFormatter priceLineFormatter = firstLine.getPriceLineFormatter();
        final Object priceLineResult = priceLineFormatter.interpretPlayerFormattedLine(e.getLine(2));
        if (!validateItemStackLineResult(priceLineResult, priceLineFormatter, player, 3)) return;

        // Parse the (valid!) player's formatting into readable + InteractListener-parseable text
        e.setLine(0, formatFirstLine(firstLine));
        e.setLine(1, sellingLineFormatter.formatSelfInterpretedLine(sellingLineResult));
        e.setLine(2, priceLineFormatter.formatSelfInterpretedLine(priceLineResult));
        e.setLine(3, firstLine.getNameLineFormatter().formatPlayerFormattedLine(e.getLine(3), player));

        sendSetUpConfirmation(player);
        //sendClickToAnnounceMessage(player, sellingItemStack, priceItemStack);
    }

    private boolean validateItemStackLineResult(Object result, IItemStackLineFormatter lineFormatter, Player signMaker, int line) {
        if (!lineFormatter.isValidSelfInterpretation(result)) {
            Errors.showUserError(Errors.INVALID_LINE, signMaker, line);
            return false;
        }
        return true;
    }

    private boolean isSignOnContainer(Block block) {
        return (SBUtil.isWallSign(block.getType())
            && SBUtil.isContainer(SBUtil.getBehindBlock(block).getState()));
    }

    private String formatFirstLine(FirstLine firstLine) {
        return ChatColor.GOLD + "[" + firstLine.getPerfectName() + "]";
    }

    private void sendSetUpConfirmation(Player player) {
        player.sendMessage(Main.MESSAGE_PREFIX + ChatColor.GREEN + "Shop setup successfully!");
        player.sendMessage(Main.MESSAGE_PREFIX + "Make sure there is enough room in your container for payment.");
    }

    private TextComponent createClickToAnnounceMessage(String command) {
        final TextComponent message = new TextComponent("[Click to Announce]");
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

    @SuppressWarnings("unused")
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
