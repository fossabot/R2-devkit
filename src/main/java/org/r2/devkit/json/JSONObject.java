package org.r2.devkit.json;

import static org.r2.devkit.json.JSONToken.*;

import org.r2.devkit.json.custom.CustomSerializer;
import org.r2.devkit.json.custom.CustomizableSerialize;
import org.r2.devkit.json.serialize.JSONSerializer;
import org.r2.devkit.json.util.JSONParseCheck;
import org.r2.devkit.json.util.JSONStringParser;
import org.r2.devkit.json.util.Holder;
import org.r2.devkit.util.Assert;

import java.util.*;

/**
 * @author ruan4261
 */
public final class JSONObject extends JSON implements CustomizableSerialize, Map<String, Object> {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_CAPACITY = 8;
    private final Map<String, Object> container;
    // serialization solution
    private CustomSerializer customSerializer;

    public JSONObject() {
        this(DEFAULT_CAPACITY);
    }

    public JSONObject(int initialCapacity) {
        this.container = new HashMap<>(initialCapacity);
    }

    public JSONObject(Map<String, Object> map) {
        Assert.notNull(map, "map");
        this.container = new HashMap<>(map);
    }

    @Override
    public void setCustomSerializer(CustomSerializer customSerializer) {
        this.customSerializer = customSerializer;
    }

    @Override
    public void addCustomSerializer(CustomSerializer customSerializer) {
        if (this.customSerializer == null)
            this.setCustomSerializer(customSerializer);
        else
            this.customSerializer.putAll(customSerializer);
    }

    @Override
    public CustomSerializer getCustomSerializer() {
        return this.customSerializer;
    }

    /**
     * 完整字符串解析调用接口
     */
    public static JSONObject parseObject(String str) {
        Holder<JSONObject> holder = JSONStringParser.parse2JSONObject(str, 0);

        // 判断字符串剩余部分是否可忽略，不可忽略则抛出异常
        JSONParseCheck.ignore(str, holder.getOffset(), "String cannot parse to JSONObject : " + str);

        return holder.getObject();
    }

    /**
     * output json string
     */
    @Override
    public String toJSONString() {
        final StringBuilder builder = new StringBuilder(String.valueOf(LBRACE));

        // body
        container.forEach((k, v) -> {
            // key
            builder.append(DOUBLE_QUOT).append(k).append(DOUBLE_QUOT);

            // :
            builder.append(COLON);

            // value
            builder.append(JSONSerializer.serializer(v, this.customSerializer));

            // ,
            builder.append(COMMA);
        });

        // delete last comma
        int last = builder.length() - 1;
        if (builder.charAt(last) == COMMA)
            builder.deleteCharAt(last);

        builder.append(RBRACE);
        return builder.toString();
    }

    @Override
    public Object clone() {
        return new JSONObject(this.container);
    }

    /* Override */

    @Override
    public int size() {
        return this.container.size();
    }

    @Override
    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.container.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.container.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.container.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return this.container.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return this.container.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        this.container.putAll(m);
    }

    @Override
    public void clear() {
        this.container.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.container.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.container.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.container.entrySet();
    }

}