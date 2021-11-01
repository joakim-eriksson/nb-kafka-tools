package se.jocke.nb.kafka.nodes.root;

import java.util.Objects;

public final class NBKafkaServiceKey {

    private final String name;

    public NBKafkaServiceKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NBKafkaServiceKey other = (NBKafkaServiceKey) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KafkaService{" + "name=" + name + '}';
    }
}
