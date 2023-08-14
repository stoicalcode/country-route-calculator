package com.stoicalcode.router.model;

import java.util.List;
import java.util.Set;

public enum Region {
    Africa,
    Americas,
    Antarctic,
    Asia,
    Europe,
    Oceania;

    private static final Set<Region> CONTINENTAL = Set.of(Africa, Europe, Asia);

    public boolean isConnectedWith(Region region) {
        if (region == null) {
            return false;
        }

        return region == this || CONTINENTAL.containsAll(List.of(this, region));
    }
}