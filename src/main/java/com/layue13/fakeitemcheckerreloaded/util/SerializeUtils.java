package com.layue13.fakeitemcheckerreloaded.util;


import net.minecraft.util.com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class SerializeUtils {
    public static Map<String, Object> generateMapFormObject(Object object) {
        Map<String, Object> result = new LinkedTreeMap<>();
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                Object value = declaredField.get(object);
                if (Objects.isNull(value)) {
                    continue;
                }
                result.put(declaredField.getName(), value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
