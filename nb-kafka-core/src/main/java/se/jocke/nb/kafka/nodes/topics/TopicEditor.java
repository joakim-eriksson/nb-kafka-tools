package se.jocke.nb.kafka.nodes.topics;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author jocke
 */
public class TopicEditor extends javax.swing.JPanel {

    private final Map<String, Object> props;

    private static final Map<Class<?>, Object> defaultValues = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Boolean.class, Boolean.FALSE),
            new AbstractMap.SimpleEntry<>(String.class, ""),
            new AbstractMap.SimpleEntry<>(Long.class, -1l),
            new AbstractMap.SimpleEntry<>(Integer.class, -1),
            new AbstractMap.SimpleEntry<>(Double.class, -1d)
    );

    /**
     * Creates new form TopicEditor
     */
    public TopicEditor() {
        this.props = new LinkedHashMap<>();
        initComponents();
        PropertySheet kps = (PropertySheet) kafkaPropsPanel;
        AbstractNode abstractNode = new AbstractNode(Children.LEAF) {
            @Override
            public Node.PropertySet[] getPropertySets() {
                Sheet.Set set = Sheet.createPropertiesSet();
                Arrays.asList(NBKafkaCreateTopic.CreateProperties.values())
                        .stream()
                        .map(cp -> createProp(props, cp))
                        .forEach(set::put);
                return new PropertySet[]{set};
            }
        };
        kps.setNodes(new Node[]{abstractNode});
    }

    private PropertySupport<?> createProp(Map<String, Object> props, NBKafkaCreateTopic.CreateProperties cp) {
        return new PropertySupport.ReadWrite(cp.getKey(), cp.getType(), cp.getKey(), cp.getDescription()) {
            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                final Object value = cp.getValue(props);
                return value == null ? defaultValues.get(cp.getType()) : value;
            }

            @Override
            public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (defaultValues.get(cp.getType()).equals(val)) {
                    restoreDefaultValue();
                } else {
                    cp.setValue(props, val);
                }
            }

            @Override
            public boolean isDefaultValue() {
                return cp.getValue(props) == null;
            }

            @Override
            public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
                cp.clearValue(props);
            }

            @Override
            public boolean supportsDefaultValue() {
                return true;
            }
        };
    }

    public Map<String, Object> getTopicProperties() {
        return new LinkedHashMap<>(props);
    }

    public Integer getNumberOfPartitions() {
        return (Integer) jSpinner1.getValue();
    }

    public Short getReplicationFactor() {
        return ((Number) jSpinner2.getValue()).shortValue();
    }

    public String getTopicName() {
        return jTextField1.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        kafkaPropsPanel = new PropertySheet();

        jTextField1.setText(org.openide.util.NbBundle.getMessage(TopicEditor.class, "TopicEditor.jTextField1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TopicEditor.class, "TopicEditor.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TopicEditor.class, "TopicEditor.jLabel2.text")); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4000, 1));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(TopicEditor.class, "TopicEditor.jLabel3.text")); // NOI18N

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kafkaPropsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1)
                            .addComponent(jSpinner2, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                            .addComponent(jSpinner1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(kafkaPropsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel kafkaPropsPanel;
    // End of variables declaration//GEN-END:variables
}
