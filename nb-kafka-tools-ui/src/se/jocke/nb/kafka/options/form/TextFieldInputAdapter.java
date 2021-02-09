package se.jocke.nb.kafka.options.form;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

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
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();
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

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
