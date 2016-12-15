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
        sorteio = director.buildSorteio(true);
    }
}
