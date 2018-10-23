package com.github.vmg.protogen.types;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractType {
    Type javaType;
    TypeName javaProtoType;

    AbstractType(Type javaType, TypeName javaProtoType) {
        this.javaType = javaType;
        this.javaProtoType = javaProtoType;
    }

    public Type getJavaType() {
        return javaType;
    }

    public TypeName getJavaProtoType() {
        return javaProtoType;
    }

    public abstract String getProtoType();
    public abstract TypeName getRawJavaType();
    public abstract void mapToProto(String field, MethodSpec.Builder method);
    public abstract void mapFromProto(String field, MethodSpec.Builder method);

    public abstract void getDependencies(Set<String> deps);
    public abstract void generateAbstractMethods(Set<MethodSpec> specs);

    protected String javaMethodName(String m, String field) {
        String fieldName = field.substring(0, 1).toUpperCase() + field.substring(1);
        return m + fieldName;
    }

    protected String protoMethodName(String m, String field) {
        CaseFormat cs = field.contains("_") ? CaseFormat.LOWER_UNDERSCORE : CaseFormat.LOWER_CAMEL;
        return m + cs.to(CaseFormat.UPPER_CAMEL, field);
    }
}
