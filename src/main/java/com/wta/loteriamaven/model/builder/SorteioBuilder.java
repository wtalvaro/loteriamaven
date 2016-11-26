package com.wta.loteriamaven.model.builder;

import com.wta.loteriamaven.model.delegate.OrdenarValor;
import com.wta.loteriamaven.model.delegate.RandomDezena;
import com.wta.loteriamaven.model.delegate.RemoverDezenas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SorteioBuilder extends LoteriaBuilder implements RandomDezena, RemoverDezenas, OrdenarValor {
    private PreparedStatement p_stmt;
    private ResultSet rs;
    private Connection conn;

    private HashMap<Integer, Double> dezenasPeso;
    private HashMap<Integer, LocalDate> intervalosDezenas;
    private HashMap<Integer, Long> intervalosUltimoSorteio;
    private HashMap<Integer, Double> dezenasEscolhidas;
    private Map<Integer, Double> dezenasOrdenadas;

    private int totalNumeroSorteios;
    private int quantidade;

    public SorteioBuilder(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public void createSorteio() {
        sorteio = this;
    }

    @Override
    public void createSQLInstance() {
        // TODO Implement this method
        try {
            this.conn = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "postgres");
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    public void executeConcursoSorteio() {
        // TODO Implement this method
        try {
            this.p_stmt = conn.prepareStatement("SELECT * FROM ESPECULACAO");
            this.rs = p_stmt.executeQuery();

            if (rs.next())
                this.totalNumeroSorteios = rs.getInt(11);
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    public void executeDezenaSorteio() {
        // TODO Implement this method
        try {
            this.p_stmt = conn.prepareStatement("SELECT * FROM DEZENA");
            this.rs = p_stmt.executeQuery();
            this.dezenasPeso = new HashMap<Integer, Double>();
            while (rs.next()) {
                double resultado = (double) rs.getInt(2) / (double) this.totalNumeroSorteios;
                dezenasPeso.put(rs.getInt(1), resultado);
            }
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    public void executeIntervaloSorteio() {
        // TODO Implement this method
        try {
            this.p_stmt =
                conn.prepareStatement("SELECT * FROM INTERVALO", ResultSet.TYPE_SCROLL_SENSITIVE,
                                      ResultSet.CONCUR_READ_ONLY);
            this.rs = p_stmt.executeQuery();
            this.intervalosDezenas = new HashMap<Integer, LocalDate>();
            for (int i = 1; i <= 60; i++) {
                int dezena = i;
                LocalDate data = null;

                /*
                 * Percorre o ResultSet até o final para pegar a última data
                 */
                while (rs.next()) {
                    if (rs.getInt(2) == dezena) {
                        data = rs.getDate(1).toLocalDate();
                    }
                }
                intervalosDezenas.put(dezena, data);
                rs.beforeFirst();
            }

            this.intervalosUltimoSorteio = new HashMap<Integer, Long>();
            for (int i = 1; i < intervalosDezenas.size(); i++) {
                intervalosUltimoSorteio.put(i,
                                            Math.abs(LocalDate.now().until(intervalosDezenas.get(i), ChronoUnit.DAYS)));
            }
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executeOrdenarDezena() {
        // TODO Implement this method
        try {
            this.p_stmt = conn.prepareStatement("SELECT * FROM DEZENA");
            this.rs = p_stmt.executeQuery();

            Set<?> set_intervalo = null;
            Iterator<?> iterator_intervalo = null;

            while (rs.next()) {
                set_intervalo = this.intervalosUltimoSorteio.entrySet();
                iterator_intervalo = set_intervalo.iterator();
                while (iterator_intervalo.hasNext()) {
                    Map.Entry me = (Map.Entry) iterator_intervalo.next();
                    if (Integer.parseInt(me.getKey().toString()) == rs.getInt(1)) {
                        int intervaloMaximoTabelaDezena = rs.getInt(3);
                        if (Integer.parseInt(me.getValue().toString()) >= intervaloMaximoTabelaDezena - 39) {
                            this.dezenasPeso.replace(Integer.parseInt(me.getKey().toString()), 1d);
                        }
                    }
                }
            }

            this.dezenasOrdenadas = doOrdenar(dezenasPeso, true);
            Set set_ordenar = dezenasOrdenadas.entrySet();
            Iterator iterator_ordenar = set_ordenar.iterator();
            this.dezenasEscolhidas = new HashMap<Integer, Double>();

            for (int i = 1; i <= quantidade; i++) {
                if (iterator_ordenar.hasNext()) {
                    Map.Entry me = (Map.Entry) iterator_ordenar.next();
                    this.dezenasEscolhidas.put(Integer.parseInt(me.getKey().toString()),
                                               Double.parseDouble(me.getValue().toString()));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    public void executeEspeculacaoMenorMaior() {
        // TODO Implement this method
    }

    @Override
    public void executeEspeculacaoParImpar() {
        // TODO Implement this method
        try {
            this.p_stmt = conn.prepareStatement("SELECT * FROM ESPECULACAO");
            this.rs = p_stmt.executeQuery();

            int par = 0;
            int impar = 0;
            int totalParImpar = 0;

            double probabilidadePar = 0d;
            double probabilidadeImpar = 0d;

            if (rs.next()) {
                par = rs.getInt(1);
                impar = rs.getInt(2);
            }

            totalParImpar = rs.getInt(12);

            probabilidadePar = (double) par / totalParImpar;
            probabilidadeImpar = (double) impar / totalParImpar;

            ArrayList<Integer> par_impar = new ArrayList<Integer>();
            if (par > impar) {
                par_impar = doRandom(6, probabilidadePar, dezenasEscolhidas, true);
            } else {
                par_impar = doRandom(6, probabilidadeImpar, dezenasEscolhidas, false);
            }

            for (Integer iterator : par_impar) {
                System.out.print(iterator + " ");
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    public void closeSQLInstance() {
        // TODO Implement this method
        try {
            this.p_stmt.close();
            this.rs.close();
            this.conn.close();
        } catch (SQLException e) {
            System.out.println(e.getSQLState() + " - " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Integer> doRandom(int n, double p, HashMap<Integer, Double> dezenas, Boolean isPar) {
        // TODO Implement this method
        ArrayList<Integer> vet = new ArrayList<Integer>();
        ArrayList<Integer> par = new ArrayList<Integer>();
        ArrayList<Integer> impar = new ArrayList<Integer>();

        Set<?> set_inserir = dezenas.entrySet();
        Iterator<?> iterator_inserir = set_inserir.iterator();

        while (iterator_inserir.hasNext()) {
            Map.Entry me = (Map.Entry) iterator_inserir.next();
            if (Double.parseDouble(me.getValue().toString()) == 1) {
                vet.add(Integer.parseInt(me.getKey().toString()));
                dezenas.remove(me.getKey(), me.getValue());
                n--;
            }
        }

        Set<?> set_remover = dezenas.entrySet();
        Iterator<?> iterator_remover = set_remover.iterator();

        while (iterator_remover.hasNext()) {
            Map.Entry me = (Map.Entry) iterator_remover.next();
            if (Integer.parseInt(me.getKey().toString()) % 2 == 0) {
                par.add(Integer.parseInt(me.getKey().toString()));
                continue;
            }
            impar.add(Integer.parseInt(me.getKey().toString()));
        }

        Random generatorPar = new Random();
        Random generatorImpar = new Random();

        for (int i = 0; i < n; i++) {
            Integer[] entriesPar = par.toArray(new Integer[par.size()]);
            Integer[] entriesImpar = impar.toArray(new Integer[impar.size()]);

            Integer randomPar = Integer.parseInt(entriesPar[generatorPar.nextInt(entriesPar.length)].toString());
            Integer randomImpar =
                Integer.parseInt(entriesImpar[generatorImpar.nextInt(entriesImpar.length)].toString());
            doRemoverDezenas(vet, par, impar, randomPar, randomImpar, p, isPar);
        }
        return vet;
    }

    @Override
    public ArrayList<Integer> doRemoverDezenas(ArrayList<Integer> vet, ArrayList<Integer> par, ArrayList<Integer> impar,
                                               Integer randomPar, Integer randomImpar, double p, boolean isPar) {
        // TODO Implement this method
        if (isPar) {
            vet.add((Math.random() < p) ? randomPar : randomImpar);
        } else {
            vet.add((Math.random() < p) ? randomImpar : randomPar);
        }

        int ultimo = vet.get(vet.size() - 1);
        if (ultimo % 2 == 0) {
            for (Integer iterator_par : vet) {
                for (int j = 0; j < par.size(); j++) {
                    if (iterator_par == par.get(j))
                        par.remove(j);
                }
            }
            return vet;
        }
        for (Integer iterator_impar : vet) {
            for (int j = 0; j < impar.size(); j++) {
                if (iterator_impar == impar.get(j))
                    impar.remove(j);
            }
        }
        return vet;
    }

    @Override
    @SuppressWarnings({ "oracle.jdeveloper.java.unchecked-conversion-or-cast",
                        "oracle.jdeveloper.java.unchecked-conversion" })
    public HashMap doOrdenar(HashMap map, boolean isDescending) {
        // TODO Implement this method
        LinkedList list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        Iterator it = null;

        if (!isDescending)
            it = list.iterator();
        if (isDescending)
            it = list.descendingIterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
