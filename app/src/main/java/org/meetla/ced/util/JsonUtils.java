package org.meetla.ced.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtils {
    public static String encode(Object object) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(object);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static <T> T decode(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
