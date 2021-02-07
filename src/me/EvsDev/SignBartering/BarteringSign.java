package me.EvsDev.SignBartering;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BarteringSign {

    public BarteringSign(Sign sign, FirstLine firstLine) throws BarteringSignCreationException {
        sellingItemStack = firstLine.getSellingLineFormatter().interpretPluginFormattedLine(sign.getLine(1));
        if (sellingItemStack == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);

        priceItemStack = firstLine.getPriceLineFormatter().interpretPluginFormattedLine(sign.getLine(2));
        if (priceItemStack == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);

        this.signOwner = firstLine.getNameLineFormatter().interpretPluginFormattedLine(sign.getLine(3));
    }

    private final ItemStack sellingItemStack;
    private final ItemStack priceItemStack;
    private final Player signOwner;

    public ItemStack getSellingItemStack() {
        return sellingItemStack;
    }

    public ItemStack getPriceItemStack() {
        return priceItemStack;
    }

    public Player getSignOwner() {
        return signOwner;
    }

    public static boolean playerIsSignOwner(Player player, Sign sign) {
        if (sign == null || player == null) return false;
        String signName = SBUtil.removeBracketsFromSignLine(sign.getLine(3));
        return signName.equals(player.getDisplayName());
    }

    public static boolean playerIsSignOwner(Player player, BarteringSign sign) {
        if (sign.getSignOwner() == null || player == null) return false;
        String signName = sign.getSignOwner().getDisplayName();
        return signName.equals(player.getDisplayName());
    }

    @SuppressWarnings("serial")
    public class BarteringSignCreationException extends Exception {
        public BarteringSignCreationException() {
            this.error = Errors.NONE;
        }

        public BarteringSignCreationException(Errors error) {
            super(error.getErrorMessage());
            this.error = error;
        }

        public BarteringSignCreationException(Errors lineError, int line) {
            super(String.format(lineError.getErrorMessage(), line));
            this.error = lineError;
        }

        private final Errors error;

        public Errors getError() {
            return error;
        }
    }
}
