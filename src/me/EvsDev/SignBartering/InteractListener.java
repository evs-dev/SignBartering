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
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND || e.getPlayer().isSneaking()) return;

        Block block = e.getClickedBlock();

        if (SBUtil.isBlockStateContainer(block.getState())) {
            onContainerInteractedWith(e);
        } else if (SBUtil.isWallSign(block.getType())) {
            onSignInteractedWith(e);
        }
    }

    private void onContainerInteractedWith(PlayerInteractEvent e) {
        Block signBlock = SBUtil.findSurroundingSellingSignBlock(e.getClickedBlock());

        if (signBlock == null) return;

        if (!BarteringSign.playerIsSignOwner(e.getPlayer(), (Sign) signBlock.getState())) {
            Errors.showUserError(Errors.NOT_THE_OWNER, e.getPlayer());
            e.setCancelled(true);
        }

        return;
    }

    private void onSignInteractedWith(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Sign sign = (Sign) block.getState();

        // Is [Selling] sign?
        BarteringSign barteringSign;
        try {
            barteringSign = new BarteringSign(sign);
        } catch (BarteringSignCreationException error) {
            Errors.showUserError(error.getError(), e.getPlayer());
            return;
        }

        String[] lines = sign.getLines();

        Inventory containerInv = ((InventoryHolder)SBUtil.getBehindBlock(block).getState()).getInventory();
        ItemStack sellingItemStack = barteringSign.getSellingItemStack();

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
            String appendix = "The shop owner " + lines[3] + ChatColor.RED;
            Player owner = barteringSign.getSignOwner();

            if (owner != null) {
                Location blockLocation = block.getLocation();
                Errors.showUserError(Errors.OWNERS_SHOP_OUT_OF_STOCK, owner,
                    e.getPlayer().getDisplayName(),
                    SBUtil.cleanName(sellingItemStack.getType().toString()),
                    Integer.toString(sellingItemStack.getAmount()),
                    blockLocation.getBlockX(),
                    blockLocation.getBlockY(),
                    blockLocation.getBlockZ()
                );
                appendix += " has been notified";
            } else {
                appendix += " could not be notified as they are offline";
            }

            Errors.showUserError(Errors.OUT_OF_STOCK, e.getPlayer(), appendix);
            return;
        }

        PlayerInventory playerInv = e.getPlayer().getInventory();
        ItemStack payment = barteringSign.getPriceItemStack();

        if (!playerInv.containsAtLeast(payment, 1)) {
            Errors.showUserError(Errors.INSUFFICIENT_FUNDS, e.getPlayer());
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

        e.getPlayer().sendMessage(Main.MESSAGE_PREFIX + "Item(s) received");

        Player owner = barteringSign.getSignOwner();
        if (owner == null) return;

        String buyAlert = String.format("%s bought [%s]x%s for [%s]x%s from your shop!",
            e.getPlayer().getDisplayName(),
            SBUtil.cleanName(sellingItemStack.getType().toString()),
            Integer.toString(sellingItemStack.getAmount()),
            SBUtil.cleanName(payment.getType().toString()),
            Integer.toString(payment.getAmount())
        );

        owner.sendMessage(Main.MESSAGE_PREFIX + buyAlert);
    }

}
