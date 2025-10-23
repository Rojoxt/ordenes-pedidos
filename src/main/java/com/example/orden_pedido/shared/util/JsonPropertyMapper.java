package com.example.orden_pedido.shared.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsonPropertyMapper {

    // Caché para mapear la ruta de campo de Java a la de JSON
    private static final Map<String, String> PATH_CACHE = new ConcurrentHashMap<>();

    // Método público que usa el caché
    public static String mapFieldPathToJsonPath(Object targetObject, String fieldPath) {
        String cacheKey = targetObject.getClass().getName() + ":" + fieldPath;
        if (PATH_CACHE.containsKey(cacheKey)) {
            return PATH_CACHE.get(cacheKey);
        }

        String mappedPath = processPathRecursively(targetObject.getClass(), fieldPath, new HashSet<>());
        PATH_CACHE.put(cacheKey, mappedPath);
        return mappedPath;
    }

    // Método recursivo para procesar la ruta y detectar ciclos
    private static String processPathRecursively(Class<?> currentClass, String fieldPath, Set<Class<?>> visitedClasses) {
        String[] pathParts = fieldPath.split("\\.", 2);
        String currentPart = pathParts[0];
        String remainingPath = pathParts.length > 1 ? pathParts[1] : null;

        Pattern pattern = Pattern.compile("(\\w+)(\\[\\d+\\])?");
        Matcher matcher = pattern.matcher(currentPart);

        if (!matcher.matches()) {
            return fieldPath;
        }

        String fieldName = matcher.group(1);
        String indexGroup = matcher.group(2) != null ? matcher.group(2) : "";

        try {
            Field field = currentClass.getDeclaredField(fieldName);
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

            String mappedName = (jsonProperty != null && !jsonProperty.value().isEmpty()) ? jsonProperty.value() : fieldName;
            StringBuilder jsonPathBuilder = new StringBuilder(mappedName);

            if (!indexGroup.isEmpty()) {
                Pattern indexPattern = Pattern.compile("\\[(\\d+)\\]");
                Matcher indexMatcher = indexPattern.matcher(indexGroup);
                if (indexMatcher.find()) {
                    int index = Integer.parseInt(indexMatcher.group(1));
                    jsonPathBuilder.append("[").append(index + 1).append("]");
                }

                Class<?> nextClass = null;
                if (field.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) field.getGenericType();
                    nextClass = (Class<?>) pType.getActualTypeArguments()[0];
                }

                if (remainingPath != null && nextClass != null) {
                    if (!visitedClasses.add(nextClass)) {
                        return jsonPathBuilder.toString() + "." + remainingPath; // Evita bucle infinito
                    }
                    return jsonPathBuilder.append(".").append(processPathRecursively(nextClass, remainingPath, visitedClasses)).toString();
                }

            } else if (remainingPath != null) {
                Class<?> nextClass = field.getType();
                if (!visitedClasses.add(nextClass)) {
                    return jsonPathBuilder.toString() + "." + remainingPath;
                }
                return jsonPathBuilder.append(".").append(processPathRecursively(nextClass, remainingPath, visitedClasses)).toString();
            }

            return jsonPathBuilder.toString();

        } catch (NoSuchFieldException e) {
            return fieldPath;
        }
    }
}