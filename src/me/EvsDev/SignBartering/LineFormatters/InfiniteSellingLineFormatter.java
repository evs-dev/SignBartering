package me.EvsDev.SignBartering.LineFormatters;

import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

import me.EvsDev.SignBartering.LineChecker;
import me.EvsDev.SignBartering.Main;
import me.EvsDev.SignBartering.SBUtil;

public class InfiniteSellingLineFormatter extends SellingLineFormatter {

    @Override
    public Object interpretPlayerFormattedLine(String line) {
        ItemStack result = LineChecker.parseItemAndQuantityLine(line, Main.ITEM_QUANTITY_SEPARATOR);
        if (result == null && line.contains("phantom")) {
            final String firstBit = line.split(Pattern.quote(Main.ITEM_QUANTITY_SEPARATOR))[0];
            line = line.replace(firstBit, "phantom membrane");
            result = LineChecker.parseItemAndQuantityLine(line, Main.ITEM_QUANTITY_SEPARATOR);
        }
        return result;
    }

    @Override
    public boolean isValidSelfInterpretation(Object result) {
        return result != null;
    }

    @Override
    public String formatSelfInterpretedLine(Object result) {
        ItemStack itemStack = (ItemStack) result;
        String formatted = SBUtil.formatItemStackLine(itemStack);
        //formatted = formatted.replace(formatted.split(Pattern.quote(Main.FORMATTED_ITEM_QUANTITY_SEPARATOR))[1], "∞");
        return formatted;
    }

    @Override
    public ItemStack interpretSelfFormattedLine(String line) {
        return LineChecker.parseItemAndQuantityLine(line, Main.FORMATTED_ITEM_QUANTITY_SEPARATOR);
    }

    @Override
    public boolean shouldCheckIfContainerInStock() {
        return false;
    }

}
