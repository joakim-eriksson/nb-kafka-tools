package se.jocke.nb.kafka.options.form;

import javax.swing.JLabel;

/**
 *
 * @author jocke
 */
public final class LabelValidationFailedDisplay implements ValidationFailedDisplay {

    private final JLabel label;

    public LabelValidationFailedDisplay(JLabel label) {
        this.label = label;
    }

    @Override
    public void show(String text) {
        label.setText(text);
    }

    @Override
    public void clear() {
        label.setText(" ");
    }
}
