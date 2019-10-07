package com.example;

import com.github.vmg.protogen.annotations.ProtoField;
import com.github.vmg.protogen.annotations.ProtoMessage;

@ProtoMessage
public class Example {
    @ProtoField(id = 1)
    public String name;
    @ProtoField(id = 2)
    public Long count;
}
