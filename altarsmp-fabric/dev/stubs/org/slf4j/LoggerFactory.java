package org.slf4j;
public final class LoggerFactory {
    private LoggerFactory() {}
    public static Logger getLogger(String name) { return new Logger() {
        public void info(String m, Object... a) { System.out.println("[INFO] " + fmt(m, a)); }
        public void info(String m) { System.out.println("[INFO] " + fmt(m, new Object[0])); }
        public void warn(String m, Object... a) { System.out.println("[WARN] " + fmt(m, a)); }
        public void warn(String m) { System.out.println("[WARN] " + fmt(m, new Object[0])); }
        public void error(String m, Object... a) { System.out.println("[ERROR] " + fmt(m, a)); }
        public void error(String m, Throwable t) { System.out.println("[ERROR] " + m); t.printStackTrace(); }
        private String fmt(String m, Object[] a) { String s=m; for (Object o : a) s=s.replaceFirst("\\{\\}", String.valueOf(o)); return s; }
    }; }
}
