package com.luxof.lapisworks.init;

/** Usually you don't fuck with this, as making a new GenericEnchant takes care of this for you. */
public class EnchantCountKeeper {
    private static int tally = -1;
    /** Doesn't "register" shit, just gives you your new enchantment's index. */
    public static int registerMyEnchantment() {
        tally++;
        return tally;
    }
    public static int getTally() {
        return tally;
    }
    /** utility method */
    public static Integer[] getTallyAsZeroes() {
        Integer[] zeroes = new Integer[tally];
        for (int i = 0; i < zeroes.length; i++) { zeroes[i] = 0; }
        return zeroes;
    }
}
