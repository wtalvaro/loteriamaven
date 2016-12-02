package com.wta.loteriamaven;

import com.wta.loteriamaven.model.builder.LoteriaDirector;
import com.wta.loteriamaven.model.builder.SorteioBuilder;

public class Loteria {
    public static void main(String[] args) {
        SorteioBuilder sorteio = new SorteioBuilder(45, 30);
        LoteriaDirector director = new LoteriaDirector(sorteio);
        sorteio = director.buildSorteio();
    }
}
