package se.jocke.nb.kafka.action;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.AbstractAction;

public class Actions {

    private Actions() {
    }

    public static AbstractAction createAction(String name, Runnable runnnable) {
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
    
    public static AbstractAction[] actionsOf(AbstractAction... actions) {
        return actions;
    }
}
