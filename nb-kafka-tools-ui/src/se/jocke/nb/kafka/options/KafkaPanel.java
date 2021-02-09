package se.jocke.nb.kafka.options;

import se.jocke.nb.kafka.options.form.DataFormInputVerifier;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import static se.jocke.nb.kafka.options.form.InputConverter.STRING_CONVERTER;
import se.jocke.nb.kafka.options.form.LabelValidationFailedDisplay;
import se.jocke.nb.kafka.options.form.TextFieldInputAdapter;
import se.jocke.nb.kafka.preferences.KafkaPreferences;
import static se.jocke.nb.kafka.preferences.ManagedAdminClientConfig.BOOTSTRAP_SERVERS;

final class KafkaPanel extends javax.swing.JPanel {

    private final KafkaOptionsPanelController controller;

    private final Map<String, DataFormInputVerifier<?, ?>> verifiers = new LinkedHashMap<>();

    KafkaPanel(KafkaOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        verifiers.put(BOOTSTRAP_SERVERS.getKey(), new DataFormInputVerifier<>(
                new TextFieldInputAdapter(bootstrapServersTF),
                 STRING_CONVERTER,
                new LabelValidationFailedDisplay(bootstrapFailedLable))
        );
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
        bootstrapFailedLable = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.jLabel1.text")); // NOI18N

        bootstrapServersTF.setText(org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.bootstrapServersTF.text")); // NOI18N
        bootstrapServersTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bootstrapServersTFActionPerformed(evt);
            }
        });

        bootstrapFailedLable.setForeground(java.awt.Color.red);
        bootstrapFailedLable.setLabelFor(bootstrapServersTF);
        org.openide.awt.Mnemonics.setLocalizedText(bootstrapFailedLable, org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.bootstrapFailedLable.text")); // NOI18N
        bootstrapFailedLable.setToolTipText(org.openide.util.NbBundle.getMessage(KafkaPanel.class, "KafkaPanel.bootstrapFailedLable.toolTipText")); // NOI18N
        bootstrapFailedLable.setPreferredSize(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bootstrapServersTF, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .addComponent(bootstrapFailedLable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bootstrapFailedLable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(bootstrapServersTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private void bootstrapServersTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bootstrapServersTFActionPerformed
        controller.changed();
    }//GEN-LAST:event_bootstrapServersTFActionPerformed

    void load() {
        KafkaPreferences.read().forEach((k, v) -> verifiers.get(k).setValue(v));
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
    private javax.swing.JLabel bootstrapFailedLable;
    private javax.swing.JTextField bootstrapServersTF;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
