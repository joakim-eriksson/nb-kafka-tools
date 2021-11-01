package se.jocke.nb.kafka.options.form;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author jocke
 */
public class DocumentListenerAdapter implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent de) {
        update(de);
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        update(de);
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        update(de);
    }

    public void update(DocumentEvent de) {

    }
}
