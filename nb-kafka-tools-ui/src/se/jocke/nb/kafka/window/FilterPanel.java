package se.jocke.nb.kafka.window;

import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;
import javax.swing.JComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.lookup.InstanceContent;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;
import se.jocke.nb.kafka.options.form.DataFormInputVerifier;
import se.jocke.nb.kafka.options.form.LabelValidationFailedDisplay;
import se.jocke.nb.kafka.options.form.TextFieldInputAdapter;

/**
 *
 * @author jocke
 */
public class FilterPanel extends javax.swing.JPanel {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final InstanceContent content;

    private final DialogDescriptor descriptor;

    private final Runnable verifier;
    
    private final Set<DataFormInputVerifier<JComponent, Predicate<NBKafkaConsumerRecord>>> verifiers;

    /**
     * Creates new form FilterPanel
     */
    public FilterPanel(InstanceContent content) {
        initComponents();
        this.content = content;
        this.descriptor = new DialogDescriptor(this, "Create filter", true, (actionEvent) -> onDialogDescriptorAction(actionEvent));
        this.verifiers = new HashSet<>();

        this.verifier = () -> {
            descriptor.setValid(verifiers.stream().allMatch(DataFormInputVerifier::isValid));
        };

        FilterPredicate valueFilter = new FilterPredicate(valueIsRegex::isSelected, NBKafkaConsumerRecord::getValue);
        FilterPredicate keyFilter = new FilterPredicate(keyIsRegexCheckBox::isSelected, NBKafkaConsumerRecord::getKey);
        DateRangePredicate dateRangeFilter = new DateRangePredicate();
        
        verifiers.add(new DataFormInputVerifier<>(new TextFieldInputAdapter(valueTextField), valueFilter, new LabelValidationFailedDisplay(valueErrorLabel)).onChange(verifier));
        verifiers.add(new DataFormInputVerifier<>(new TextFieldInputAdapter(keyTextField), keyFilter, new LabelValidationFailedDisplay(keyErrorLabel)).onChange(verifier));
        verifiers.add(new DataFormInputVerifier<>(new TextFieldInputAdapter(betweenFormattedTextField), dateRangeFilter, new LabelValidationFailedDisplay(betweenErrorLabel)).onChange(verifier));
    }

    public void showDialog() {
        descriptor.setValid(false);
        DialogDisplayer.getDefault().notifyLater(descriptor);
    }

    public void onDialogDescriptorAction(ActionEvent event) {
        LOG.log(Level.INFO, "Action triggered with command {0}", event.getActionCommand());

        if ("OK".equalsIgnoreCase(event.getActionCommand())) {
            content.add(Filters.and(verifiers.stream().map(DataFormInputVerifier::getValue).collect(toSet())));
        } else if ("Cancel".equalsIgnoreCase(event.getActionCommand())) {
            LOG.log(Level.INFO, "Action Cancel triggered");
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

        keyLabel = new javax.swing.JLabel();
        keyTextField = new javax.swing.JTextField();
        keyErrorLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        valueErrorLabel = new javax.swing.JLabel();
        valueTextField = new javax.swing.JTextField();
        keyIsRegexCheckBox = new javax.swing.JCheckBox();
        valueIsRegex = new javax.swing.JCheckBox();
        betweenFormattedTextField = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        betweenErrorLabel = new javax.swing.JLabel();
        nowButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(keyLabel, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.keyLabel.text")); // NOI18N

        keyTextField.setText(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.keyTextField.text")); // NOI18N

        keyErrorLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(keyErrorLabel, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.keyErrorLabel.text")); // NOI18N
        keyErrorLabel.setToolTipText(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.keyErrorLabel.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.jLabel2.text")); // NOI18N

        valueErrorLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(valueErrorLabel, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.valueErrorLabel.text")); // NOI18N

        valueTextField.setText(org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.valueTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(keyIsRegexCheckBox, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.keyIsRegexCheckBox.text")); // NOI18N
        keyIsRegexCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                keyIsRegexCheckBoxItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(valueIsRegex, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.valueIsRegex.text")); // NOI18N
        valueIsRegex.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                valueIsRegexItemStateChanged(evt);
            }
        });

        betweenFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss / yyyy-MM-dd HH:mm:ss"))));
        betweenFormattedTextField.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        betweenFormattedTextField.setValue(null);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.jLabel4.text")); // NOI18N

        betweenErrorLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(betweenErrorLabel, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.betweenErrorLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(nowButton, org.openide.util.NbBundle.getMessage(FilterPanel.class, "FilterPanel.nowButton.text")); // NOI18N
        nowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nowButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyLabel)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(valueTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                            .addComponent(keyTextField)
                            .addComponent(betweenFormattedTextField, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(keyIsRegexCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valueIsRegex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nowButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(betweenErrorLabel)
                            .addComponent(keyErrorLabel)
                            .addComponent(valueErrorLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(keyErrorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keyLabel)
                    .addComponent(keyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyIsRegexCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valueErrorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valueIsRegex))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(betweenErrorLabel)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(betweenFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(nowButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nowButtonActionPerformed
        betweenFormattedTextField.setValue(new Date());
    }//GEN-LAST:event_nowButtonActionPerformed

    private void keyIsRegexCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_keyIsRegexCheckBoxItemStateChanged
        verifier.run();
    }//GEN-LAST:event_keyIsRegexCheckBoxItemStateChanged

    private void valueIsRegexItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_valueIsRegexItemStateChanged
        verifier.run();
    }//GEN-LAST:event_valueIsRegexItemStateChanged

    public static class Filters {

        private final Predicate<NBKafkaConsumerRecord> predicate;

        public Filters(Predicate<NBKafkaConsumerRecord> predicate) {
            this.predicate = predicate;
        }

        public Predicate<NBKafkaConsumerRecord> getPredicate() {
            return predicate;
        }
        

        public static Filters and(Set<Predicate<NBKafkaConsumerRecord>> predicates) {
            Predicate<NBKafkaConsumerRecord> and = predicates.stream().reduce(x -> true, Predicate::and);
            return new Filters(and);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            return getClass() == obj.getClass();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel betweenErrorLabel;
    private javax.swing.JFormattedTextField betweenFormattedTextField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel keyErrorLabel;
    private javax.swing.JCheckBox keyIsRegexCheckBox;
    private javax.swing.JLabel keyLabel;
    private javax.swing.JTextField keyTextField;
    private javax.swing.JButton nowButton;
    private javax.swing.JLabel valueErrorLabel;
    private javax.swing.JCheckBox valueIsRegex;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables
}