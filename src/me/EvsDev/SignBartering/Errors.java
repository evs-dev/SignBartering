package me.EvsDev.SignBartering;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public enum Errors {

    NONE(""),
    INSUFFICIENT_FUNDS("You have insufficient funds"),
    INVALID_LINE("Error on line %s"),
    INVALID_SELLING_SIGN("Something is wrong with this SignBartering shop"),
    OUT_OF_STOCK("The shop is not currently stocked. %s"),
    OWNERS_SHOP_OUT_OF_STOCK("%s tried to purchase [%s]x%s from your shop at X: %s Y: %s Z: %s but it is out of stock"),
    NOT_THE_OWNER("You cannot open this container as you are not the owner of this shop"),
    SIGN_NOT_ON_CONTAINER("Sign must be placed on a container")
    ;

    private Errors(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private final String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public static void showUserError(Errors error, Player player) {
        if (error == null || error == Errors.NONE || player == null) return;
        showUserError(error.getErrorMessage(), player);
    }

    public static void showUserError(Errors error, Player player, Object... formatArgs) {
        if (error == null || error == Errors.NONE || player == null) return;
        String message = String.format(error.getErrorMessage(), formatArgs);
        showUserError(message, player);
    }

    private static void showUserError(String message, Player player) {
        player.sendMessage(Main.MESSAGE_PREFIX + ChatColor.RED + message);
    }

}
