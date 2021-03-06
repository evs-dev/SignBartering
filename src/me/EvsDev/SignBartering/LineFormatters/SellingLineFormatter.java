package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.inventory.ItemStack;

public abstract class SellingLineFormatter implements IItemStackLineFormatter {

    @Override
    public abstract Object interpretPlayerFormattedLine(String line);
    @Override
    public abstract boolean isValidSelfInterpretation(Object result);
    @Override
    public abstract String formatSelfInterpretedLine(Object result);
    @Override
    public abstract ItemStack interpretSelfFormattedLine(String line);
    public abstract boolean shouldCheckIfContainerInStock();

}
