package me.EvsDev.SignBartering;

import me.EvsDev.SignBartering.LineFormatters.BasicNameLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.BasicSellingLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.HasCostPriceLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.NameLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.PriceLineFormatter;
import me.EvsDev.SignBartering.LineFormatters.SellingLineFormatter;
import net.md_5.bungee.api.ChatColor;

public enum FirstLine {

    SELL("sell", "SELLING", new BasicSellingLineFormatter(), new HasCostPriceLineFormatter(), new BasicNameLineFormatter(), "selling")
    ;

    FirstLine(String name, String perfectName,
            SellingLineFormatter sellingLineFormatter, PriceLineFormatter priceLineFormatter, NameLineFormatter nameLineFormatter,
            String... aliases
            ) {
        this.perfectName = perfectName;
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
    private final SellingLineFormatter sellingLineFormatter;
    private final PriceLineFormatter priceLineFormatter;
    private final NameLineFormatter nameLineFormatter;

    public SellingLineFormatter getSellingLineFormatter() {
        return sellingLineFormatter;
    }

    public PriceLineFormatter getPriceLineFormatter() {
        return priceLineFormatter;
    }

    public NameLineFormatter getNameLineFormatter() {
        return nameLineFormatter;
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
