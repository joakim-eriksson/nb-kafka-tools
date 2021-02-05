package se.jocke.nb.kafka.options;

import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 *
 * @author jocke
 */
public final class DataFormInputVerifier<C extends JComponent, T> extends InputVerifier {

    private final InputAdapter inputAdapter;

    private final InputConverter<T> converter;

    public DataFormInputVerifier(InputAdapter inputAdapter, InputConverter<T> converter) {
        this.inputAdapter = inputAdapter;
        this.converter = converter;
        this.inputAdapter.getLookup().lookupResult(String.class).addLookupListener(r -> verify(null));
    }

    @Override
    public boolean verify(JComponent component) {
        T fromString;
        try {
            fromString = getValue();
        } catch (IllegalArgumentException e) {
            return false;
        }
        return fromString != null;
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

    boolean isValid() {
        return verify(null);
    }
}
