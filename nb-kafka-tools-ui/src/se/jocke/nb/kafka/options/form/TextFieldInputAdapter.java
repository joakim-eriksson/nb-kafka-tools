package se.jocke.nb.kafka.options.form;

import javax.swing.JTextField;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import se.jocke.nb.kafka.options.InputAdapter;

/**
 *
 * @author jocke
 */
public class TextFieldInputAdapter implements InputAdapter {

    private final JTextField textField;

    private final InstanceContent content;

    private final Lookup lookup;

    public TextFieldInputAdapter(JTextField textField) {
        this.textField = textField;
        this.content = new InstanceContent();
        this.lookup = new AbstractLookup(content);
    }

    @Override
    public String getValueAsString() {
        return textField.getText();
    }

    @Override
    public void setValueFromString(String value) {
        textField.setText(value);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
