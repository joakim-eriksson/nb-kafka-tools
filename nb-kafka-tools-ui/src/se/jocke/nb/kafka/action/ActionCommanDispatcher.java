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

public class ActionCommanDispatcher implements ActionListener {

    private final Map<String, Consumer<ActionEvent>> commands;

    public ActionCommanDispatcher(Map<String, Consumer<ActionEvent>> commands) {
        this.commands = new HashMap<>(commands);
    }

    public static ActionCommanDispatcher ok(Consumer<ActionEvent> consumer) {
        return of("ok", consumer);
    }

    public static ActionCommanDispatcher cancel(Consumer<ActionEvent> consumer) {
        return of("cancel", consumer);
    }

    public static ActionCommanDispatcher of(String command, Consumer<ActionEvent> consumer) {
        return new ActionCommanDispatcher(Collections.singletonMap(command, consumer));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (commands.containsKey(e.getActionCommand().toLowerCase())) {
            commands.get(e.getActionCommand().toLowerCase()).accept(e);
        }
    }

    public static ActionCommanDispatcher onAction(ActionCommanDispatcher dispatcher, ActionCommanDispatcher... commands) {
        return Stream.concat(Stream.of(dispatcher), Arrays.asList(commands)
                .stream())
                .flatMap(acd -> acd.commands.entrySet().stream())
                .collect(Collectors.collectingAndThen(toMap(Entry::getKey, Entry::getValue), ActionCommanDispatcher::new));
    }
}
