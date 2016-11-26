package com.wta.loteriamaven.model.delegate;

import java.util.HashMap;

public interface OrdenarValor {
    /**
     * @param map
     * @param isDescending
     * @return
     */
    public HashMap doOrdenar(HashMap map, boolean isDescending);
}
