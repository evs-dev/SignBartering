package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("all")
public class BarteringSign {

    public BarteringSign(Sign sign) throws BarteringSignCreationException {
        if (!LineChecker.perfectFirstLine(sign.getLine(0))) throw new BarteringSignCreationException();
        this.sign = sign;
        this.location = sign.getLocation();
        sellingItemStack = LineChecker.parseItemAndQuantityLine(sign.getLine(1), SB.formattedItemQuantitySeparator);
        if (sellingItemStack == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);
        priceItemStack = LineChecker.parseItemAndQuantityLine(sign.getLine(2), SB.formattedItemQuantitySeparator);
        if (priceItemStack == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);
        this.signOwner = SB.getSignOwner(sign.getLine(3));
        if (signOwner == null) throw new BarteringSignCreationException(Errors.INVALID_SELLING_SIGN);
    }



    private final Sign sign;
    private final Location location;
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

    public boolean playerIsSignOwner(Player player) {
        String signName = SB.removeBracketsFromNameLine(signOwner.getDisplayName());
        return signName.equals(player.getDisplayName());
    }

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
