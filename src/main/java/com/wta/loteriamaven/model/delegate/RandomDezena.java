package com.wta.loteriamaven.model.delegate;

import com.wta.loteriamaven.model.builder.SorteioBuilder;

import java.util.HashMap;
import java.util.NavigableMap;

public interface RandomDezena {
    /**
     * @param n
     * @param p
     * @param dezenas
     * @param esp
     * @return
     * @param
     */
    public NavigableMap<Integer, Double> doRandom(int n, double p, HashMap<Integer, Double> dezenas,
                                                  SorteioBuilder.ESPECULACAO_STATUS esp);
}
