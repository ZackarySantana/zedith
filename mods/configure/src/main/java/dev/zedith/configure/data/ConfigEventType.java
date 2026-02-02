package dev.zedith.configure.data;

import com.hypixel.hytale.codec.Codec;

public enum ConfigEventType {

    CLOSE,
    SAVE,
    OPEN_MODS_PAGE,
    RESET_VALUE(Scope.VALUE),
    SET_PAGE(Scope.PAGE, DataType.INTEGER),
    SET_PAGE2(Scope.PAGE, DataType.INTEGER),
    OPEN_MOD_PAGE(Scope.PAGE, DataType.INTEGER),
    FILTER_KEYS(Scope.PAGE, DataType.STRING, Codec.STRING),
    CHANGE_PAGE_SIZE(Scope.PAGE, DataType.INTEGER, Codec.STRING),
    STR_CONFIG_VALUE_CHANGE_EVENT(Scope.VALUE, DataType.STRING, Codec.STRING),
    INT_CONFIG_VALUE_CHANGE_EVENT(Scope.VALUE, DataType.INTEGER, Codec.STRING),
    BOOL_CONFIG_VALUE_CHANGE_EVENT(Scope.VALUE, DataType.BOOLEAN, Codec.BOOLEAN);

    // The scope tells the event handler what this action is affecting.
    private final Scope scope;
    // The dataType tells the event handler what type to parse
    // the value in to.
    private final DataType dataType;
    // The codec tells the event handler that this action is pulling in data
    // from a UI element.
    private final Codec<?> codec;

    ConfigEventType() {
        this.scope = Scope.PAGE;
        this.dataType = null;
        this.codec = null;
    }

    ConfigEventType(Scope scope) {
        this.scope = scope;
        this.dataType = null;
        this.codec = null;
    }

    ConfigEventType(Scope scope, DataType dataType) {
        this.scope = scope;
        this.dataType = dataType;
        this.codec = null;
    }

    ConfigEventType(Scope scope, DataType dataType, Codec<?> codec) {
        this.scope = scope;
        this.dataType = dataType;
        this.codec = codec;
    }

    public static ConfigEventType fromString(String name) {
        for (ConfigEventType eventType : ConfigEventType.values()) {
            if (eventType.toString().equals(name)) {
                return eventType;
            }
        }
        return null;
    }

    DataType getDataType() {
        return dataType;
    }

    Codec<?> getCodec() {
        return codec;
    }

    @Override
    public String toString() {
        if (this.codec != null) {
            return "@" + this.name();
        }
        return this.name();
    }

    public boolean isPageAction() {
        return scope == Scope.PAGE;
    }

    public boolean isValueAction() {
        return scope == Scope.VALUE;
    }

    public enum Scope {PAGE, VALUE}

    public enum DataType {STRING, INTEGER, BOOLEAN}
}
