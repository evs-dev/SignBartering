package me.EvsDev.SignBartering;

import me.EvsDev.SignBartering.LineFormatters.AllowEmptyNameLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.BasicNameLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.BasicSellingLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.FreePriceLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.HasCostPriceLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.InfiniteSellingLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.NameLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.PriceLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.SellingLineFormatter;
import net.md_5.bungee.api.ChatColor;

public enum FirstLine {

    SELL("sell", "SELLING", new BasicSellingLineFormatter(), new HasCostPriceLineFormatter(), new BasicNameLineFormatter(), false, "selling", "barter"),
    FREE("free", "FREE", new BasicSellingLineFormatter(), new FreePriceLineFormatter(), new BasicNameLineFormatter(), false, "give"),
    ISELL("isell", "INFINITE", new InfiniteSellingLineFormatter(), new HasCostPriceLineFormatter(), new AllowEmptyNameLineFormatter(), true, "infinite", "inf")
    ;

    FirstLine(String name, String perfectName,
            SellingLineFormatter sellingLineFormatter, PriceLineFormatter priceLineFormatter, NameLineFormatter nameLineFormatter,
            boolean onlyOp, String... aliases
            ) {
        this.perfectName = perfectName;
        this.onlyOp = onlyOp;
        this.names = new String[1 + aliases.length];
        this.names[0] = name;
        for (int i = 0; i < aliases.length; i++) {
            names[i + 1] = aliases[i];
        }
        this.sellingLineFormatter = sellingLineFormatter;
        this.priceLineFormatter = priceLineFormatter;
        this.nameLineFormatter = nameLineFormatter;
    }

    private final String[] names;
    private final String perfectName;
    private final boolean onlyOp;
    private final SellingLineFormatter sellingLineFormatter;
    private final PriceLineFormatter priceLineFormatter;
    private final NameLineFormatter nameLineFormatter;

    public String getPerfectName() {
        return perfectName;
    }

    public SellingLineFormatter getSellingLineFormatter() {
        return sellingLineFormatter;
    }

    public PriceLineFormatter getPriceLineFormatter() {
        return priceLineFormatter;
    }

    public NameLineFormatter getNameLineFormatter() {
        return nameLineFormatter;
    }

    public boolean onlyOp() {
        return onlyOp;
    }

    public static FirstLine interpretFirstLine(String line, boolean perfect) {
        line = ChatColor.stripColor(line);
        if (!(line.startsWith("[") || line.endsWith("]"))) return null;
        line = line.replace("[", "").replace("]", "");
        for (FirstLine firstLine : FirstLine.values()) {
            if (!perfect) {
                for (String name : firstLine.names) {
                    if (line.equalsIgnoreCase(name)) {
                        return firstLine;
                    }
                }
            } else {
                if (line.equalsIgnoreCase(firstLine.perfectName)) {
                    return firstLine;
                }
            }
        }
        return null;
    }

}
