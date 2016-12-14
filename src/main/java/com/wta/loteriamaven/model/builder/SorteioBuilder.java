package com.wta.loteriamaven.model.builder;

import com.wta.loteriamaven.model.delegate.OrdenarValor;
import com.wta.loteriamaven.model.delegate.RandomDezena;
import com.wta.loteriamaven.model.delegate.RemoverDezenas;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import weka.classifiers.rules.OneR;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class SorteioBuilder extends LoteriaBuilder implements RandomDezena, RemoverDezenas, OrdenarValor {
    private PreparedStatement p_stmt;
    private ResultSet rs;
    private Connection conn;

    private HashMap<Integer, Double> dezenasPeso;
    private HashMap<Integer, LocalDate> intervalosDezenas;
    private HashMap<Integer, Long> intervalosUltimoSorteio;
    private HashMap<Integer, Double> dezenasEscolhidas;
    private NavigableMap<Integer, Double> resultado_parcial;
    private NavigableMap<Integer, Double> resultado_intermediario;
    private Map<Integer, Double> dezenasOrdenadas;
    private NavigableMap<LocalDate, ArrayList<Integer>> resultado_final;
    private ArrayList<String> resultadoPattern;

    private int totalNumeroSorteios;
    private int total_pares;
    private int total_impares;
    private int metade_baixa;
    private int metade_alta;
    private int primeira_baixa;
    private int segunda_baixa;
    private int terceira_baixa;
    private int primeira_alta;
    private int segunda_alta;
    private int terceira_alta;
    private int total_sorteados;
    private int quantidade;
    private int repeticoes;
    private int linha;

    public enum ESPECULACAO_STATUS {
        PAR,
        IMPAR,
        MENOR,
        MAIOR
    };

    public SorteioBuilder(int quantidade, int repeticoes) {
        if (quantidade < 29)
            throw new IllegalArgumentException("A quantidade de dezenas não pode ser menor que 29. Por favor escolha um número que seja maior ou igual a 29.");
        this.quantidade = quantidade;
        this.repeticoes = repeticoes;
        this.resultado_final = new TreeMap<LocalDate, ArrayList<Integer>>();
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

            if (rs.next()) {
                this.total_pares = rs.getInt(1);
                this.total_impares = rs.getInt(2);
                this.metade_baixa = rs.getInt(3);
                this.metade_alta = rs.getInt(4);
                this.primeira_baixa = rs.getInt(5);
                this.segunda_baixa = rs.getInt(6);
                this.terceira_baixa = rs.getInt(7);
                this.primeira_alta = rs.getInt(8);
                this.segunda_alta = rs.getInt(9);
                this.terceira_alta = rs.getInt(10);
                this.totalNumeroSorteios = rs.getInt(11);
                this.total_sorteados = rs.getInt(12);
            }
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
            set_intervalo = this.intervalosUltimoSorteio.entrySet();
            iterator_intervalo = set_intervalo.iterator();
            while (rs.next()) {
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

    private void executeEspeculacaoMenorMaior() {
        // TODO Implement this method
        double probabilidadeMenor = 0d;
        double probabilidadeMaior = 0d;
        probabilidadeMenor = (double) this.metade_baixa / this.total_sorteados;
        probabilidadeMaior = (double) this.metade_alta / this.total_sorteados;
        resultado_parcial = new TreeMap<Integer, Double>();
        if (this.metade_baixa > this.metade_alta) {
            resultado_parcial = doRandom(14, probabilidadeMenor, dezenasEscolhidas, ESPECULACAO_STATUS.MENOR);
        }
        if (this.metade_alta > this.metade_baixa) {
            resultado_parcial = doRandom(14, probabilidadeMaior, dezenasEscolhidas, ESPECULACAO_STATUS.MAIOR);
        }
    }

    private void executeEspeculacaoParImpar() {
        // TODO Implement this method
        double probabilidadePar = 0d;
        double probabilidadeImpar = 0d;
        probabilidadePar = (double) this.total_pares / this.total_sorteados;
        probabilidadeImpar = (double) this.total_impares / this.total_sorteados;
        resultado_intermediario = new TreeMap<Integer, Double>();
        if (this.total_pares > this.total_impares) {
            resultado_intermediario = doRandom(6, probabilidadePar, dezenasEscolhidas, ESPECULACAO_STATUS.PAR);
        }
        if (this.total_impares > total_pares) {
            resultado_intermediario = doRandom(6, probabilidadeImpar, dezenasEscolhidas, ESPECULACAO_STATUS.IMPAR);
        }
        ArrayList<Integer> novoMapa = new ArrayList<Integer>();
        Set set = resultado_intermediario.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            novoMapa.add(Integer.parseInt(me.getKey().toString()));
        }
        this.resultado_final.put(LocalDate.now(), novoMapa);
    }

    @Override
    public void executeEspeculacao() {
        // TODO Implement this method
        for (int i = 1; i <= this.repeticoes; i++) {
            this.linha = i;
            executeEspeculacaoMenorMaior();
            executeEspeculacaoParImpar();
            System.out.print(this.toString() + "\n");
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
    public NavigableMap<Integer, Double> doRandom(int n, double p, HashMap<Integer, Double> dezenas,
                                                  ESPECULACAO_STATUS esp) {
        // TODO Implement this method
        NavigableMap<Integer, Double> treeMap = new TreeMap<Integer, Double>();
        ArrayList<Integer> primeira_especulacao = new ArrayList<Integer>();
        ArrayList<Integer> segunda_especulacao = new ArrayList<Integer>();
        Random generatorPrimeira = new Random();
        Random generatorSegunda = new Random();
        Integer dezena_escolhida = 0;
        Set<?> set_inserir = dezenas.entrySet();
        Iterator<?> iterator_inserir = set_inserir.iterator();
        while (iterator_inserir.hasNext()) {
            Map.Entry me = (Map.Entry) iterator_inserir.next();
            if (Double.parseDouble(me.getValue().toString()) == 1) {
                treeMap.put(Integer.parseInt(me.getKey().toString()), Double.parseDouble(me.getValue().toString()));
                dezenas.remove(me.getKey(), me.getValue());
                n--;
            }
        }
        Set<?> set_remover = dezenas.entrySet();
        Iterator<?> iterator_remover = set_remover.iterator();
        while (iterator_remover.hasNext()) {
            Map.Entry me = (Map.Entry) iterator_remover.next();
            if (Integer.parseInt(me.getKey().toString()) % 2 == 0) {
                primeira_especulacao.add(Integer.parseInt(me.getKey().toString()));
                continue;
            }
            segunda_especulacao.add(Integer.parseInt(me.getKey().toString()));
        }
        for (int i = 0; i < n; i++) {
            Integer[] entriesPrimeira = primeira_especulacao.toArray(new Integer[primeira_especulacao.size()]);
            Integer[] entriesSegunda = segunda_especulacao.toArray(new Integer[segunda_especulacao.size()]);

            Integer randomPrimeira =
                Integer.parseInt(entriesPrimeira[generatorPrimeira.nextInt(entriesPrimeira.length)].toString());
            Integer randomSegunda =
                Integer.parseInt(entriesSegunda[generatorSegunda.nextInt(entriesSegunda.length)].toString());
            if (esp == ESPECULACAO_STATUS.PAR || esp == ESPECULACAO_STATUS.MENOR) {
                dezena_escolhida = (Math.random() < p) ? randomPrimeira : randomSegunda;
                treeMap.put(dezena_escolhida, 0d);
            }
            if (esp == ESPECULACAO_STATUS.IMPAR || esp == ESPECULACAO_STATUS.MAIOR) {
                dezena_escolhida = (Math.random() < p) ? randomSegunda : randomPrimeira;
                treeMap.put(dezena_escolhida, 0d);
            }
            doRemoverDezenas(dezena_escolhida, primeira_especulacao, segunda_especulacao, randomPrimeira, randomSegunda,
                             p);
        }
        Set<?> set_valor = dezenas.entrySet();
        Iterator<?> iterator_valor = set_valor.iterator();
        while (iterator_valor.hasNext()) {
            Map.Entry me = (Map.Entry) iterator_valor.next();
            for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
                Integer key = entry.getKey();
                if (Integer.parseInt(me.getKey().toString()) == key)
                    entry.setValue(Double.parseDouble(me.getValue().toString()));
            }
        }
        return treeMap;
    }

    @Override
    public void doRemoverDezenas(Integer ultimo, ArrayList<Integer> primeira_especulacao,
                                 ArrayList<Integer> segunda_especulacao, Integer randomPrimeira, Integer randomSegunda,
                                 double p) {
        // TODO Implement this method
        if (ultimo % 2 == 0) {
            for (int j = 0; j < primeira_especulacao.size(); j++) {
                if (ultimo == primeira_especulacao.get(j)) {
                    primeira_especulacao.remove(j);
                    break;
                }
            }
        } else {
            for (int j = 0; j < segunda_especulacao.size(); j++) {
                if (ultimo == segunda_especulacao.get(j)) {
                    segunda_especulacao.remove(j);
                    break;
                }
            }
        }
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

    @Override
    public String toString() {
        // TODO Implement this method
        this.resultadoPattern = new ArrayList<String>();
        NavigableMap<Integer, Double> res = sorteio.getResultado_intermediario();
        int last = res.lastKey();
        StringBuilder sb = new StringBuilder();
        sb.append("jogo " + linha + " = ");
        for (Map.Entry entry : res.entrySet()) {
            if (Integer.parseInt(entry.getKey().toString()) == last) {
                sb.append(entry.getKey() + "\n");
                resultadoPattern.add(entry.getKey().toString());
                break;
            }
            resultadoPattern.add(entry.getKey().toString());
            sb.append(entry.getKey() + " - ");
        }

        try {
            DataSource ds = new DataSource("src/main/resources/neuron.arff");
            Instances ins = ds.getDataSet();
            ins.setClassIndex(7);
            OneR nb = new OneR();
            nb.buildClassifier(ins);
            Instance novo = new DenseInstance(8);
            novo.setDataset(ins);
            Long millis = System.currentTimeMillis();
            novo.setValue(0, millis);
            novo.setValue(1, Double.parseDouble(resultadoPattern.get(0)));
            novo.setValue(2, Double.parseDouble(resultadoPattern.get(1)));
            novo.setValue(3, Double.parseDouble(resultadoPattern.get(2)));
            novo.setValue(4, Double.parseDouble(resultadoPattern.get(3)));
            novo.setValue(5, Double.parseDouble(resultadoPattern.get(4)));
            novo.setValue(6, Double.parseDouble(resultadoPattern.get(5)));
            double probabilidade[] = nb.distributionForInstance(novo);
            System.out.println("Sucesso: " + probabilidade[0] + " - Fracasso: " + probabilidade[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    public NavigableMap<Integer, Double> getResultado_intermediario() {
        return resultado_intermediario;
    }

    public TreeMap<LocalDate, ArrayList<Integer>> getResultado_final() {
        return (TreeMap<LocalDate, ArrayList<Integer>>) resultado_final;
    }

    public ArrayList<String> getResultadoPattern() {
        return resultadoPattern;
    }
}
