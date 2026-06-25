package com.buyerplugin.service;

public final class PriceCalculator {

    public static final int STACK_SIZE = 64;

    private PriceCalculator() {
    }

    public static double stackPrice(double unitPrice) {
        return unitPrice * STACK_SIZE;
    }

    public static double totalPrice(double unitPrice, int amount) {
        return unitPrice * amount;
    }

    public static String format(double value) {
        if (Math.rint(value) == value) {
            return String.format("%.0f", value);
        }
        return String.format("%.2f", value);
    }
}
