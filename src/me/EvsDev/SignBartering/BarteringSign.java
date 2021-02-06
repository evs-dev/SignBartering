package me.EvsDev.SignBartering;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BarteringSign {

    public BarteringSign(Sign sign) throws BarteringSignCreationException {
        if (!LineChecker.perfectFirstLine(sign.getLine(0))) throw new BarteringSignCreationException();
        //this.sign = sign; Might be used later
        sellingItemStack = LineChecker.parseItemAndQuantityLine(sign.getLine(1), SBUtil.formattedItemQuantitySeparator);
        if (sellingItemStack == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);
        priceItemStack = LineChecker.parseItemAndQuantityLine(sign.getLine(2), SBUtil.formattedItemQuantitySeparator);
        if (priceItemStack == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);
        this.signOwner = getOwnerFromLine(sign.getLine(3));
        if (signOwner == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);
    }

    //private final Sign sign;
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
        String signName = SBUtil.removeBracketsFromNameLine(sign.getLine(3));
        return signName.equals(player.getDisplayName());
    }

    public static boolean playerIsSignOwner(Player player, BarteringSign sign) {
        String signName = sign.getSignOwner().getDisplayName();
        return signName.equals(player.getDisplayName());
    }

    private static Player getOwnerFromLine(String ownerLine) {
        String ownerName = SBUtil.removeBracketsFromNameLine(ownerLine);
        return Bukkit.getPlayer(ownerName);
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
