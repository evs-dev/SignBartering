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

import net.md_5.bungee.api.ChatColor;

public class InteractListener implements Listener {

	@EventHandler
	public void onSignOrContainerInteractedWith(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;
		
		Block block = e.getClickedBlock();
		
		// Is container		
		if (ContainerUtil.isBlockStateContainer(block.getState())) {
			Block signBlock = ContainerUtil.findSurroundingSellingSignBlock(block);
			
			if (signBlock == null) return;
			
			if (!SB.playerIsSignOwner((Sign) signBlock.getState(), e.getPlayer())) {
				SB.error(e, "You cannot open this container as you are not the owner of this shop");
				e.setCancelled(true);
			}
			
			return;
		}
		
		// Is sign
		if (!SB.isWallSign(block.getType())) return;
		
		// Is [Selling] sign
		Sign sign = (Sign) block.getState();
		if (!LineChecker.perfectFirstLine(sign.getLine(0))) return;
		
		String[] lines = sign.getLines();
		
		ItemAndQuantity sellingItemAndQuantity = LineChecker.parseItemAndQuantityLine(lines[1], true);
		ItemAndQuantity priceItemAndQuantity = LineChecker.parseItemAndQuantityLine(lines[2], true);
		
		if (sellingItemAndQuantity == null || priceItemAndQuantity == null) {
			SB.error(e, "This sign is not a valid SignBartering sign (something is wrong with it)");
			return;
		}
		
		// Get container inventory
		Inventory containerInv = ((InventoryHolder)SB.getBehindBlock(block).getState()).getInventory();
		
		// Does the container behind this sign have enough to give?
		boolean isStocked = false;
		int foundAmount = 0;
		for (ItemStack itemStack : containerInv.getStorageContents()) {
			if (itemStack == null) continue;
			if (itemStack.getType() != sellingItemAndQuantity.item) {
				isStocked = false;
			} else if (itemStack.getAmount() >= sellingItemAndQuantity.quantity) {
				isStocked = true;
				break;
			} else {
				foundAmount += itemStack.getAmount();
				if (foundAmount >= sellingItemAndQuantity.quantity) {
					isStocked = true;
					break;
				}
			}
		}
		if (!isStocked) {
			String appendix;
			Player owner = SB.getSignOwner(lines[3]);			
			if (owner != null) {
				Location blockLocation = block.getLocation();
				String noStockMessage = String.format(
						"%s tried to purchase [%s]x%s from your shop at X: %s Y: %s Z: %s but it is out of stock",
						e.getPlayer().getDisplayName(),
						SB.cleanName(sellingItemAndQuantity.item.toString()),
						Integer.toString(sellingItemAndQuantity.quantity),
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
		ItemStack payment = new ItemStack(priceItemAndQuantity.item, priceItemAndQuantity.quantity);
		
		// Does the player have enough payment?
		if (!playerInv.containsAtLeast(payment, 1)) {
			SB.error(e, SB.error_NoMoney);
			return;
		}
		
		// Find purchase in container
		ItemStack purchase = null;
		for (ItemStack itemStack : containerInv.getStorageContents()) {
			if (itemStack == null) continue;
			if (itemStack.getType() == sellingItemAndQuantity.item) {
				purchase = new ItemStack(itemStack);
				purchase.setAmount(sellingItemAndQuantity.quantity);
				break;
			}
		}
		
		playerInv.removeItem(payment);     // Take payment
		containerInv.addItem(payment);     // Store payment in container		
		containerInv.removeItem(purchase); // Take purchase from container
		playerInv.addItem(purchase);       // Give purchase to player
		
		e.getPlayer().sendMessage(SB.messagePrefix + "Item(s) received");
		
		Player owner = SB.getSignOwner(lines[3]);
		if (owner == null) return;
		
		String buyAlert = String.format("%s bought [%s]x%s for [%s]x%s from your shop!",
			e.getPlayer().getDisplayName(),
			SB.cleanName(sellingItemAndQuantity.item.toString()),
			Integer.toString(sellingItemAndQuantity.quantity),
			SB.cleanName(priceItemAndQuantity.item.toString()),
			Integer.toString(priceItemAndQuantity.quantity)
		);
		
		owner.sendMessage(SB.messagePrefix + buyAlert);
	}

	
}
