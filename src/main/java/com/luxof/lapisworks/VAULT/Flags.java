package com.luxof.lapisworks.VAULT;

import java.util.Arrays;
import java.util.List;

/** syntax sugar */
public class Flags {
    private final List<Integer> flags;

    public static final int EQ_TRINKETS = 0;
    public static final int EQ_TRINKETS_INVITEM = 1;
    public static final int HANDS = 1;
    public static final int HANDS_INVITEM = 2;
    public static final int HOTBAR = 3;
    public static final int HOTBAR_INVITEM = 4;
    public static final int INVENTORY = 5;
    public static final int INVENTORY_INVITEM = 6;


    public static final Flags PRESET_EVERYTHING = Flags.build(
        Flags.EQ_TRINKETS, Flags.HANDS, Flags.HOTBAR, Flags.INVENTORY,
        Flags.EQ_TRINKETS_INVITEM, Flags.HANDS_INVITEM, Flags.HOTBAR_INVITEM, Flags.INVENTORY_INVITEM
    );
    /** use case: finding Amel */
    public static final Flags PRESET_UpToHotbar = Flags.build(
        Flags.EQ_TRINKETS, Flags.HANDS, Flags.HOTBAR,
        Flags.EQ_TRINKETS_INVITEM, Flags.HANDS_INVITEM, Flags.HOTBAR_INVITEM
    );


    private Flags(List<Integer> flags) { this.flags = flags; };
    /** Build <code>Flags</code> for use.
     * <p>See the PRESET_* constants here for examples. */
    public static Flags build(int... flags) { return new Flags(Arrays.stream(flags).boxed().toList()); }

    public boolean has(int flag) {
        return this.flags.contains(flag);
    }
}
