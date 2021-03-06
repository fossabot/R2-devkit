package org.r2.devkit.json.util;

/**
 * 内部解析时使用的占位符
 * 主要用于保存相对于解析字符串，当前已解析过的偏移量
 *
 * @author ruan4261
 */
public final class Holder<T> {

    private T object;

    private int offset;

    public T getObject() {
        return object;
    }

    public Holder<T> setObject(T object) {
        this.object = object;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public Holder<T> setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public Holder() {
    }

    public Holder(T object, int offset) {
        this.object = object;
        this.offset = offset;
    }

}
