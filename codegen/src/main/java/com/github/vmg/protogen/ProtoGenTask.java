package com.github.vmg.protogen;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class ProtoGenTask extends DefaultTask {
    private String protoPackage;
    private String javaPackage;
    private String goPackage;

    private File protosDir;
    private File mapperDir;
    private String mapperPackage;

    private File sourceJar;
    private String sourcePackage;

    public String getProtoPackage() {
        return protoPackage;
    }

    public void setProtoPackage(String protoPackage) {
        this.protoPackage = protoPackage;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getGoPackage() {
        return goPackage;
    }

    public void setGoPackage(String goPackage) {
        this.goPackage = goPackage;
    }

    public File getProtosDir() {
        return protosDir;
    }

    public void setProtosDir(File protosDir) {
        this.protosDir = protosDir;
    }

    public File getMapperDir() {
        return mapperDir;
    }

    public void setMapperDir(File mapperDir) {
        this.mapperDir = mapperDir;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public File getSourceJar() {
        return sourceJar;
    }

    public void setSourceJar(File sourceJar) {
        this.sourceJar = sourceJar;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    @TaskAction
    public void generate() {
        ProtoGen generator = new ProtoGen(protoPackage, javaPackage, goPackage);
        try {
            generator.processPackage(sourceJar, sourcePackage);
            generator.writeMapper(mapperDir, mapperPackage);
            generator.writeProtos(protosDir);
        } catch (IOException e) {
            System.err.printf("protogen: failed with %s\n", e);
        }
    }
}
