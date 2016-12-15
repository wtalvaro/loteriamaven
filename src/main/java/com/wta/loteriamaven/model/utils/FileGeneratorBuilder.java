package com.wta.loteriamaven.model.utils;

public abstract class FileGeneratorBuilder {
    protected FileGenerator fileGenerator;

    public abstract void createFileGenerator();

    public abstract void createSQLInstance();

    public abstract void executeQuerySQL();

    public abstract void executeArrayList();

    public abstract void executeBufferedWriter() throws Exception;

    public abstract void closeSQLInstance();

    public FileGenerator getFile() {
        return fileGenerator;
    }
}
