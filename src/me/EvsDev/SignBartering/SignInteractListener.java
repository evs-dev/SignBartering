package me.EvsDev.SignBartering;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SignInteractListener implements Listener {

	@EventHandler
	public void onSignInteractedWith(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;
		
		Block block = e.getClickedBlock();
				
		// Is sign or chest
		Material material = block.getType();
		
		if (SB.isChest(block)) {
			Block infrontBlock = SB.getInFrontBlock(block);
			if (!SB.isWallSign(infrontBlock.getType())) return;
			
			Sign sign = (Sign) infrontBlock.getState();
			if (!LineChecker.perfectFirstLine(sign.getLine(0))) return;
			
			if (!SB.playerIsSignOwner(sign, e.getPlayer())) {
				SB.error(e, "You cannot open this chest as you are not the owner of this shop");
				e.setCancelled(true);
			}
			
			return;
		}
		
		if (!SB.isWallSign(material)) return;		
		
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
		
		PlayerInventory playerInv = e.getPlayer().getInventory();
		ItemStack payment = new ItemStack(priceItemAndQuantity.item, priceItemAndQuantity.quantity);
		
		// Does the player have enough payment?
		if (!playerInv.containsAtLeast(payment, 1)) {
			SB.error(e, SB.error_NoMoney);
			return;
		}
		
		Inventory chestInv = ((Chest)SB.getBehindBlock(block).getState()).getBlockInventory();
		/*
		Material purchaseItem;
		if (sellingItemAndQuantity.item != Material.ENCHANTED_BOOK)
			purchaseItem = sellingItemAndQuantity.item;
		else
			purchaseItem = Material.ENCHANTED_BOOK;
		*/
		
		ItemStack purchase = new ItemStack(sellingItemAndQuantity.item, sellingItemAndQuantity.quantity);
		boolean isEnchantedBook = sellingItemAndQuantity.item == Material.ENCHANTED_BOOK;
		
		// Does the chest behind this sign have enough to give?
		if (!isEnchantedBook) {
			if (!chestInv.containsAtLeast(purchase, 1)) {
				SB.error(e, "The shop is not currently stocked. Contact the shop owner " + lines[3]);
				return;
			}
		} else {
			boolean isStocked = false;
			for (ItemStack itemStack : chestInv.getContents()) {
				if (!(itemStack.getType() == Material.ENCHANTED_BOOK)) {
					isStocked = false;
				} else {
					isStocked = true;
					break;
				}
			}
			if (!isStocked) {
				SB.error(e, "The shop is not currently stocked. Contact the shop owner " + lines[3]);
				return;
			}
		}
		
		playerInv.removeItem(payment); // Take payment
		chestInv.addItem(payment);     // Store payment in container
		
		if (!isEnchantedBook) {
			chestInv.removeItem(purchase); // Take purchase from container
			playerInv.addItem(purchase);   // Give purchase to player
		} else {
			ItemStack toRemove = null;
			for (ItemStack itemStack : chestInv.getContents()) {
				if (itemStack.getType() == Material.ENCHANTED_BOOK) {
					toRemove = itemStack;
					break;
				}
			}
			if (toRemove == null) return;
			chestInv.remove(toRemove);
			playerInv.addItem(toRemove);
		}
		
		e.getPlayer().sendMessage(SB.messagePrefix + "Item(s) received");
		
		String ownerName = SB.removeBracketsFromNameLine(lines[3]);
		Player owner = Bukkit.getPlayer(ownerName);
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
