package se.jocke.nb.kafka.options.form;

/**
 *
 * @author jocke
 */
public interface ValidationFailedDisplay {

    void show(String text);

    void clear();

    static final ValidationFailedDisplay NO_FAIL_DISPLAY = new ValidationFailedDisplay() {
        @Override
        public void show(String text) {
        }

        @Override
        public void clear() {
        }
    };
}
