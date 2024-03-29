package se.jocke.nb.kafka.nodes.root;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;

import javax.swing.Action;
import org.openide.*;
import org.openide.actions.PropertiesAction;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

import static se.jocke.nb.kafka.action.ActionCommandDispatcher.*;
import static se.jocke.nb.kafka.action.Actions.action;
import static se.jocke.nb.kafka.action.Actions.actions;

import se.jocke.nb.kafka.client.AdminClientService;
import se.jocke.nb.kafka.config.ClientConnectionConfig;
import se.jocke.nb.kafka.config.ClientConnectionConfigPropertySupport;
import se.jocke.nb.kafka.nodes.topics.NBKafkaCreateTopic;
import se.jocke.nb.kafka.nodes.topics.NBKafkaTopic;
import se.jocke.nb.kafka.nodes.topics.TopicEditor;
import se.jocke.nb.kafka.preferences.NBKafkaPreferences;
import se.jocke.nb.kafka.window.RecordsTopComponent;

/**
 *
 * @author jocke
 */
public class NBKafkaServiceNode extends AbstractNode {

  private final NBKafkaTopicChildFactory kafkaTopicChildFactory;

  private final NBKafkaServiceKey kafkaServiceKey;

  private NBKafkaServiceNode(NBKafkaTopicChildFactory kafkaTopicChildFactory, NBKafkaServiceKey kafkaServiceKey) {
    super(Children.create(kafkaTopicChildFactory, true));
    this.kafkaTopicChildFactory = kafkaTopicChildFactory;
    this.kafkaServiceKey = kafkaServiceKey;
  }

  public NBKafkaServiceNode(NBKafkaServiceKey kafkaServiceKey) {
    this(new NBKafkaTopicChildFactory(kafkaServiceKey, NBKafkaPreferences.topicFilter(kafkaServiceKey)), kafkaServiceKey);
    setDisplayName(kafkaServiceKey.getName());
    setIconBaseWithExtension("se/jocke/nb/kafka/nodes/root/kafka.png");
  }

  @Override
  protected Sheet createSheet() {
    Sheet sheet = Sheet.createDefault();
    Sheet.Set set = Sheet.createPropertiesSet();
    final Map<ClientConnectionConfig, Object> props = NBKafkaPreferences.readAll(kafkaServiceKey);

    Map<ClientConnectionConfig, Object> edit = new LinkedHashMap<>(props) {
      @Override
      public Object put(ClientConnectionConfig key, Object value) {
        NBKafkaPreferences.put(kafkaServiceKey, key, value);
        NBKafkaPreferences.sync(kafkaServiceKey);
        return super.put(key, value);
      }
    };

    set.setDisplayName("Connection config");

    Arrays.asList(ClientConnectionConfig.values())
        .stream()
        .map(conf -> new ClientConnectionConfigPropertySupport(conf, edit))
        .forEach(set::put);
    sheet.put(set);
    return sheet;
  }

  public void showTopicEditor() {
    TopicEditor topicEditor = new TopicEditor();
    DialogDescriptor descriptor = new DialogDescriptor(topicEditor, "Create", true, onAction(ok(e -> onCreateTopicDialogDescriptorActionOK(topicEditor))));
    DialogDisplayer.getDefault().notifyLater(descriptor);
  }

  private void onCreateTopicDialogDescriptorActionOK(TopicEditor topicEditor) {

    Map<String, String> configs = topicEditor.getTopicProperties().entrySet()
        .stream()
        .collect(toMap(Entry::getKey, e -> e.getValue().toString()));

    NBKafkaCreateTopic createTopic = new NBKafkaCreateTopic.KafkaCreateTopicBuilder()
        .name(topicEditor.getTopicName())
        .numPartitions(Optional.ofNullable(topicEditor.getNumberOfPartitions()))
        .replicationFactor(Optional.ofNullable(topicEditor.getReplicationFactor()))
        .configs(configs)
        .build();

    AdminClientService client = Lookup.getDefault().lookup(AdminClientService.class);

    client.createTopics(kafkaServiceKey, Collections.singletonList(createTopic), this::refreshTopics, Exceptions::printStackTrace);

  }

  public void refreshTopics() {
    kafkaTopicChildFactory.refresh();
  }

