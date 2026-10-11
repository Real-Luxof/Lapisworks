package com.luxof.lapisworks.init;

/** Usually you don't fuck with this, as making a new GenericEnchant takes care of this for you. */
public class EnchantCountKeeper {
    private static int tally = 0;
    /** Doesn't "register" shit, just gives you your new enchantment's index. */
    public static int registerMyEnchantment() {
        tally++;
        return tally - 1;
    }
    /** returns the index of the enchantment that was last registered */
    public static int getTally() {
        return tally - 1;
    }
    /** utility method */
    public static Integer[] getTallyAsZeroes() {
        Integer[] zeroes = new Integer[tally];
        for (int i = 0; i < zeroes.length; i++) { zeroes[i] = 0; }
        return zeroes;
    }
}
