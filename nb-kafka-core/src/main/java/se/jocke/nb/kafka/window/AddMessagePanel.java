package se.jocke.nb.kafka.window;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import static se.jocke.nb.kafka.action.ActionCommandDispatcher.*;
import se.jocke.nb.kafka.client.NBKafkaProducer;
import se.jocke.nb.kafka.nodes.root.NBKafkaServiceKey;
import se.jocke.nb.kafka.nodes.topics.NBKafkaTopic;
import static se.jocke.nb.kafka.window.RecordsTopComponent.A_NEW;
import static se.jocke.nb.kafka.window.RecordsTopComponent.KEYLESS;

/**
 *
 * @author jocke
 */
public class AddMessagePanel extends javax.swing.JPanel {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final NBKafkaTopic topic;

    private static final Set<String> NULL_KEYS = Sets.newHashSet(A_NEW, KEYLESS);

    private final NBKafkaServiceKey kafkaServiceKey;

    public AddMessagePanel(NBKafkaServiceKey kafkaServiceKey, NBKafkaTopic topic) {
        super();
        this.topic = topic;
        initComponents();
        topicLabel.setText(topic.getName());
        this.kafkaServiceKey = kafkaServiceKey;
    }

    public void showDialog(FileObject fileObject) {
        DialogDescriptor descriptor = new DialogDescriptor(this, "Publish record", true, onAction(
                ok(actionEvent -> onDialogDescriptorAction(actionEvent, fileObject))
        ));
        DialogDisplayer.getDefault().notifyLater(descriptor);
        keyTextField.setText(NULL_KEYS.contains(fileObject.getName()) ? "" : fileObject.getName());
    }

    public void onDialogDescriptorAction(ActionEvent event, FileObject fileObject) {
        LOG.log(Level.INFO, "Action triggered with command {0}", event.getActionCommand());

        NBKafkaProducer producer = Lookup.getDefault().lookup(NBKafkaProducer.class);

        try {
            String key = keyTextField.getText().isBlank() ? null : keyTextField.getText();
            String value = fileObject.asText();

            if (value != null && !value.isBlank()) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                JsonObject jsonObject = new JsonParser().parse(value).getAsJsonObject();
                ProducerRecord<String, String> pr = new ProducerRecord<>(topic.getName(), key, trimCheckBox.isSelected() ? gson.toJson(jsonObject) : value);
                producer.send(kafkaServiceKey, pr, (RecordMetadata rm, Exception ex) -> {
                    if (ex == null) {
                        try {
                            DataObject dob = DataObject.find(fileObject);
                            EditorCookie ed = dob.getLookup().lookup(EditorCookie.class);
                            ed.close();
                        } catch (DataObjectNotFoundException objectNotFoundException) {
                            Exceptions.printStackTrace(objectNotFoundException);
                        }
                    } else {
                        Exceptions.printStackTrace(ex);
                    }
                });
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        topicLabel = new javax.swing.JLabel();
        keyTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        trimCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AddMessagePanel.class, "AddMessagePanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(topicLabel, org.openide.util.NbBundle.getMessage(AddMessagePanel.class, "AddMessagePanel.topicLabel.text")); // NOI18N

        keyTextField.setText(org.openide.util.NbBundle.getMessage(AddMessagePanel.class, "AddMessagePanel.keyTextField.text")); // NOI18N
        keyTextField.setToolTipText(org.openide.util.NbBundle.getMessage(AddMessagePanel.class, "AddMessagePanel.keyTextField.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(AddMessagePanel.class, "AddMessagePanel.jLabel2.text")); // NOI18N

        trimCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(trimCheckBox, org.openide.util.NbBundle.getMessage(AddMessagePanel.class, "AddMessagePanel.trimCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyTextField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trimCheckBox)
                            .addComponent(topicLabel))
                        .addGap(0, 226, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(topicLabel))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trimCheckBox)
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField keyTextField;
    private javax.swing.JLabel topicLabel;
    private javax.swing.JCheckBox trimCheckBox;
    // End of variables declaration//GEN-END:variables
}
