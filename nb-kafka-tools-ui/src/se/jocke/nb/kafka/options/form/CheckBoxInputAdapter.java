package se.jocke.nb.kafka.options.form;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;

public final class CheckBoxInputAdapter extends InputAdapter<ItemEvent> {

    private final JCheckBox checkBox;

    public CheckBoxInputAdapter(JCheckBox checkBox) {
        this.checkBox = checkBox;
        checkBox.addItemListener((e) -> {
            onChange(new UpdateEvent<>(e));
        });
    }

    @Override
    public String getValueAsString() {
        return Boolean.toString(checkBox.isSelected());
    }

    @Override
    public void setValueFromString(String value) {
        checkBox.setSelected(Boolean.valueOf(value));
    }
}
