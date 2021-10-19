package se.jocke.nb.kafka.window;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;
import se.jocke.nb.kafka.options.form.InputConverter;

class DateRangePredicate implements Predicate<NBKafkaConsumerRecord>, InputConverter<Predicate<NBKafkaConsumerRecord>> {

    private static final String DATE_SEPARATOR = " / ";

    private static final String YYYY_M_MDD_H_HMMSS = "yyyy-MM-dd HH:mm:ss";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(YYYY_M_MDD_H_HMMSS);

    private final Date from;

    private final Date to;

    public DateRangePredicate(String text) {
        if (text != null && !text.isBlank()) {
            String[] split = text.split(DATE_SEPARATOR);
            try {
                if (split.length == 2) {
                    this.from = FORMAT.parse(split[0]);
                    this.to = FORMAT.parse(split[1]);
                    if (from.after(to)) {
                        throw new IllegalArgumentException("Invalid date range from not after to");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid date range pattern");
                }
            } catch (ParseException ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            this.from = null;
            this.to = null;
        }
    }

    public DateRangePredicate() {
        this(null);
    }

    @Override
    public boolean test(NBKafkaConsumerRecord t) {
        if (to != null && from != null) {
            Date recorded = new Date(t.getTimestamp());
            return recorded.after(from) && recorded.before(to);
        }
        return true;
    }

    @Override
    public DateRangePredicate fromString(String value) {
        return new DateRangePredicate(value);
    }
}
