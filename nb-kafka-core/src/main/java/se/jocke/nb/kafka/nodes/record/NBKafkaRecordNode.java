package se.jocke.nb.kafka.nodes.record;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;

import static org.openide.nodes.PropertySupport.readOnly;

public class NBKafkaRecordNode extends AbstractNode {

  private final NBKafkaConsumerRecord record;

  public NBKafkaRecordNode(NBKafkaConsumerRecord record) {
    super(Children.LEAF);
    this.record = record;
  }

  @Override
  protected Sheet createSheet() {
    Sheet sheet = Sheet.createDefault();

    Sheet.Set meta = Sheet.createPropertiesSet();
    meta.setDisplayName("Meta");
    meta.setName("meta");
    meta.put(readOnly("Key", String.class, record::getKey));
    meta.put(readOnly("Partition", Integer.class, record::getPartition));
    meta.put(readOnly("Offset", Long.class, record::getOffset));
    meta.put(readOnly("Timestamp", Long.class, record::getTimestamp));

    Sheet.Set headers = Sheet.createPropertiesSet();
    headers.setDisplayName("Headers");
    headers.setName("headers");
    record.getHeaders().forEach((key, value) -> headers.put(readOnly(key, String.class, () -> value)));

    sheet.put(meta);
    sheet.put(headers);
    return sheet;
  }
}
