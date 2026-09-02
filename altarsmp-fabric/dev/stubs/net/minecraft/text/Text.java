package net.minecraft.text;
public interface Text {
    static MutableText literal(String s) { return new MutableText(s); }
    String getString();
    final class MutableText implements Text {
        private final String s;
        public MutableText(String s){this.s=s;}
        public MutableText italic(boolean v){return this;}
        public MutableText formatted(/*Formatting*/ Object... f){return this;}
        public String getString(){return s;}
    }
}
