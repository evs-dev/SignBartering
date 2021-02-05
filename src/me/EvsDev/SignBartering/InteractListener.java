package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.EvsDev.SignBartering.BarteringSign.BarteringSignCreationException;
import net.md_5.bungee.api.ChatColor;

public class InteractListener implements Listener {

    @EventHandler
    public void onSignOrContainerInteractedWith(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;

        Block block = e.getClickedBlock();

        if (ContainerUtil.isBlockStateContainer(block.getState())) {
            onContainerInteractedWith(e);
        } else if (SB.isWallSign(block.getType())) {
            onSignInteractedWith(e);
        }
    }

    private void onContainerInteractedWith(PlayerInteractEvent e) {
        Block signBlock = ContainerUtil.findSurroundingSellingSignBlock(e.getClickedBlock());

        if (signBlock == null) return;

        if (!SB.playerIsSignOwner((Sign) signBlock.getState(), e.getPlayer())) {
            SB.error(e, "You cannot open this container as you are not the owner of this shop");
            e.setCancelled(true);
        }

        return;
    }

    private void onSignInteractedWith(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        // Is [Selling] sign
        Sign sign = (Sign) block.getState();

        BarteringSign barteringSign;
        try {
            barteringSign = new BarteringSign(sign);
        } catch (BarteringSignCreationException error) {
            return;
        }

        String[] lines = sign.getLines();

        ItemStack sellingItemStack = barteringSign.getSellingItemStack();
        //SB.error(e, "This sign is not a valid SignBartering sign (something is wrong with it)");

        // Get container inventory
        Inventory containerInv = ((InventoryHolder)SB.getBehindBlock(block).getState()).getInventory();

        // Does the container behind this sign have enough to give?
        boolean isStocked = false;
        int foundAmount = 0;
        for (ItemStack itemStack : containerInv.getStorageContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType() != sellingItemStack.getType()) {
                isStocked = false;
            } else if (itemStack.getAmount() >= sellingItemStack.getAmount()) {
                isStocked = true;
                break;
            } else {
                foundAmount += itemStack.getAmount();
                if (foundAmount >= sellingItemStack.getAmount()) {
                    isStocked = true;
                    break;
                }
            }
        }
        if (!isStocked) {
            String appendix;
            Player owner = barteringSign.getSignOwner();
            if (owner != null) {
                Location blockLocation = block.getLocation();
                String noStockMessage = String.format(
                        "%s tried to purchase [%s]x%s from your shop at X: %s Y: %s Z: %s but it is out of stock",
                        e.getPlayer().getDisplayName(),
                        SB.cleanName(sellingItemStack.getType().toString()),
                        Integer.toString(sellingItemStack.getAmount()),
                        blockLocation.getBlockX(),
                        blockLocation.getBlockY(),
                        blockLocation.getBlockZ()
                        );
                SB.error(owner, noStockMessage);
                appendix = "The shop owner " + lines[3] + ChatColor.RED + " has been notified";
            } else {
                appendix = "The shop owner " + lines[3] + ChatColor.RED + " could not be notified as they are offline";
            }
            SB.error(e, "The shop is not currently stocked. " + appendix);

            return;
        }

        // Get player and payment info
        PlayerInventory playerInv = e.getPlayer().getInventory();
        ItemStack payment = barteringSign.getPriceItemStack();

        // Does the player have enough payment?
        if (!playerInv.containsAtLeast(payment, 1)) {
            SB.error(e, SB.error_NoMoney);
            return;
        }

        // Find purchase in container
        ItemStack purchase = null;
        for (ItemStack itemStack : containerInv.getStorageContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType() == sellingItemStack.getType()) {
                purchase = new ItemStack(itemStack);
                purchase.setAmount(sellingItemStack.getAmount());
                break;
            }
        }

        playerInv.removeItem(payment);     // Take payment
        containerInv.addItem(payment);     // Store payment in container
        containerInv.removeItem(purchase); // Take purchase from container
        playerInv.addItem(purchase);       // Give purchase to player

        e.getPlayer().sendMessage(SB.messagePrefix + "Item(s) received");

        Player owner = barteringSign.getSignOwner();
        if (owner == null) return;

        String buyAlert = String.format("%s bought [%s]x%s for [%s]x%s from your shop!",
            e.getPlayer().getDisplayName(),
            SB.cleanName(sellingItemStack.getType().toString()),
            Integer.toString(sellingItemStack.getAmount()),
            SB.cleanName(payment.getType().toString()),
            Integer.toString(payment.getAmount())
        );

        owner.sendMessage(SB.messagePrefix + buyAlert);
    }

}
