package se.jocke.nb.kafka.window;

import com.google.common.base.Optional;
import java.awt.EventQueue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;

public class RecordTableModel extends AbstractTableModel {

    private final Set<NBKafkaConsumerRecord> uniqueRecords;

    private final List<NBKafkaConsumerRecord> records;

    public enum VisibleColumn {
        TIMESTAMP {
            @Override
            Object getValue(NBKafkaConsumerRecord record) {
                return record.getTimestamp();
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
        this.uniqueRecords = new HashSet<>();
        this.records = new LinkedList<>();
        this.columns = List.of(VisibleColumn.TIMESTAMP, VisibleColumn.KEY, VisibleColumn.VALUE);
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getValue(records.get(rowIndex));
    }
    
    @Override
    public String getColumnName(int column) {
        return columns.get(column).name();
    }
    
    public NBKafkaConsumerRecord getRecord(int selectedRow) {
        return records.get(selectedRow);
    }


    public void onRecord(NBKafkaConsumerRecord record) {
        EventQueue.invokeLater(() -> {
            if (uniqueRecords.add(record)) {
                records.add(0, record);
                this.fireTableRowsInserted(0, 0);
            }
        });
    }
}
