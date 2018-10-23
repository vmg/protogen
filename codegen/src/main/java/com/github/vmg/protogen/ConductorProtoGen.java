package com.github.vmg.protogen;

import java.io.File;
import java.io.IOException;

public class ConductorProtoGen {
    public static void main(String [] args) throws IOException {
        String rootDir = "/home/vmg/src/gopath/src/github.com/netflix/conductor";

        String protoPackage = "conductor.proto";
        String javaPackage = "com.netflix.conductor.proto";
        String goPackage = "github.com/netflix/conductor/client/gogrpc/conductor/model";

        File protosDir = new File(rootDir, "grpc/src/main/proto");
        File mapperDir = new File(rootDir, "grpc/src/main/java/com/netflix/conductor/grpc");
        String mapperPackage = "com.netflix.conductor.grpc";

        File sourceJar = new File(rootDir, "common/build/libs/conductor-common-1.11.0-SNAPSHOT.jar");
        String sourcePackage = "com.netflix.conductor.common";

        ProtoGen generator = new ProtoGen(protoPackage, javaPackage, goPackage);
        generator.processPackage(sourceJar, sourcePackage);
        generator.writeMapper(mapperDir, mapperPackage);
        generator.writeProtos(protosDir);
    }
}
