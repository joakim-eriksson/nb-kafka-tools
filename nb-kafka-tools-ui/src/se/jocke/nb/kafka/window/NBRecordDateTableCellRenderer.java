package se.jocke.nb.kafka.window;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class NBRecordDateTableCellRenderer extends DefaultTableCellRenderer {

    private static final String YYYY_M_MDD_H_HMMSS = "yyyy-MM-dd HH:mm:ss";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(YYYY_M_MDD_H_HMMSS);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Long time = (Long) value;
        Date date = new Date(time);
        return super.getTableCellRendererComponent(table, FORMAT.format(date), isSelected, hasFocus, row, column);
    }
}
