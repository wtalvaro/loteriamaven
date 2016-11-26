package com.wta.loteriamaven.model.delegate;

import java.util.ArrayList;
import java.util.HashMap;

public interface RandomDezena {
    /**
     * @param n
     * @param p
     * @param dezenas
     * @param isPar
     * @return
     */
    public ArrayList<Integer> doRandom(int n, double p, HashMap<Integer, Double> dezenas, Boolean isPar);
}
