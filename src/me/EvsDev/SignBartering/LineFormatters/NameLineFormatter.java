package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.entity.Player;

import me.EvsDev.SignBartering.BarteringSign;
import me.EvsDev.SignBartering.SignEditListener;

public abstract class NameLineFormatter {

    /** Used by {@link SignEditListener.onSignChange} */
    public abstract String formatPlayerFormattedLine(String line, Player signMaker);
    /** Used by {@link BarteringSign} */
    public abstract Player interpretSelfFormattedLine(String line);

}
