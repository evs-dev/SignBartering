package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.inventory.ItemStack;

import me.EvsDev.SignBartering.BarteringSign;
import me.EvsDev.SignBartering.SignEditListener;

public interface IItemStackLineFormatter {

    /** Used by {@link SignEditListener.onSignChange} */
    Object interpretPlayerFormattedLine(String line);
    /** Used by {@link SignEditListener.validateItemStackLineResult} */
    boolean isValid(Object result);
    /** Used by {@link SignEditListener.onSignChange} */
    String formatSelfInterpretedLine(Object result);
    /** Used by {@link BarteringSign} */
    ItemStack interpretPluginFormattedLine(String line);

}
