package com.github.vmg.protogen.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapType extends GenericType {
    private AbstractType keyType;
    private AbstractType valueType;

    public MapType(Type type) {
        super(type);
    }

    @Override
    public String getWrapperSuffix() {
        return "Map";
    }

    @Override
    public AbstractType getValueType() {
        if (valueType == null) {
            valueType = resolveGenericParam(1);
        }
        return valueType;
    }

    public AbstractType getKeyType() {
        if (keyType == null) {
            keyType = resolveGenericParam(0);
        }
        return keyType;
    }

    @Override
    public void mapToProto(String field, MethodSpec.Builder method) {
        AbstractType valueType = getValueType();
        if (valueType instanceof ScalarType) {
            method.addStatement("to.$L( from.$L() )",
                    fieldMethod("putAll", field), fieldMethod("get", field));
        } else {
            TypeName typeName = ParameterizedTypeName.get(Map.Entry.class,
                    getKeyType().getJavaType(),
                    getValueType().getJavaType());
            method.beginControlFlow("for ($T pair : from.$L().entrySet())",
                    typeName, fieldMethod("get", field));
            method.addStatement("to.$L( pair.getKey(), toProto( pair.getValue() ) )",
                    fieldMethod("put", field));
            method.endControlFlow();
        }
    }

    @Override
    public void mapFromProto(String field, MethodSpec.Builder method) {
        AbstractType valueType = getValueType();
        if (valueType instanceof ScalarType) {
            method.addStatement("to.$L( from.$L() )",
                    fieldMethod("set", field), fieldMethod("get", field)+"Map");
        } else {
            Type keyType = getKeyType().getJavaType();
            Type valueTypeJava = getValueType().getJavaType();
            TypeName valueTypePb = getValueType().getJavaProtoType();

            ParameterizedTypeName entryType = ParameterizedTypeName.get(ClassName.get(Map.Entry.class), TypeName.get(keyType), valueTypePb);
            ParameterizedTypeName mapType = ParameterizedTypeName.get(Map.class, keyType, valueTypeJava);
            ParameterizedTypeName hashMapType = ParameterizedTypeName.get(HashMap.class, keyType, valueTypeJava);
            String mapName = field+"Map";

            method.addStatement("$T $L = new $T()", mapType, mapName, hashMapType);
            method.beginControlFlow("for ($T pair : from.$L().entrySet())",
                    entryType, fieldMethod("get", field)+"Map");
            method.addStatement("$L.put( pair.getKey(), fromProto( pair.getValue() ) )", mapName);
            method.endControlFlow();
            method.addStatement("to.$L($L)", fieldMethod("set", field), mapName);
        }
    }

    @Override
    public TypeName resolveJavaProtoType() {
        return ParameterizedTypeName.get((ClassName)getRawJavaType(),
                getKeyType().getJavaProtoType(),
                getValueType().getJavaProtoType());
    }

    @Override
    public String getProtoType() {
        AbstractType keyType = getKeyType();
        AbstractType valueType = getValueType();
        if (!(keyType instanceof ScalarType)) {
            throw new IllegalArgumentException("cannot map non-scalar map key: "+this.getJavaType());
        }
        return String.format("map<%s, %s>", keyType.getProtoType(), valueType.getProtoType());
    }
}
