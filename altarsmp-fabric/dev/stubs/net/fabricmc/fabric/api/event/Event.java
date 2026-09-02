package net.fabricmc.fabric.api.event;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
public class Event<T> {
    private final List<T> handlers = new CopyOnWriteArrayList<>();
    public void register(T handler){ handlers.add(handler); }
    public List<T> getHandlers(){ return handlers; }
}
