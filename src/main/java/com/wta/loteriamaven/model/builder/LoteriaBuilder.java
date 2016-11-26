package com.wta.loteriamaven.model.builder;

public abstract class LoteriaBuilder {
    protected SorteioBuilder sorteio;

    public abstract void createSorteio();

    public abstract void createSQLInstance();

    public abstract void executeConcursoSorteio();

    public abstract void executeDezenaSorteio();

    public abstract void executeIntervaloSorteio();

    public abstract void executeOrdenarDezena();
    
    public abstract void executeEspeculacaoMenorMaior();

    public abstract void executeEspeculacaoParImpar();

    public abstract void closeSQLInstance();

    public SorteioBuilder getSorteio() {
        return sorteio;
    }
}
