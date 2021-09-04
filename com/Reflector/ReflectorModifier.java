package com.Reflector;

public enum ReflectorModifier
{
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected"),
    DEFAULT("default"),
    STATIC("static"),
    FINAL("final"),
    SYNCHRONIZED("synchronized"),
    VOLATILE("volatile"),
    TRANSIENT("transient"),
    NATIVE("native"),
    INTERFACE("interface"),
    ABSTRACT("abstract"),
    STRICT("strict"),
    BRIDGE("bridge"),
    VARARGS("varargs"),
    SYNTHETIC("synthetic"),
    ANNOTATION("annotation"),
    ENUM("enum"),
    LOCAL_CLASS("local class"),
    MEMBER_CLASS("member class");

    private final String string;
    ReflectorModifier(String string) { this.string = string; }
    @Override
    public String toString() { return string; }
}
