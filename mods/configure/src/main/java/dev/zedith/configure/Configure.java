package dev.zedith.configure;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.builder.BuilderField;
import com.hypixel.hytale.codec.validation.Validator;
import dev.zedith.configure.config.WrappedConfig;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Configure {

    protected final static Map<String, WrappedConfig<?>> configs = new ConcurrentHashMap<>();
    private final static Map<Class<?>, Function<?, ?>> codecAppenders = new ConcurrentHashMap<>();

    public static void registerConfig(WrappedConfig<?> config) {
        configs.put(config.metadata().name().toLowerCase(), config);
    }

    public static void registerConfig(String name, WrappedConfig<?> config) {
        configs.put(name.toLowerCase(), config);
    }

    public static <T> BuilderCodec.Builder<T> build(Class<T> type, Supplier<T> constructor) {
        BuilderCodec.Builder<T> b = BuilderCodec.builder(type, constructor);

        Field[] fields = type.getDeclaredFields();

        for (Field f : fields) {
            CodecValue[] values = f.getAnnotationsByType(CodecValue.class);

            for (CodecValue value : values) {
                String key = capitalize(!value.key().isEmpty() ? value.key() : f.getName());
                BuilderField.FieldBuilder<T, ?, BuilderCodec.Builder<T>> stage;

                if (f.getType() == String.class) {
                    Accessors<T, String> a = accessors(type, f, String.class);
                    stage = b.append(new KeyedCodec<>(key, BuilderCodec.STRING), a.setter, a.getter);
                } else if (f.getType() == Integer.class) {
                    Accessors<T, Integer> a = accessors(type, f, Integer.class);
                    stage = b.append(new KeyedCodec<>(key, BuilderCodec.INTEGER), a.setter, a.getter);
                } else if (f.getType() == int.class) {
                    Accessors<T, Integer> a = accessors(type, f, int.class);
                    stage = b.append(new KeyedCodec<>(key, BuilderCodec.INTEGER), a.setter, a.getter);
                } else if (f.getType() == Boolean.class) {
                    Accessors<T, Boolean> a = accessors(type, f, Boolean.class);
                    stage = b.append(new KeyedCodec<>(key, BuilderCodec.BOOLEAN), a.setter, a.getter);
                } else if (f.getType() == boolean.class) {
                    Accessors<T, Boolean> a = accessors(type, f, boolean.class);
                    stage = b.append(new KeyedCodec<>(key, BuilderCodec.BOOLEAN), a.setter, a.getter);
                } else {
                    throw new IllegalStateException("Unsupported field type: " + f.getType());
                }

                Class<? extends Validator> validator = value.validator();
                if (validator != null) {
                    try {
                        Field instanceField = validator.getField("INSTANCE");
                        int mods = instanceField.getModifiers();
                        if (Modifier.isStatic(mods) && Validator.class.isAssignableFrom(instanceField.getType())) {
                            Object instance = instanceField.get(null);
                            if (instance instanceof Validator) {
                                stage.addValidator((Validator) instance);
                            }
                        }
                    } catch (NoSuchFieldException _) {
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }


                stage.documentation(value.documentation()).add();
            }
        }

        return b;
    }

    private static <T, V> Accessors<T, V> accessors(Class<T> owner, Field field, Class<V> expectedType) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, MethodHandles.lookup());
            VarHandle vh = lookup.unreflectVarHandle(field);

            if (vh.varType() != expectedType) {
                throw new IllegalStateException(
                        "Type mismatch for codec: expected '%s' but was '%s' for field name '%s'.%s".formatted(
                                expectedType.getTypeName(),
                                vh.varType().getTypeName(),
                                field.getDeclaringClass().getName(),
                                field.getName()
                        ));
            }

            //noinspection unchecked
            return new Accessors<>(vh::set, (obj) -> (V) vh.get(obj));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access field: " + field, e);
        }
    }

    public static Map<String, WrappedConfig<?>> getConfigs() {
        return configs;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        }
        return Character.toUpperCase(first) + s.substring(1);
    }

    private record Accessors<K, V>(BiConsumer<K, V> setter, Function<K, V> getter) {
    }
}
