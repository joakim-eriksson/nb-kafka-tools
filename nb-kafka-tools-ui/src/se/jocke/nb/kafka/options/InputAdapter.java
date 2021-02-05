package se.jocke.nb.kafka.options;

import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public interface InputAdapter extends Lookup.Provider {

    String getValueAsString();

    void setValueFromString(String value);
}
