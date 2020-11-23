package ru.javawebinar.topjava.util;

import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.Jsr310DateTimeFormatAnnotationFormatterFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class NullableLocalDateTimeAnnotationFormatterFactory extends Jsr310DateTimeFormatAnnotationFormatterFactory {

    @Override
    public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
        DateTimeFormatter formatter = getFormatter(annotation, fieldType);
        return new NullableLocalDateTimePrinter(formatter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
        return new NullableLocalDateTimeParser((Class<? extends TemporalAccessor>) fieldType);
    }

    @Override
    protected DateTimeFormatter getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
        if ("LocalDate".equals(fieldType.getSimpleName())) {
            return DateTimeFormatter.ISO_LOCAL_DATE;
        } else if ("LocalTime".equals(fieldType.getSimpleName())) {
            return DateTimeFormatter.ISO_LOCAL_TIME;
        } else {
            throw new IllegalStateException("Unsupported type: " + fieldType + ", must be LocalDate/LocalTime");
        }
    }

    private static class NullableLocalDateTimePrinter implements Printer<TemporalAccessor> {
        private final DateTimeFormatter formatter;

        public NullableLocalDateTimePrinter(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public String print(TemporalAccessor partial, Locale locale) {
            return formatter.format(partial);
        }
    }

    private static class NullableLocalDateTimeParser implements Parser<TemporalAccessor> {
        private final Class<? extends TemporalAccessor> temporalAccessorType;

        public NullableLocalDateTimeParser(Class<? extends TemporalAccessor> temporalAccessorType) {
            this.temporalAccessorType = temporalAccessorType;
        }

        @Override
        public TemporalAccessor parse(String text, Locale locale) {
            if (LocalDate.class == temporalAccessorType) {
                return DateTimeUtil.parseLocalDate(text);
            }
            else if (LocalTime.class == temporalAccessorType) {
                return DateTimeUtil.parseLocalTime(text);
            }
            else {
                throw new IllegalStateException("Unsupported type: " + temporalAccessorType + ", must be LocalDate/LocalTime");
            }
        }
    }
}