  public void viewTopic() {
    final ViewTopicPanel viewTopicPanel = new ViewTopicPanel();
    DialogDescriptor descriptor = new DialogDescriptor(viewTopicPanel, "View Topic", true,
        onAction(ok(event -> {
          if (viewTopicPanel.getTopicName() != null && !viewTopicPanel.getTopicName().isBlank()) {
            NBKafkaTopic kafkaTopic = new NBKafkaTopic(viewTopicPanel.getTopicName(), Optional.empty());
            RecordsTopComponent component = new RecordsTopComponent();
            Set<String> topics = new LinkedHashSet<>(NBKafkaPreferences.getStrings(kafkaServiceKey, ClientConnectionConfig.SAVED_TOPICS));

            if (viewTopicPanel.remeberMe() && topics.add(viewTopicPanel.getTopicName())) {
              NBKafkaPreferences.put(kafkaServiceKey, ClientConnectionConfig.SAVED_TOPICS, topics);
              NBKafkaPreferences.sync(kafkaServiceKey);
              refreshTopics();

            } else if (!viewTopicPanel.remeberMe() && topics.remove(viewTopicPanel.getTopicName())) {
              NBKafkaPreferences.put(kafkaServiceKey, ClientConnectionConfig.SAVED_TOPICS, topics);
              NBKafkaPreferences.sync(kafkaServiceKey);
              refreshTopics();
            }

            component.showTopic(kafkaServiceKey, kafkaTopic);
          }
        })));
    DialogDisplayer.getDefault().notifyLater(descriptor);
  }

  private void addFilter() {
    NBKafkaServiceFilterPanel filterPanel = new NBKafkaServiceFilterPanel();
    DialogDescriptor descriptor = new DialogDescriptor(filterPanel, "Add filter", false,
        onAction(ok(e -> onCreateFilterActionOK(filterPanel))));
    DialogDisplayer.getDefault().notifyLater(descriptor);
  }


  private void deleteNode() {
    final Object result = DialogDisplayer.getDefault()
        .notify(new NotifyDescriptor.Confirmation("Delete node " + kafkaServiceKey.getName() + " ?"));
    if (NotifyDescriptor.YES_OPTION.equals(result)) {
      Logger.getLogger(NBKafkaServiceNode.class.getName()).log(Level.INFO, "Delete service {0}", kafkaServiceKey.getName());
      NBKafkaPreferences.delete(kafkaServiceKey);
      refreshParent();
    }
  }

  private void onCreateFilterActionOK(NBKafkaServiceFilterPanel filterPanel) {
    final String name = filterPanel.getFilterName();
    final String expression = filterPanel.getFilterExpression();

    if (name != null
        && !name.isBlank()
        && expression != null
        && !expression.isBlank()
        && isValidRegex(expression, filterPanel.isFilterReqex())) {
      final Map<ClientConnectionConfig, Object> props = NBKafkaPreferences.readAll(kafkaServiceKey);
      props.put(ClientConnectionConfig.LIST_TOPICS_FILTER_EXPESSION, expression);
      props.put(ClientConnectionConfig.LIST_TOPICS_FILTER_IS_REGEX, filterPanel.isFilterReqex());
      NBKafkaPreferences.store(new NBKafkaServiceKey(filterPanel.getFilterName()), props);
      refreshParent();
    }
  }

  private void refreshParent() {
    NBKafkaServiceRootNode node = (NBKafkaServiceRootNode) getParentNode();
    node.refresh();
  }

  private boolean isValidRegex(String expression, boolean isRegex) {
    if (isRegex) {
      try {
        Pattern.compile(expression);
      } catch (Exception e) {
        Exceptions.printStackTrace(e);
        return false;
      }
    }
    return true;
  }

  private void exportSettings() {
    try {

      FileObject fob = FileUtil.createMemoryFileSystem().getRoot().createData(kafkaServiceKey.getName(), "properties");

      try (OutputStream outputStream = fob.getOutputStream()) {
        NBKafkaPreferences.exportProperties(kafkaServiceKey, outputStream);
        outputStream.flush();
      }

      DataObject dob = DataObject.find(fob);
      OpenCookie openCookie = dob.getLookup().lookup(OpenCookie.class);

      if (openCookie != null) {
        openCookie.open();
      }

    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    }
  }

  @Override
  public Action[] getActions(boolean context) {
    return actions(
        action("Refresh", this::refreshTopics),
        action("Create Topic", this::showTopicEditor),
        action("View topic", this::viewTopic),
        action("Export settings", this::exportSettings),
        action("Add filter", this::addFilter),
        action("Delete", this::deleteNode),
        SystemAction.get(PropertiesAction.class)
    );
  }
}
