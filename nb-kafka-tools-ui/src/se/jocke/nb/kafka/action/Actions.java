package se.jocke.nb.kafka.action;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class Actions {

    private Actions() {
    }

    public static AbstractAction action(String name, Runnable runnnable) {
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnnable.run();
            }
        };
    }

    public static AbstractAction createAction(String name, Consumer<ActionEvent> consumer) {
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
    }
    
    public static Action[] actions(Action... actions) {
        return actions;
    }
}
