package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FreePriceLineFormatter extends PriceLineFormatter {

    @Override
    public Object interpretPlayerFormattedLine(String line) {
        return null;
    }

    @Override
    public boolean isValid(Object result) {
        return true;
    }

    @Override
    public String formatSelfInterpretedLine(Object result) {
        return "";
    }

    @Override
    public ItemStack interpretPluginFormattedLine(String line) {
        return new ItemStack(Material.AIR, 0);
    }

}
