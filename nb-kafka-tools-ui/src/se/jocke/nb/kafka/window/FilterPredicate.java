package se.jocke.nb.kafka.window;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import se.jocke.nb.kafka.client.NBKafkaConsumerRecord;
import se.jocke.nb.kafka.options.form.InputConverter;

class FilterPredicate implements Predicate<NBKafkaConsumerRecord>, InputConverter<Predicate<NBKafkaConsumerRecord>> {
    
    private final String expression;
    private final boolean isRegex;
    private final Function<NBKafkaConsumerRecord, String> provider;
    private Pattern pattern;
    private final Supplier<Boolean> supplier;

    public FilterPredicate(String expression, Supplier<Boolean> supplier, Function<NBKafkaConsumerRecord, String> provider) {
        this.expression = expression;
        this.supplier = supplier;
        this.isRegex = supplier.get();
        this.provider = provider;
        if (this.isRegex) {
            this.pattern = Pattern.compile(expression);
        }
    }

    public FilterPredicate(Supplier<Boolean> supplier, Function<NBKafkaConsumerRecord, String> provider) {
        this(null, supplier, provider);
    }

    @Override
    public boolean test(NBKafkaConsumerRecord record) {
        String text = provider.apply(record);
        if (text == null || expression == null) {
            return true;
        }
        return isRegex ? pattern.matcher(text).matches() : text.contains(expression);
    }

    @Override
    public FilterPredicate fromString(String value) {
        return new FilterPredicate(value, supplier, provider);
    }
    
}
