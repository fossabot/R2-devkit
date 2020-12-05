package org.r2.devkit;

import org.r2.devkit.bean.BeanUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author ruan4261
 */
public final class Cast {

    private Cast() {
        throw new AssertionError("No Cast instances for you!");
    }

    private static final String EMPTY = "";

    /**
     * 调用{@code val.toString()}，如果{@code val}为{@code null}，返回参数{@code defaultVal}，
     */
    public static String o2String(Object val, String defaultVal) {
        String s;
        return val == null ? defaultVal : (s = val.toString()) == null ? defaultVal : s;
    }

    /**
     * 参数为NULL时返回''空字符串
     */
    public static String o2String(Object val) {
        return o2String(val, EMPTY);
    }

    /**
     * 默认返回{@code BigDecimal.ZERO}
     */
    public static BigDecimal o2BigDecimal(Object val) {
        return o2BigDecimal(val, BigDecimal.ZERO);
    }

    public static BigDecimal o2BigDecimal(Object val, BigDecimal defaultVal) {
        if (val == null) return defaultVal;
        try {
            if (val instanceof Integer) {
                return new BigDecimal((Integer) val);
            } else if (val instanceof Long) {
                return new BigDecimal((Long) val);
            } else if (val instanceof BigInteger) {
                return new BigDecimal((BigInteger) val);
            } else if (val instanceof Number) {
                return new BigDecimal(val.toString());
            } else if (val instanceof CharSequence || BeanUtil.hasOwnMethod(val.getClass(), "toString", 8)) {
                return new BigDecimal(val.toString());
            }
        } catch (ArithmeticException | NumberFormatException ignore) {
            return defaultVal;
        }
        return defaultVal;
    }

    /**
     * 默认返回-1
     */
    public static int o2Integer(Object val) {
        return o2Integer(val, -1);
    }

    public static int o2Integer(Object val, int defaultVal) {
        if (val == null) return defaultVal;
        try {
            if (val instanceof Number) {
                return ((Number) val).intValue();
            } else if (val instanceof CharSequence || BeanUtil.hasOwnMethod(val.getClass(), "toString", 8)) {
                return Integer.parseInt(val.toString());
            }
        } catch (NumberFormatException ignore) {
            return defaultVal;
        }
        return defaultVal;
    }

    public static long o2Long(Object val) {
        return o2Long(val, -1L);
    }

    public static long o2Long(Object val, long defaultVal) {
        if (val == null) return defaultVal;
        try {
            if (val instanceof Number) {
                return ((Number) val).longValue();
            } else if (val instanceof CharSequence || BeanUtil.hasOwnMethod(val.getClass(), "toString", 8)) {
                return Long.parseLong(val.toString());
            }
        } catch (NumberFormatException ignore) {
            return defaultVal;
        }
        return defaultVal;
    }
}
