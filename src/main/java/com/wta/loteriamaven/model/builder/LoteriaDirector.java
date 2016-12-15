package com.wta.loteriamaven.model.builder;

public class LoteriaDirector {
    LoteriaBuilder builder;

    public LoteriaDirector(LoteriaBuilder builder) {
        super();
        this.builder = builder;
    }

    public SorteioBuilder buildSorteio(boolean toString) {
        builder.createSorteio();
        builder.createSQLInstance();
        builder.executeConcursoSorteio();
        builder.executeDezenaSorteio();
        builder.executeIntervaloSorteio();
        builder.executeOrdenarDezena();
        builder.executeEspeculacao(toString);
        return builder.getSorteio();
    }
}
