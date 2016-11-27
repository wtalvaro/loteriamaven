package com.wta.loteriamaven.model.delegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NavigableMap;

public interface RemoverDezenas {
    /**
     * @param vet
     * @param par
     * @param impar
     * @param randomPar
     * @param randomImpar
     * @param p
     * @param isPar
     * @return
     */
    public void doRemoverDezenas(Integer ultimo, ArrayList<Integer> par,
                                                          ArrayList<Integer> impar, Integer randomPar,
                                                          Integer randomImpar, double p);

}
