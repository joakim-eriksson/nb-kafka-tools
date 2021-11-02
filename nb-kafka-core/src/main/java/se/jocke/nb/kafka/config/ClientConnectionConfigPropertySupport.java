package se.jocke.nb.kafka.config;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Map;
import org.openide.nodes.PropertySupport;

@SuppressWarnings("rawtypes")
public class ClientConnectionConfigPropertySupport extends PropertySupport.ReadWrite {

    private final Map<ClientConnectionConfig, Object> props;

    private static final Map<Class<?>, Object> defaultValues = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Boolean.class, Boolean.FALSE),
            new AbstractMap.SimpleEntry<>(String.class, ""),
            new AbstractMap.SimpleEntry<>(Long.class, -1l)
    );

    private final ClientConnectionConfig config;

    public ClientConnectionConfigPropertySupport(ClientConnectionConfig config, Map<ClientConnectionConfig, Object> props) {
        super(config.getKey(), config.getPropertyType(), config.getKey(), config.getDesc());
        this.config = config;
        this.props = props;
       
    }

    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        Object value = props.get(config);
        return value == null ? defaultValues.get(config.getPropertyType()) : value;
    }

    @Override
    public void setValue(Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        props.put(config, value);
    }

    @Override
    public boolean isDefaultValue() {
        return props.get(config) == null;
    }

    @Override
    public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
        props.put(config, null);
    }

    @Override
    public boolean supportsDefaultValue() {
        return true;
    }
}
