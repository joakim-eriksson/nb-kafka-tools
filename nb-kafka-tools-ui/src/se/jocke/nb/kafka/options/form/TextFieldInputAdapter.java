package se.jocke.nb.kafka.options.form;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author jocke
 */
public class TextFieldInputAdapter extends InputAdapter<DocumentEvent> {

    private final JTextField textField;

    public TextFieldInputAdapter(JTextField textField) {
        this.textField = textField;
        textField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void update(DocumentEvent de) {
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
