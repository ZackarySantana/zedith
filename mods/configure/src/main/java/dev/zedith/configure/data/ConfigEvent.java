package dev.zedith.configure.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.ui.builder.EventData;

public class ConfigEvent {

    public static final BuilderCodec<ConfigEvent> CODEC;

    static {
        BuilderCodec.Builder<ConfigEvent> builder = BuilderCodec.builder(ConfigEvent.class, ConfigEvent::new);

        for (ConfigEventType action : ConfigEventType.values()) {
            for (int i = 0; i < (action.isValueAction() ? 100 : 1); ++i) {
                int idxOnPage = i;
                String codecKey = action.toString();
                if (action.isValueAction()) {
                    codecKey += idxOnPage;
                }

                if (action.getCodec() == Codec.STRING || action.getCodec() == null) {
                    builder.append(
                            new KeyedCodec<>(codecKey, Codec.STRING),
                            (configEvent, newVal) -> configEvent.setVal(action, newVal, idxOnPage),
                            ConfigEvent::getRawVal
                    ).add();
                    continue;
                }

                if (action.getCodec() == Codec.BOOLEAN) {
                    builder.append(
                            new KeyedCodec<>(codecKey, Codec.BOOLEAN),
                            (configEvent, newVal) -> configEvent.setVal(
                                    action, String.valueOf(newVal), idxOnPage),
                            ConfigEvent::getBoolVal
                    ).add();
                }
            }
        }

        CODEC = builder.build();
    }

    private ConfigEventType action;
    private int indexOnPage;

    private String rawVal;

    private String strVal;
    private int intVal;
    private boolean boolVal;

    private boolean dataParseError;

    public static EventData pageEvent(ConfigEventType action) {
        return EventData.of(action.toString(), action.toString());
    }

    public static EventData pageEvent(ConfigEventType action, String value) {
        return EventData.of(action.toString(), value);
    }

    public static EventData valueEvent(ConfigEventType action, int index, String value) {
        return EventData.of(action.toString() + index, value);
    }

    public ConfigEventType getAction() {
        return action;
    }

    public int getIndexOnPage() {
        return indexOnPage;
    }

    private String getRawVal() {
        return rawVal;
    }

    private void setVal(ConfigEventType eventType, String configValue, int indexOnPage) {
        this.indexOnPage = indexOnPage;
        this.action = eventType;
        this.rawVal = configValue;

        try {
            switch (eventType.getDataType()) {
                case STRING:
                    this.strVal = configValue;
                    break;
                case BOOLEAN:
                    this.boolVal = Boolean.parseBoolean(configValue);
                    break;
                case INTEGER:
                    this.intVal = Integer.parseInt(configValue);
                    break;
                case null:
                    break;
            }
        } catch (Exception e) {
            this.dataParseError = true;
        }
    }

    public String getStrVal() {
        return strVal;
    }

    public int getIntVal() {
        return intVal;
    }

    public boolean getBoolVal() {
        return boolVal;
    }

    public boolean hasDataParseError() {
        return this.dataParseError;
    }

    public boolean dataIsEmpty() {
        return this.rawVal == null || this.rawVal.isEmpty();
    }
}
