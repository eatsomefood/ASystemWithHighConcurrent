package com.star.highconcurrent.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class BeanUtil {
    /**
     * 将Map中的值按字段名对应赋值给对象c的字段
     * @param c 目标对象（需被赋值的对象）
     * @param map 数据源（key为字段名，value为要赋值的值）
     * @param <T> 泛型类型
     * @return 赋值后的对象c
     */
    public static <T> T copyBean(T c, Map<Object, Object> map) {
        if (c == null || map == null) {
            return c; // 空对象或空Map直接返回
        }

        // 获取对象c的所有声明字段（包括private、protected等）
        Field[] fields = c.getClass().getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName(); // 字段名（如"id"、"createTime"）
            if ("serialVersionUID".equals(fieldName)){
                continue;
            }
            Object value = map.get(fieldName);  // 从Map中获取对应字段名的值

            if (value == null) {
                continue; // 若Map中无对应值，跳过该字段
            }

            try {
                // 设置字段可访问（私有字段需要这一步，否则无法赋值）
                field.setAccessible(true);

                // 将Map中的值转换为字段的类型（如String转LocalDateTime、Integer等）
                Object convertedValue = convertValue(value, field.getType());

                // 给字段赋值
                field.set(c, convertedValue);

            } catch (Exception e) {
                // 捕获反射或类型转换异常，包装后抛出（便于定位问题）
                throw new RuntimeException("字段[" + fieldName + "]赋值失败，值：" + value, e);
            }
        }

        return c;
    }

    /**
     * 将Map中的值转换为目标字段的类型
     * @param value Map中的原始值（通常是String，如从Redis Hash中获取的字符串）
     * @param targetType 字段的目标类型（如Integer、LocalDateTime等）
     * @return 转换后的目标类型值
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 若值已是目标类型，直接返回（如Map中的值本身就是Integer，字段类型也是Integer）
        if (targetType.isInstance(value)) {
            return value;
        }

        // 统一转为字符串处理（适用于从Redis等存储中获取的字符串值）
        String valueStr = value.toString().trim();

        // 1. 字符串类型
        if (targetType == String.class) {
            return valueStr;
        }

        // 2. 整数类型（int/Integer）
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(valueStr);
        }

        // 3. 长整数类型（long/Long）
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(valueStr);
        }

        // 4. 布尔类型（boolean/Boolean）
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(valueStr);
        }

        // 5. 双精度类型（double/Double）
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(valueStr);
        }

        // 6. 本地日期时间（LocalDateTime，需指定格式）
        if (targetType == LocalDateTime.class) {
            return LocalDateTime.parse(valueStr);
        }

        // 7. 本地日期（LocalDate，需指定格式）
        if (targetType == LocalDate.class) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(valueStr, formatter);
        }

        // 其他类型可继续扩展（如LocalTime、BigDecimal等）
        throw new IllegalArgumentException("不支持的类型转换：原始值类型[" + value.getClass() + "] -> 目标字段类型[" + targetType + "]");
    }
}
