package se.jocke.nb.kafka.options;

import se.jocke.nb.kafka.options.form.DataFormInputVerifier;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import se.jocke.nb.kafka.options.form.CheckBoxInputAdapter;
import se.jocke.nb.kafka.options.form.LabelValidationFailedDisplay;
import se.jocke.nb.kafka.options.form.TextFieldInputAdapter;
import se.jocke.nb.kafka.preferences.KafkaPreferences;
import static se.jocke.nb.kafka.preferences.ManagedAdminClientConfig.BOOTSTRAP_SERVERS;
import static se.jocke.nb.kafka.options.form.InputConverter.REQUIRED_STRING_CONVERTER;
import static se.jocke.nb.kafka.options.form.InputConverter.STRING_CONVERTER;
import static se.jocke.nb.kafka.preferences.ManagedAdminClientConfig.GCP_ENABLED;
import static se.jocke.nb.kafka.preferences.ManagedAdminClientConfig.GCP_ENCODED_KEY;
import static se.jocke.nb.kafka.preferences.ManagedAdminClientConfig.GCP_SECRET_VERSION_REQUEST_NAME;

final class KafkaPanel extends javax.swing.JPanel {

    private final KafkaOptionsPanelController controller;

    private final Map<String, DataFormInputVerifier<?, ?>> verifiers = new LinkedHashMap<>();

    KafkaPanel(KafkaOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        verifiers.put(BOOTSTRAP_SERVERS.getKey(), new DataFormInputVerifier<>(new TextFieldInputAdapter(bootstrapServersTF), REQUIRED_STRING_CONVERTER, new LabelValidationFailedDisplay(bootstrapFailedLabel)));
        verifiers.put(GCP_SECRET_VERSION_REQUEST_NAME.getKey(), new DataFormInputVerifier<>(new TextFieldInputAdapter(secretVersionRequestNameTextField), STRING_CONVERTER, new LabelValidationFailedDisplay(errorEncodedKeyLabel)));
        verifiers.put(GCP_ENCODED_KEY.getKey(), new DataFormInputVerifier<>(new TextFieldInputAdapter(encodedKeyTextArea), STRING_CONVERTER, new LabelValidationFailedDisplay(errorEncodedKeyLabel)));
        verifiers.put(GCP_ENABLED.getKey(), new DataFormInputVerifier<>(new CheckBoxInputAdapter(gcpCheckBox), STRING_CONVERTER, LabelValidationFailedDisplay.NO_FAIL_DISPLAY));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        bootstrapServersTF = new javax.swing.JTextField();
        bootstrapFailedLabel = new javax.swing.JLabel();
        gcpCheckBox = new javax.swing.JCheckBox();
        gcpPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        secretVersionRequestNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        encodedKeyTextArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        errorSecretKeyLabel = new javax.swing.JLabel();
        errorEncodedKeyLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.jLabel1.text")); // NOI18N

        bootstrapServersTF.setText(org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.bootstrapServersTF.text")); // NOI18N
        bootstrapServersTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bootstrapServersTFActionPerformed(evt);
            }
        });

        bootstrapFailedLabel.setForeground(java.awt.Color.red);
        bootstrapFailedLabel.setLabelFor(bootstrapServersTF);
        org.openide.awt.Mnemonics.setLocalizedText(bootstrapFailedLabel, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.bootstrapFailedLabel.text")); // NOI18N
        bootstrapFailedLabel.setToolTipText(org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.bootstrapFailedLabel.toolTipText")); // NOI18N
        bootstrapFailedLabel.setPreferredSize(null);

        org.openide.awt.Mnemonics.setLocalizedText(gcpCheckBox, "GCP");
        gcpCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                gcpCheckBoxItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.jLabel2.text")); // NOI18N

        secretVersionRequestNameTextField.setText(org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.secretVersionRequestNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.jLabel3.text")); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        encodedKeyTextArea.setColumns(20);
        encodedKeyTextArea.setLineWrap(true);
        encodedKeyTextArea.setRows(5);
        jScrollPane1.setViewportView(encodedKeyTextArea);

        jLabel4.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorSecretKeyLabel, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.errorSecretKeyLabel.text")); // NOI18N

        errorEncodedKeyLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(errorEncodedKeyLabel, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.errorEncodedKeyLabel.text")); // NOI18N

        javax.swing.GroupLayout gcpPanelLayout = new javax.swing.GroupLayout(gcpPanel);
        gcpPanel.setLayout(gcpPanelLayout);
        gcpPanelLayout.setHorizontalGroup(
            gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gcpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(gcpPanelLayout.createSequentialGroup()
                        .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(secretVersionRequestNameTextField)
                            .addGroup(gcpPanelLayout.createSequentialGroup()
                                .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(errorEncodedKeyLabel)
                                    .addGroup(gcpPanelLayout.createSequentialGroup()
                                        .addComponent(errorSecretKeyLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        gcpPanelLayout.setVerticalGroup(
            gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gcpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(errorSecretKeyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secretVersionRequestNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(7, 7, 7)
                .addGroup(gcpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(errorEncodedKeyLabel))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bootstrapServersTF, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                            .addComponent(bootstrapFailedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(gcpCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(gcpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bootstrapFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(bootstrapServersTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gcpCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gcpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    private void bootstrapServersTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bootstrapServersTFActionPerformed
        controller.changed();
    }//GEN-LAST:event_bootstrapServersTFActionPerformed

    private void gcpCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gcpCheckBoxItemStateChanged
        gcpPanel.setVisible(gcpCheckBox.isSelected());
    }//GEN-LAST:event_gcpCheckBoxItemStateChanged

    void load() {
        KafkaPreferences.read().forEach((k, v) -> verifiers.get(k).setValue(v));
        gcpPanel.setVisible(KafkaPreferences.getBoolean(GCP_ENABLED.getKey(), false));
    }

    void store() {
        if (valid()) {
            Map<String, String> config = verifiers.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().getValueAsString()));
            KafkaPreferences.store(config);
        } else {
            throw new IllegalStateException("Can not save invalid state");
        }
    }

    boolean valid() {
        return verifiers.values().stream().allMatch(DataFormInputVerifier::isValid);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bootstrapFailedLabel;
    private javax.swing.JTextField bootstrapServersTF;
    private javax.swing.JTextArea encodedKeyTextArea;
    private javax.swing.JLabel errorEncodedKeyLabel;
    private javax.swing.JLabel errorSecretKeyLabel;
    private javax.swing.JCheckBox gcpCheckBox;
    private javax.swing.JPanel gcpPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField secretVersionRequestNameTextField;
    // End of variables declaration//GEN-END:variables
}
