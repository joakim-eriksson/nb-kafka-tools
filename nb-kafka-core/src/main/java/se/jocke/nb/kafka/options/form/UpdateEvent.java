package se.jocke.nb.kafka.options.form;

/**
 *
 * @author jocke
 */
public final class UpdateEvent<S> {

    private final S source;

    public UpdateEvent(S source) {
        this.source = source;
    }

    public S getSource() {
        return source;
    }
}
