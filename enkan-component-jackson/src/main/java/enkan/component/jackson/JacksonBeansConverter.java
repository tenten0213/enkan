package enkan.component.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import enkan.component.BeansConverter;
import enkan.component.ComponentLifecycle;
import enkan.exception.MisconfigurationException;

import java.io.IOException;

/**
 * @author kawasima
 */
public class JacksonBeansConverter extends BeansConverter {
    private ObjectMapper mapper;

    @Override
    public void copy(Object source, Object destination) {
        try {
            byte[] buf = mapper.writeValueAsBytes(source);
            mapper.readerForUpdating(destination).readValue(buf);
        } catch (IOException e) {
            throw new MisconfigurationException("jackson.IO_ERROR");
        }
    }

    @Override
    public <T> T createFrom(Object source, Class<T> destinationClass) {
        return mapper.convertValue(source, destinationClass);
    }

    @Override
    protected ComponentLifecycle lifecycle() {
        return new ComponentLifecycle<JacksonBeansConverter>() {
            @Override
            public void start(JacksonBeansConverter component) {
                component.mapper = new ObjectMapper();
                component.mapper.registerModule(new JavaTimeModule());
                component.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                component.mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
                component.mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                component.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }

            @Override
            public void stop(JacksonBeansConverter component) {
                component.mapper = null;
            }
        };
    }
}
