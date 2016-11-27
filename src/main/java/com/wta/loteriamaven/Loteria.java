package com.wta.loteriamaven;

import com.wta.loteriamaven.model.builder.LoteriaDirector;
import com.wta.loteriamaven.model.builder.SorteioBuilder;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Loteria {
    public static void main(String[] args) {
        for (int i = 1; i <= 30; i++) {
            SorteioBuilder sorteio = new SorteioBuilder(30);
            LoteriaDirector director = new LoteriaDirector(sorteio);
            sorteio = director.buildSorteio();
            NavigableMap<Integer, Double> res = sorteio.getResultado_final();

            int last = res.lastKey();
            System.out.print("jogo " + i + " = ");
            for (Map.Entry entry : res.entrySet()) {
                if (Integer.parseInt(entry.getKey().toString()) == last) {
                    System.out.print(entry.getKey());
                    break;
                }

                System.out.print(entry.getKey() + " - ");
            }
            System.out.println();
        }
    }
}
