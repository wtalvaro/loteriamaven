package com.wta.loteriamaven.model.utils;

public class FileGeneratorDirector {
    FileGeneratorBuilder builder;

    public FileGeneratorDirector(FileGeneratorBuilder builder) {
        super();
        this.builder = builder;
    }

    public FileGenerator buildFile() {
        builder.createFileGenerator();
        builder.createSQLInstance();
        builder.executeQuerySQL();
        builder.executeArrayList();
        try {
            builder.executeBufferedWriter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return builder.getFile();
    }
}
