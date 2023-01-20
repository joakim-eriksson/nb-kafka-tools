package se.jocke.nb.kafka.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;

public class ActionCommandDispatcher implements ActionListener {

    private final Map<String, Consumer<ActionEvent>> commands;

    public ActionCommandDispatcher(Map<String, Consumer<ActionEvent>> commands) {
        this.commands = new HashMap<>(commands);
    }

    public static ActionCommandDispatcher ok(Consumer<ActionEvent> consumer) {
        return of("ok", consumer);
    }

    public static ActionCommandDispatcher cancel(Consumer<ActionEvent> consumer) {
        return of("cancel", consumer);
    }

    public static ActionCommandDispatcher of(String command, Consumer<ActionEvent> consumer) {
        return new ActionCommandDispatcher(Collections.singletonMap(command, consumer));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (commands.containsKey(e.getActionCommand().toLowerCase())) {
            commands.get(e.getActionCommand().toLowerCase()).accept(e);
        }
    }

    public static ActionCommandDispatcher onAction(ActionCommandDispatcher dispatcher, ActionCommandDispatcher... commands) {
        return Stream.concat(Stream.of(dispatcher), Arrays.asList(commands)
                .stream())
                .flatMap(acd -> acd.commands.entrySet().stream())
                .collect(Collectors.collectingAndThen(toMap(Entry::getKey, Entry::getValue), ActionCommandDispatcher::new));
    }
}