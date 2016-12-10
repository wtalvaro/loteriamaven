package com.wta.loteriamaven;

import com.wta.loteriamaven.model.builder.LoteriaDirector;
import com.wta.loteriamaven.model.builder.SorteioBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.math.BigInteger;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

public class Loteria {
    public static void main(String[] args) {
        SorteioBuilder sorteio = new SorteioBuilder(45, 10);
        LoteriaDirector director = new LoteriaDirector(sorteio);
        sorteio = director.buildSorteio();

        boolean execute = false;
        if (execute) {
            try {
                PreparedStatement p_stmt;
                ResultSet rs;
                Connection conn;

                ArrayList<String> res = sorteio.getResultadoPattern();
                StringBuilder sb = new StringBuilder();
                ArrayList<String> res2 = new ArrayList<String>();

                int j = 0;
                for (int i = 0; i < res.size(); i++) {
                    sb.append(String.format("%2s", res.get(i)).replace(" ", "0"));
                    j++;

                    if (j % 6 == 0) {
                        res2.add(sb.toString());
                        sb.setLength(0);
                    }
                }

                ArrayList<String> str2 = new ArrayList<String>();
                conn = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "postgres");
                p_stmt = conn.prepareStatement("SELECT * FROM MEGASENA");
                rs = p_stmt.executeQuery();

                int size = 0;
                while (rs.next()) {
                    size++;
                    Integer oneInt = rs.getInt(3);
                    Integer twoInt = rs.getInt(4);
                    Integer threeInt = rs.getInt(5);
                    Integer fourInt = rs.getInt(6);
                    Integer fiveInt = rs.getInt(7);
                    Integer sixInt = rs.getInt(8);
                    String one = String.format("%2s", Integer.toString(oneInt).replace(" ", "0"));
                    String two = String.format("%2s", Integer.toString(twoInt).replace(" ", "0"));
                    String three = String.format("%2s", Integer.toString(threeInt).replace(" ", "0"));
                    String four = String.format("%2s", Integer.toString(fourInt).replace(" ", "0"));
                    String five = String.format("%2s", Integer.toString(fiveInt).replace(" ", "0"));
                    String six = String.format("%2s", Integer.toString(sixInt).replace(" ", "0"));
                    StringBuilder resultado = new StringBuilder();
                    resultado.append(String.format("%2s", one.replace(" ", "0")));
                    resultado.append(String.format("%2s", two.replace(" ", "0")));
                    resultado.append(String.format("%2s", three.replace(" ", "0")));
                    resultado.append(String.format("%2s", four.replace(" ", "0")));
                    resultado.append(String.format("%2s", five.replace(" ", "0")));
                    resultado.append(String.format("%2s", six.replace(" ", "0")));
                    str2.add(resultado.toString());
                }
                ArrayList<String> resultadoArray = new ArrayList<String>();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(size + "\n");
                sb2.append(12 + "\n");
                sb2.append(12 + "\n");

                ArrayList<String> str = new ArrayList<String>();
                str.add("10");

                for (int i = 0; i <= 9; i++) {
                    String sec = String.format("%08d", new BigInteger(Long.toBinaryString(i)));
                    str.add(i + sec);
                }

                Path file =
                    Paths.get("/home/wagner/Public/Projects/JDeveloper/LoteriaMaven/NeuronLoto/classes/com/wta/neuronetwork/ascii2bin.txt");
                Files.write(file, str, Charset.forName("UTF-8"));

                for (int i = 0; i < res2.size(); i++) {
                    sb2.append(res2.get(i) + " " + str2.get(i));
                    resultadoArray.add(sb2.toString());
                    sb2.setLength(0);
                }

                Path file2 =
                    Paths.get("/home/wagner/Public/Projects/JDeveloper/LoteriaMaven/NeuronLoto/classes/com/wta/neuronetwork/pattern.txt");
                Files.write(file2, resultadoArray, Charset.forName("UTF-8"));
            } catch (SQLException e) {
                System.out.println(e.getSQLState() + " - " + e.getMessage());
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
