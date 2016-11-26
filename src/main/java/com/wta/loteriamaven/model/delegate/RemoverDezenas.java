package com.wta.loteriamaven.model.delegate;

import java.util.ArrayList;

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
    public ArrayList<Integer> doRemoverDezenas(ArrayList<Integer> vet, ArrayList<Integer> par,
                                                           ArrayList<Integer> impar, Integer randomPar,
                                                           Integer randomImpar, double p, boolean isPar);

}
