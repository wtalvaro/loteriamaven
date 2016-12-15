package com.wta.loteriamaven.model.utils;

import com.wta.loteriamaven.model.builder.LoteriaDirector;
import com.wta.loteriamaven.model.builder.SorteioBuilder;

import java.io.BufferedWriter;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class FileGenerator extends FileGeneratorBuilder {
    private Connection conn;
    private PreparedStatement p_stmt;
    private ResultSet rs;
    private NavigableMap<LocalDate, ArrayList<Integer>> entrada_sucesso;
    private ArrayList<ArrayList<Integer>> entrada_fracasso;
    private String atributos[];

    public FileGenerator() {
        super();
        this.entrada_sucesso = new TreeMap<LocalDate, ArrayList<Integer>>();
        this.atributos = new String[7];
        this.atributos[0] = "data";
        this.atributos[1] = "primeira_dezena";
        this.atributos[2] = "segunda_dezena";
        this.atributos[3] = "terceira_dezena";
        this.atributos[4] = "quarta_dezena";
        this.atributos[5] = "quinta_dezena";
        this.atributos[6] = "sexta_dezena";
    }

    @Override
    public void createFileGenerator() {
        // TODO Implement this method
        fileGenerator = this;
    }

    @Override
    public void createSQLInstance() {
        // TODO Implement this method
        try {
            this.conn = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "postgres");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void executeQuerySQL() {
        // TODO Implement this method
        try {
            this.p_stmt =
                conn.prepareStatement("SELECT DATA_SORTEIO, PRIMEIRA_DEZENA, SEGUNDA_DEZENA, TERCEIRA_DEZENA, QUARTA_DEZENA, QUINTA_DEZENA, SEXTA_DEZENA FROM MEGASENA");
            this.rs = p_stmt.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void executeArrayList() {
        // TODO Implement this method
        try {
            while (rs.next()) {
                ArrayList<Integer> dezenas = new ArrayList<Integer>();
                dezenas.add(rs.getInt(2));
                dezenas.add(rs.getInt(3));
                dezenas.add(rs.getInt(4));
                dezenas.add(rs.getInt(5));
                dezenas.add(rs.getInt(6));
                dezenas.add(rs.getInt(7));
                this.entrada_sucesso.put(rs.getDate(1).toLocalDate(), dezenas);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void executeBufferedWriter() throws Exception {
        // TODO Implement this method
        try {
            BufferedWriter arquivo;
            File file = new File("src/main/resources/neuron.arff");
            arquivo = new java.io.BufferedWriter(new java.io.FileWriter(file));
            arquivo.write("@relation megasena\n");
            arquivo.write("@attribute data date 'yyyy-MM-dd'\n");
            for (int i = 1; i < atributos.length; i++) {
                arquivo.write("@attribute " + atributos[i] + " numeric\n");
            }
            arquivo.write("@attribute sucesso {true, false}\n");
            arquivo.write("@data");
            arquivo.write(dataTrueFalse(60, 1885).toString());
            arquivo.close();
        } catch (Exception e) {
            throw new Exception("Não foi possível criar o arquivo - " + e.getMessage());
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

    @SuppressWarnings("unchecked")
    public StringBuilder dataTrueFalse(int dezenas, int repeticoes) {
        SorteioBuilder sorteio = new SorteioBuilder(dezenas, repeticoes);
        LoteriaDirector director = new LoteriaDirector(sorteio);
        sorteio = director.buildSorteio(false, false);

        this.entrada_fracasso = sorteio.getResultado_final();

        StringBuilder sb = new StringBuilder();
        Set set = entrada_sucesso.entrySet();
        Iterator it = set.iterator();
        int j = 0;
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            LocalDate data = (LocalDate) me.getKey();
            @SuppressWarnings({ "unchecked" })
            ArrayList<Integer> dezena_sucesso = (ArrayList<Integer>) me.getValue();
            sb.append("\n" + data);
            for (int i = 0; i < dezena_sucesso.size(); i++) {
                sb.append(",");
                sb.append(dezena_sucesso.get(i));
            }
            sb.append(",true");
            sb.append("\n");

            ArrayList<Integer> dezena_fracasso = entrada_fracasso.get(j++);
            sb.append(data);
            for (int i = 0; i < dezena_fracasso.size(); i++) {
                sb.append(",");
                sb.append(dezena_fracasso.get(i));
            }
            sb.append(",false");
        }
        return sb;
    }
}
