package me.f0reach.bedrockdialog.response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Package-private implementation of {@link InputResponse} backed by a {@link Map}.
 */
class MapInputResponse implements InputResponse {

    private record DropdownResult(String id, int index) {}

    private final Map<String, Object> data;

    MapInputResponse(Map<String, Object> data) {
        this.data = Map.copyOf(data);
    }

    @Override
    public String getText(String key) {
        Object val = data.get(key);
        if (val instanceof String s) return s;
        throw new IllegalArgumentException("No text value for key: '" + key + "'");
    }

    @Override
    public float getFloat(String key) {
        Object val = data.get(key);
        if (val instanceof Float f) return f;
        throw new IllegalArgumentException("No float value for key: '" + key + "'");
    }

    @Override
    public boolean getBoolean(String key) {
        Object val = data.get(key);
        if (val instanceof Boolean b) return b;
        throw new IllegalArgumentException("No boolean value for key: '" + key + "'");
    }

    @Override
    public String getDropdownOptionId(String key) {
        Object val = data.get(key);
        if (val instanceof DropdownResult dr) return dr.id();
        throw new IllegalArgumentException("No dropdown value for key: '" + key + "'");
    }

    @Override
    public int getDropdownIndex(String key) {
        Object val = data.get(key);
        if (val instanceof DropdownResult dr) return dr.index();
        throw new IllegalArgumentException("No dropdown value for key: '" + key + "'");
    }

    @Override
    public boolean has(String key) {
        return data.containsKey(key);
    }

    static final class Builder implements InputResponse.Builder {
        private final Map<String, Object> data = new LinkedHashMap<>();

        @Override
        public Builder putText(String key, String value) {
            data.put(key, value);
            return this;
        }

        @Override
        public Builder putFloat(String key, float value) {
            data.put(key, value);
            return this;
        }

        @Override
        public Builder putBoolean(String key, boolean value) {
            data.put(key, value);
            return this;
        }

        @Override
        public Builder putDropdown(String key, String id, int index) {
            data.put(key, new DropdownResult(id, index));
            return this;
        }

        @Override
        public InputResponse build() {
            return new MapInputResponse(data);
        }
    }
}
