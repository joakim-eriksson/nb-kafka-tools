package se.jocke.nb.kafka.options.form;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 *
 * @author jocke
 */
public final class DataFormInputVerifier<C extends JComponent, T> extends InputVerifier {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final InputAdapter<?> inputAdapter;

    private final InputConverter<T> converter;

    private final ValidationFailedDisplay failedDisplay;

    public DataFormInputVerifier(InputAdapter<?> inputAdapter, InputConverter<T> converter, ValidationFailedDisplay failedDisplay) {
        this.inputAdapter = inputAdapter;
        this.converter = converter;
        this.failedDisplay = failedDisplay;
        this.inputAdapter.addChangeListener(e -> verify(null));
    }

    @Override
    public boolean verify(JComponent component) {

        T fromString;
        try {
            fromString = getValue();
        } catch (IllegalArgumentException e) {
            failedDisplay.show(e.getMessage());
            return false;
        }
        failedDisplay.clear();
        return fromString != null;
    }

    public DataFormInputVerifier<C, T> onChange(Runnable callback) {
        this.inputAdapter.addChangeListener(e -> callback.run());
        return this;
    }

    public T getValue() {
        String valueAsString = inputAdapter.getValueAsString();
        return converter.fromString(valueAsString);
    }

    public String getValueAsString() {
        return inputAdapter.getValueAsString();
    }

    public void setValue(String value) {
        inputAdapter.setValueFromString(value);
    }

    public boolean isValid() {
        return verify(null);
    }
}
