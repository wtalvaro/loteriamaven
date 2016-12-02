package com.wta.loteriamaven.restful;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("resources")
public class GenericApplication extends javax.ws.rs.core.Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Register root resources.
        classes.add(PersonService.class);

        // Register provider classes.

        return classes;
    }
}
