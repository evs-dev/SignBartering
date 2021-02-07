package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.inventory.ItemStack;

import me.EvsDev.SignBartering.LineChecker;
import me.EvsDev.SignBartering.Main;
import me.EvsDev.SignBartering.SBUtil;

public class BasicSellingLineFormatter extends SellingLineFormatter {

    @Override
    public Object interpretPlayerFormattedLine(String line) {
        return LineChecker.parseItemAndQuantityLine(line, Main.ITEM_QUANTITY_SEPARATOR);
    }

    @Override
    public boolean isValid(Object result) {
        return result != null;
    }

    @Override
    public String formatSelfInterpretedLine(Object result) {
        ItemStack itemStack = (ItemStack) result;
        return SBUtil.formatItemStackLine(itemStack);
    }

    @Override
    public ItemStack interpretPluginFormattedLine(String line) {
        return LineChecker.parseItemAndQuantityLine(line, Main.FORMATTED_ITEM_QUANTITY_SEPARATOR);
    }

    @Override
    public boolean shouldCheckIfContainerInStock() {
        return true;
    }

}
