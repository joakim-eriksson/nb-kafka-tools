package se.jocke.nb.kafka.window;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import se.jocke.nb.kafka.Disposable;
import se.jocke.nb.kafka.client.NBKafkaConsumer;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;
import se.jocke.nb.kafka.nodes.topics.KafkaTopic;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//se.jocke.nb.kafka.window//Records//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = RecordsTopComponent.RECORDS_TOP_COMPONENT_ID,
        iconBase = "/se/jocke/nb/kafka/nodes/root/kafka.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "se.jocke.nb.kafka.window.RecordsTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RecordsAction",
        preferredID = "RecordsTopComponent"
)
@Messages({
    "CTL_RecordsAction=Records",
    "CTL_RecordsTopComponent=Records Window",
    "HINT_RecordsTopComponent=This is a Records window"
})
public final class RecordsTopComponent extends TopComponent {

    private static final int MIN_COLUMN_WIDTH = 200;
    private static final int MAX_COLUMN_WIDTH = 400;
    
    public static final String A_NEW = "new";
    
    public static final String KEYLESS = "keyless";

    private final InstanceContent content;

    public static final String RECORDS_TOP_COMPONENT_ID = "RecordsTopComponent";
    
    public RecordsTopComponent() {
        content = new InstanceContent();
        content.add(this.getActionMap());
        Lookup lkp = new ProxyLookup(new AbstractLookup(content));
        associateLookup(lkp);
        initComponents();
        setName(Bundle.CTL_RecordsTopComponent());
        setToolTipText(Bundle.HINT_RecordsTopComponent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rowPopupMenu = new javax.swing.JPopupMenu();
        openInEditorMenuItem = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        offsetResetComboBox = new javax.swing.JComboBox<>();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jButton1 = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        statusLabel = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        recordCountLable = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        rateSpinner = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        recordTable = new javax.swing.JTable();

        org.openide.awt.Mnemonics.setLocalizedText(openInEditorMenuItem, org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.openInEditorMenuItem.text")); // NOI18N
        openInEditorMenuItem.setToolTipText(org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.openInEditorMenuItem.toolTipText")); // NOI18N
        openInEditorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openInEditorMenuItemActionPerformed(evt);
            }
        });
        rowPopupMenu.add(openInEditorMenuItem);

        jToolBar1.setRollover(true);
        jToolBar1.setPreferredSize(new java.awt.Dimension(40, 40));

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/se/jocke/nb/kafka/window/add-record.png"))); // NOI18N
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setIconTextGap(0);
        addButton.setMaximumSize(new java.awt.Dimension(36, 36));
        addButton.setMinimumSize(new java.awt.Dimension(36, 36));
        addButton.setPreferredSize(new java.awt.Dimension(36, 36));
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/se/jocke/nb/kafka/window/stop.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(stopButton, org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.stopButton.text")); // NOI18N
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setPreferredSize(new java.awt.Dimension(36, 36));
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stopButton);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/se/jocke/nb/kafka/window/run.png"))); // NOI18N
        runButton.setEnabled(false);
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(runButton);
        jToolBar1.add(filler3);

        offsetResetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Latest", "Earliest" }));
        jToolBar1.add(offsetResetComboBox);
        jToolBar1.add(filler4);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/se/jocke/nb/kafka/window/filter.png"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/se/jocke/nb/kafka/window/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.refreshButton.text")); // NOI18N
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setPreferredSize(new java.awt.Dimension(36, 36));
        refreshButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(refreshButton);
        jToolBar1.add(filler1);

        org.openide.awt.Mnemonics.setLocalizedText(statusLabel, org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.statusLabel.text")); // NOI18N
        jToolBar1.add(statusLabel);
        jToolBar1.add(filler5);

        org.openide.awt.Mnemonics.setLocalizedText(recordCountLable, org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.recordCountLable.text")); // NOI18N
        jToolBar1.add(recordCountLable);
        jToolBar1.add(filler2);

        rateSpinner.setModel(new javax.swing.SpinnerNumberModel(5.0d, 0.5d, 100.0d, 0.5d));
        rateSpinner.setToolTipText(org.openide.util.NbBundle.getMessage(RecordsTopComponent.class, "RecordsTopComponent.rateSpinner.toolTipText")); // NOI18N
        rateSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rateSpinnerStateChanged(evt);
            }
        });
        jToolBar1.add(rateSpinner);

        recordTable.setModel(new RecordTableModel());
        recordTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        recordTable.setComponentPopupMenu(rowPopupMenu);
        jScrollPane1.setViewportView(recordTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 954, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        KafkaTopic topic = getLookup().lookup(KafkaTopic.class);
        if (topic != null) {
            try {
                final AddMessagePanel addMessagePanel = new AddMessagePanel(topic);
                FileObject fob = FileUtil.createMemoryFileSystem().getRoot().createData(A_NEW, "json");
                fob.addFileChangeListener(new FileChangeAdapter(){
                    @Override
                    public void fileChanged(FileEvent fe) {
                        addMessagePanel.showDialog(fe.getFile());
                    }                   
                });
                DataObject dob = DataObject.find(fob);
                EditorCookie ed = dob.getLookup().lookup(EditorCookie.class);
                ed.open();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed


    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        NBKafkaConsumer consumer = getLookup().lookup(NBKafkaConsumer.class);
        if (consumer != null) {
            consumer.stop();
            statusLabel.setText("Stopped");
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }//GEN-LAST:event_stopButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        NBKafkaConsumer consumer = getLookup().lookup(NBKafkaConsumer.class);
        if (consumer != null) {
            consumer.restart();
            statusLabel.setText("Running");
            stopButton.setEnabled(true);
            runButton.setEnabled(false);
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void rateSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rateSpinnerStateChanged
        NBKafkaConsumer consumer = getLookup().lookup(NBKafkaConsumer.class);
        if (consumer != null) {
            Double rate = (Double) rateSpinner.getValue();
            consumer.setRate(rate);
        }
    }//GEN-LAST:event_rateSpinnerStateChanged

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        initResources(getLookup().lookup(KafkaTopic.class));
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void openInEditorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openInEditorMenuItemActionPerformed
        int selectedRow = recordTable.getSelectedRow();
        KafkaTopic topic = getLookup().lookup(KafkaTopic.class);
        if (selectedRow >= 0 && topic != null) {
            try {
                RecordTableModel model = (RecordTableModel) recordTable.getModel();
                NBKafkaConsumerRecord record = model.getRecord(selectedRow);
                String name = record.getKey() != null ? record.getKey() : KEYLESS;
                FileObject fob = FileUtil.createMemoryFileSystem().getRoot().createData(name, "json");

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject jsonObject = new JsonParser().parse(record.getValue()).getAsJsonObject();

                try (OutputStream outputStream = fob.getOutputStream()) {
                    outputStream.write(gson.toJson(jsonObject).getBytes());
                    outputStream.flush();
                }

                final AddMessagePanel addMessagePanel = new AddMessagePanel(topic);
                fob.addFileChangeListener(new FileChangeAdapter() {
                    @Override
                    public void fileChanged(FileEvent fe) {
                        addMessagePanel.showDialog(fe.getFile());
                    }
                });

                DataObject dob = DataObject.find(fob);
                EditorCookie ed = dob.getLookup().lookup(EditorCookie.class);
                ed.open();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_openInEditorMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox<String> offsetResetComboBox;
    private javax.swing.JMenuItem openInEditorMenuItem;
    private javax.swing.JSpinner rateSpinner;
    private javax.swing.JLabel recordCountLable;
    private javax.swing.JTable recordTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPopupMenu rowPopupMenu;
    private javax.swing.JButton runButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        requestActive();
        sizeColumns();
    }

    private void sizeColumns() {
        TableModel model = recordTable.getModel();
        int columnsToResize = model.getColumnCount() - 1;
        for (int i = 0; i < columnsToResize; i++) {
            recordTable.getColumnModel().getColumn(i).setMaxWidth(MAX_COLUMN_WIDTH);
            recordTable.getColumnModel().getColumn(i).setMinWidth(MIN_COLUMN_WIDTH);
        }
    }

    @Override
    public void componentClosed() {
        closeResources();
    }

    public void showTopic(KafkaTopic topic) {
        setDisplayName(topic.getName());
        initResources(topic);
        open();
    }

    private void initResources(KafkaTopic topic) {
        closeResources();

        content.add(topic);

        RecordTableModel tableModel = new RecordTableModel();

        recordTable.setModel(tableModel);

        NBKafkaConsumer.Builder builder = new NBKafkaConsumer.Builder()
                .observer(tableModel::onRecord)
                .topic(topic)
                .rate((Double) rateSpinner.getValue());

        if ("earliest".equalsIgnoreCase((String) offsetResetComboBox.getSelectedItem())) {
            builder.earliest();
        }

        content.add(builder.build().start());

        Timer t = new Timer(500, this::updateRecordCountLabel);

        content.add((Disposable) t::stop);

        t.start();
    }

    private void updateRecordCountLabel(ActionEvent e) {
        var consumer = getLookup().lookup(NBKafkaConsumer.class);
        if (consumer != null) {
            recordCountLable.setText(recordTable.getModel().getRowCount() + "/" + consumer.getCount());
        }
    }

    private void closeResources() {
        Collection<? extends Disposable> disposables = getLookup().lookupAll(Disposable.class);
        disposables.forEach(content::remove);
        disposables.forEach(Disposable::dispose);
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
