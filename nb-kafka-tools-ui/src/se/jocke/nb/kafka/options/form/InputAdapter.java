package se.jocke.nb.kafka.options.form;

/**
 *
 * @author jocke
 */
public abstract class InputAdapter {

    abstract String getValueAsString();

    abstract void setValueFromString(String value);
    
}
