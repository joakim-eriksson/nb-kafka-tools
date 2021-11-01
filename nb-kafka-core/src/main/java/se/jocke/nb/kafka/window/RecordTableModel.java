package se.jocke.nb.kafka.window;

import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;

public class RecordTableModel extends AbstractTableModel {

    private final Set<String> uniqueKeys;

    private final List<NBKafkaConsumerRecord> records;

    private final List<NBKafkaConsumerRecord> distinctRecords;

    private boolean distinct = false;

    private boolean slidingWindow = false;

    private int maxRecords = 100;

    private static final String YYYY_M_MDD_H_HMMSS = "yyyy-MM-dd HH:mm:ss";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(YYYY_M_MDD_H_HMMSS);

    public enum VisibleColumn {
        TIMESTAMP {
            @Override
            Object getValue(NBKafkaConsumerRecord record) {
                return FORMAT.format(new Date(record.getTimestamp()));
            }
        },
        KEY {
            @Override
            Object getValue(NBKafkaConsumerRecord record) {
                return record.getKey();
            }
        },
        VALUE {
            @Override
            Object getValue(NBKafkaConsumerRecord record) {
                return record.getValue();
            }
        },
        PARTITION {
            @Override
            Object getValue(NBKafkaConsumerRecord record) {
                return record.getPartition();
            }
        },
        OFFSET {
            @Override
            Object getValue(NBKafkaConsumerRecord record) {
                return record.getOffset();
            }
        };

        abstract Object getValue(NBKafkaConsumerRecord record);
    }

    private final List<VisibleColumn> columns;

    public RecordTableModel() {
        this.uniqueKeys = new HashSet<>();
        this.records = new LinkedList<>();
        this.distinctRecords = new LinkedList<>();
        this.columns = List.of(VisibleColumn.TIMESTAMP, VisibleColumn.KEY, VisibleColumn.VALUE);
    }

    public void showDistinct(boolean distinct) {
        boolean changed = this.distinct != distinct;
        if (changed) {
            this.distinct = distinct;
            fireTableDataChanged();
        }
    }

    public boolean isSlidingWindow() {
        return slidingWindow;
    }

    public void setSlidingWindow(boolean slidingWindow) {
        this.slidingWindow = slidingWindow;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    @Override
    public int getRowCount() {
        return getRecords().size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getValue(getRecords().get(rowIndex));
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).name();
    }

    public NBKafkaConsumerRecord getRecord(int selectedRow) {
        return records.get(selectedRow);
    }

    private List<NBKafkaConsumerRecord> getRecords() {
        return distinct ? distinctRecords : records;
    }

    public void onRecord(NBKafkaConsumerRecord record) {
        EventQueue.invokeLater(() -> {
            records.add(0, record);
            if (record.getKey() != null) {
                if (uniqueKeys.add(record.getKey())) {
                    distinctRecords.add(0, record);
                } else {
                    removeRecordWithSameKey(record);
                    distinctRecords.add(0, record);
                }
            }

            fireTableRowsInserted(0, 0);
            if (slidingWindow) {
                moveSlidingWindow();
            }
        });
    }

    private void moveSlidingWindow() {
        if (removeOverMax(records) || removeOverMax(distinctRecords)) {
            fireTableRowsDeleted(getRowCount(), getRowCount());
        }
    }

    private boolean removeOverMax(List<NBKafkaConsumerRecord> consumerRecords) {
        if (consumerRecords.size() > maxRecords) {
            removeLast(consumerRecords);
            return true;
        }
        return false;
    }

    private void removeRecordWithSameKey(NBKafkaConsumerRecord record) {
        for (ListIterator<NBKafkaConsumerRecord> it = distinctRecords.listIterator(); it.hasNext();) {
            int index = it.nextIndex();
            NBKafkaConsumerRecord rec = it.next();
            if (record.getKey().equals(rec.getKey())) {
                it.remove();
                if (distinct) {
                    fireTableRowsDeleted(index, index);
                }
                break;
            }
        }
    }

    private void removeLast(List<NBKafkaConsumerRecord> list) {
        list.remove(list.size() - 1);
    }
}
