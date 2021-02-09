package se.jocke.nb.kafka.options.form;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 *
 * @author jocke
 */
public abstract class InputAdapter<S> {

    private final Set<Consumer<UpdateEvent<S>>> events;

    public InputAdapter() {
        this.events = new CopyOnWriteArraySet<>();
    }

    public InputAdapter(Set<Consumer<UpdateEvent<S>>> events) {
        this.events = events;
    }

    public void addChangeListener(Consumer<UpdateEvent<S>> consumer) {
        events.add(consumer);
    }

    public void removeChangeListener(Consumer<UpdateEvent<S>> consumer) {
        events.remove(consumer);
    }

    public void onChange(UpdateEvent<S> update) {
        events.forEach(c -> c.accept(update));
    }

    abstract String getValueAsString();

    abstract void setValueFromString(String value);

}
