package net.minecraft.util;
public final class Identifier {
    private final String ns, path;
    private Identifier(String ns, String path) { this.ns=ns; this.path=path; }
    public static Identifier of(String ns, String path) { return new Identifier(ns,path); }
    public String getNamespace() { return ns; }
    public String getPath() { return path; }
    public String toString() { return ns + ":" + path; }
    public boolean equals(Object o) { return o instanceof Identifier i && i.ns.equals(ns) && i.path.equals(path); }
    public int hashCode() { return (ns+":"+path).hashCode(); }
}
