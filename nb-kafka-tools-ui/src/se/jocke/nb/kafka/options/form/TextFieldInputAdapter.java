package se.jocke.nb.kafka.options.form;

import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author jocke
 */
public final class TextFieldInputAdapter extends InputAdapter<DocumentEvent> {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    
    private final JTextField textField;

    public TextFieldInputAdapter(JTextField textField) {
        this.textField = textField;
        textField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void update(DocumentEvent de) {
                LOG.log(Level.INFO, "Document change {0}", de.toString());
                onChange(new UpdateEvent<>(de));
            }
        });
    }

    @Override
    public String getValueAsString() {
        return textField.getText();
    }

    @Override
    public void setValueFromString(String value) {
        textField.setText(value);
    }
}
