package org.r2.devkit.serialize;

import org.r2.devkit.exception.runtime.IllegalDataException;
import org.r2.devkit.exception.runtime.UnsupportedClassException;
import org.r2.devkit.reflect.ReflectUtil;
import org.r2.devkit.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 * 可自定义的多类型的序列化方案
 *
 * 如果一个类拥有自己的序列化器，那么在序列化时，这个类的实例总会选择自身类的序列化器
 * 如果一个类不拥有自己的序列化器，那么他将会去寻找自己的超类或实现的接口的序列化器
 * 如果其超类和实现接口都有序列化器（也就是寻找到多个序列化器），其将会根据优先级进行选择
 * 如果一个类的序列化器的优先级(level)小于0，那么它将不会允许该类以外的任何类实例使用该序列化器
 * 在注册时，如果不录入level，level默认为-1，只允许注册它的类调用，子类无法调用
 *
 * @author ruan4261
 */
public final class CustomSerializer implements Cloneable, Serializable {
    private static final long serialVersionUID = 2881383912572573956L;
    private static final int DEFAULT_CAPACITY = 8;
    private final Map<Class, Bucket> customize;

    public CustomSerializer() {
        this.customize = new HashMap<>(DEFAULT_CAPACITY);
    }

    public CustomSerializer(int initialCapacity) {
        this.customize = new HashMap<>(initialCapacity);
    }

    public CustomSerializer(Map<Class, Bucket> map) {
        Assert.notNull(map);
        this.customize = new HashMap<>(map);
    }

    /**
     * 使用自定义序列化函数进行序列化
     * 如果有类型完全一致的序列化器，直接使用
     * 如果没有类型完全一致的序列化器，则尝试获得优先级最高的父类序列化器
     *
     * @throws UnsupportedClassException 不支持此类型的自定义序列化
     */
    @SuppressWarnings("unchecked")
    public String serialize(Object object) {
        Assert.notNull(object);
        if (this.isExistClassSerializer(object))
            return this.classSerializer(object).serialize(object);
        else
            return this.prioritySerializer(object).serialize(object);
    }

    /** @see CustomSerializer#prioritySerializer(Class) */
    public Serializer prioritySerializer(Object object) {
        return this.prioritySerializer(object.getClass());
    }

    /**
     * 获取对于目标类型优先级最高的序列化器
     *
     * @throws UnsupportedClassException 不支持此类型的自定义序列化
     */
    @SuppressWarnings("unchecked")
    public <T> Serializer<T> prioritySerializer(Class<T> clazz) {
        Set<Class> classes = ReflectUtil.getAllSuper(clazz);

        final int[] max = {-1};
        final Serializer[] functions = {null};

        classes.stream().anyMatch(c -> {
            Bucket bucket = this.customize.get(c);
            if (bucket != null) {
                if (bucket.T == clazz) {
                    functions[0] = bucket.serializer;
                    return true;
                } else if (bucket.level > max[0]) {
                    max[0] = bucket.level;
                    functions[0] = bucket.serializer;

                    return max[0] == Integer.MAX_VALUE;
                }
            }
            return false;
        });

        if (functions[0] == null) throw new UnsupportedClassException(clazz);
        return functions[0];
    }

    public Serializer classSerializer(Object object) {
        return this.classSerializer(object.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> classSerializer(Class<T> clazz) {
        Serializer<T> serializer = this.customize.get(clazz).serializer;
        if (serializer == null)
            throw new UnsupportedClassException(clazz, clazz.toString() + " doesn't have own serializer.");
        return serializer;
    }

    /** @see CustomSerializer#hasCustomizer(Class) */
    public boolean hasCustomizer(Object object) {
        return this.hasCustomizer(object.getClass());
    }

    /**
     * 判断一个类有没有自定义的序列化机制
     * 如果其实现的接口或其超类或其超类实现的接口有自定义序列化机制，也算作其的自定义序列化机制
     */
    public boolean hasCustomizer(Class clazz) {
        if (isExistClassSerializer(clazz)) return true;
        Set<Class> allClass = ReflectUtil.getAllSuper(clazz);
        for (Class c : allClass) {
            Bucket bucket = this.customize.get(c);
            if (bucket != null) {
                if ((bucket.level >>> 31) != 1)
                    return true;
            }
        }
        return false;
    }

    public boolean isExistClassSerializer(Object object) {
        return this.isExistClassSerializer(object.getClass());
    }

    public boolean isExistClassSerializer(Class clazz) {
        return this.customize.containsKey(clazz);
    }

    public <T> void register(Class<T> clazz, int level, Serializer<T> function) {
        this.customize.put(clazz, new Bucket<>(clazz, level, function));
    }

    public <T> void register(Class<T> clazz, Serializer<T> function) {
        this.customize.put(clazz, new Bucket<>(clazz, function));
    }

    public int queryLevel(Class clazz) {
        return this.customize.get(clazz).level;
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> querySerializer(Class<T> clazz) {
        return this.customize.get(clazz).serializer;
    }

    public void update(Class clazz, int level) {
        Bucket bucket = this.customize.get(clazz);
        if (bucket == null)
            throw new IllegalDataException(clazz.getTypeName() + " doesn't have custom serializer.");

        bucket.level = level;
    }

    public <T> void update(Class<T> clazz, Serializer<T> function) {
        Bucket bucket = this.customize.get(clazz);
        if (bucket == null)
            throw new IllegalDataException(clazz.getTypeName() + " doesn't have custom serializer.");

        bucket.serializer = function;
    }

    public void delete(Class clazz) {
        this.customize.remove(clazz);
    }

    @Override
    public Object clone() {
        return new CustomSerializer(this.customize);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CustomSerializer that = (CustomSerializer) object;
        return Objects.equals(customize, that.customize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customize);
    }

    @Override
    public String toString() {
        return customize.toString();
    }

    /**
     * 序列化函数以及其优先级
     * 优先级低于0时，禁止继承使用
     */
    private static final class Bucket<T> implements Serializable {
        private static final long serialVersionUID = 3882202669581823538L;
        private Class<T> T;
        private int level;
        private Serializer<T> serializer;

        public Bucket(Class<T> T, int level, Serializer<T> serializer) {
            this.T = T;
            this.level = level;
            this.serializer = serializer;
        }

        public Bucket(Class<T> T, Serializer<T> serializer) {
            this.T = T;
            this.level = -1;
            this.serializer = serializer;
        }
    }

}