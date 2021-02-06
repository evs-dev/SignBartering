package me.EvsDev.SignBartering;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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

        if (SBUtil.isContainer(block.getState())) {
            onContainerInteractedWith(e);
        } else if (SBUtil.isWallSign(block.getType())) {
            onSignInteractedWith(e);
        }
    }

    private void onContainerInteractedWith(PlayerInteractEvent e) {
        final Sign surroundingSign = SBUtil.findAttachedBarteringSign(e.getClickedBlock());

        if (surroundingSign == null) return;

        if (!BarteringSign.playerIsSignOwner(e.getPlayer(), surroundingSign)) {
            Errors.showUserError(Errors.NOT_THE_OWNER, e.getPlayer());
            e.setCancelled(true);
        }

        return;
    }

    private void onSignInteractedWith(PlayerInteractEvent e) {
        final Block block = e.getClickedBlock();
        final Sign sign = (Sign) block.getState();
        final Player buyer = e.getPlayer();
        final BarteringSign barteringSign = createBarteringSign(sign, buyer);
        if (barteringSign == null) return;
        final ItemStack sellingItemStack = barteringSign.getSellingItemStack();
        // Can assume that the behind block is a container because of SignEditListener validation
        final Inventory containerInv = ((InventoryHolder)SBUtil.getBehindBlock(block).getState()).getInventory();

        if (!inventoryIsStocked(containerInv, sellingItemStack)) {
            showOutOfStockError(barteringSign, block, sellingItemStack, buyer);
            return;
        }

        final PlayerInventory buyerInv = buyer.getInventory();
        final ItemStack payment = barteringSign.getPriceItemStack();

        if (!inventoryIsStocked(buyerInv, payment)) {
            Errors.showUserError(Errors.INSUFFICIENT_FUNDS, buyer);
            return;
        }

        // This has to happen to enable items' NBTs to be transferred
        final List<ItemStack> purchase = findPurchaseInContainer(containerInv, sellingItemStack);

        buyerInv.removeItem(payment);      // Take payment
        containerInv.addItem(payment);     // Store payment in container

        for (ItemStack itemStack : purchase) {
            containerInv.removeItem(itemStack); // Take purchase from container
            buyerInv.addItem(itemStack);        // Give purchase to player
        }

        buyer.sendMessage(Main.MESSAGE_PREFIX + "Item(s) received");

        final Player owner = barteringSign.getSignOwner();
        if (owner == null) return;
        sendBuyAlertToSeller(buyer, owner, sellingItemStack, payment);
    }

    @Nullable
    private BarteringSign createBarteringSign(Sign sign, Player player) {
        BarteringSign barteringSign;
        try {
            barteringSign = new BarteringSign(sign);
        } catch (BarteringSignCreationException error) {
            Errors.showUserError(error.getError(), player);
            return null;
        }
        return barteringSign;
    }

    private boolean inventoryIsStocked(Inventory inventory, ItemStack selling) {
        boolean isStocked = false;
        int foundAmount = 0;
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType() != selling.getType()) {
                isStocked = false;
            } else if (itemStack.getAmount() >= selling.getAmount()) {
                isStocked = true;
                break;
            } else {
                foundAmount += itemStack.getAmount();
                if (foundAmount >= selling.getAmount()) {
                    isStocked = true;
                    break;
                }
            }
        }
        return isStocked;
    }

    private void showOutOfStockError(BarteringSign sign, Block signBlock, ItemStack selling, Player buyer) {
        final Player owner = sign.getSignOwner();
        String appendix = ChatColor.translateAlternateColorCodes('&', "The shop owner &7(")
            + owner.getName()
            + ")"
            + ChatColor.RED;

        if (owner != null && owner.isOnline()) {
            final Location blockLocation = signBlock.getLocation();
            Errors.showUserError(Errors.OWNERS_SHOP_OUT_OF_STOCK, owner,
                buyer.getDisplayName(),
                SBUtil.cleanName(selling.getType().toString()),
                Integer.toString(selling.getAmount()),
                blockLocation.getBlockX(),
                blockLocation.getBlockY(),
                blockLocation.getBlockZ()
            );
            appendix += " has been notified";
        } else {
            appendix += " could not be notified as they are offline";
        }

        Errors.showUserError(Errors.OUT_OF_STOCK, buyer, appendix);
    }

    private List<ItemStack> findPurchaseInContainer(Inventory inventory, ItemStack selling) {
        List<ItemStack> purchase = new ArrayList<>();
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType() == selling.getType()) {
                int currentTotalNumberOfPurchasedItems = 0;
                for (ItemStack purchased : purchase) {
                    currentTotalNumberOfPurchasedItems += purchased.getAmount();
                }
                if (currentTotalNumberOfPurchasedItems < selling.getAmount()) {
                    ItemStack toAdd = new ItemStack(itemStack);
                    toAdd.setAmount(Math.min(selling.getAmount() - currentTotalNumberOfPurchasedItems, itemStack.getAmount()));
                    purchase.add(toAdd);
                } else {
                    break;
                }
            }
        }
        return purchase;
    }

    private void sendBuyAlertToSeller(Player buyer, Player seller, ItemStack selling, ItemStack payment) {
        String buyAlert = String.format("%s bought [%s]x%s for [%s]x%s from your shop!",
            buyer.getDisplayName(),
            SBUtil.cleanName(selling.getType().toString()),
            Integer.toString(selling.getAmount()),
            SBUtil.cleanName(payment.getType().toString()),
            Integer.toString(payment.getAmount())
        );
        seller.sendMessage(Main.MESSAGE_PREFIX + buyAlert);
    }

}
