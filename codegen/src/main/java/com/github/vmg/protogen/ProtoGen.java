package com.github.vmg.protogen;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ProtoGen {
    private final static String GENERATOR_NAME = "ProtoGen";

    private String protoPackageName;
    private String javaPackageName;
    private String goPackageName;
    private List<File> files = new ArrayList<>();

    public ProtoGen(String protoPackageName, String javaPackageName, String goPackageName) {
        this.protoPackageName = protoPackageName;
        this.javaPackageName = javaPackageName;
        this.goPackageName = goPackageName;
    }

    public void writeMapper(String mapperPackageName, String root) throws Exception {
        TypeSpec.Builder protoMapper = TypeSpec.classBuilder("AbstractProtoMapper")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", GENERATOR_NAME).build());

        Set<MethodSpec> abstractMethods = new HashSet<>();

        for (File file : files) {
            Element elem = file.getMessage();
            elem.generateJavaMapper(protoMapper);
            elem.generateAbstractMethods(abstractMethods);
        }

        protoMapper.addMethods(abstractMethods);

        JavaFile javaFile = JavaFile.builder(mapperPackageName, protoMapper.build())
                .indent("    ").build();
        Path filename = Paths.get(root, "AbstractProtoMapper.java");
        try (Writer writer = new FileWriter(filename.toString())) {
            javaFile.writeTo(writer);
        }
    }

    public void writeProtos(String root) throws Exception {
        TemplateLoader loader = new FileTemplateLoader("protogen/templates", ".proto");
        Handlebars handlebars = new Handlebars(loader)
                .infiniteLoops(true)
                .prettyPrint(true)
                .with(EscapingStrategy.NOOP);

        Template protoFile = handlebars.compile("file");

        for (File file : files) {
            Path filename = Paths.get(root, file.getFilePath());
            try (Writer writer = new FileWriter(filename.toString())) {
                protoFile.apply(file, writer);
            }
        }
    }

    public void process(Class obj) throws Exception {
        files.add(new File(obj, protoPackageName, javaPackageName, goPackageName));
    }
}
