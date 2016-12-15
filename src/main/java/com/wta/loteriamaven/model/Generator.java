package com.wta.loteriamaven.model;

import com.wta.loteriamaven.model.utils.FileGenerator;
import com.wta.loteriamaven.model.utils.FileGeneratorBuilder;
import com.wta.loteriamaven.model.utils.FileGeneratorDirector;

public class Generator {
    public Generator() {
        super();
    }

    public static void main(String[] args) {
        FileGeneratorBuilder file_generator = new FileGenerator();
        FileGeneratorDirector director = new FileGeneratorDirector(file_generator);
        file_generator = director.buildFile();
    }
}
