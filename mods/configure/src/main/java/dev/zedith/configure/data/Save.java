package dev.zedith.configure.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Save {
    // Cache fields per class so we only reflect once.
    private static final ConcurrentHashMap<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

    private Save() {
    }

    /**
     * Copies all (non-static, non-final) instance fields from {@code source} into {@code target}.
     * Requires both objects to have the exact same runtime class.
     * <p>
     * Shallow copy: object references are copied as-is.
     */
    public static void copyInto(Object target, Object source) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(source, "source");

        Class<?> cls = target.getClass();
        if (source.getClass() != cls) {
            throw new IllegalArgumentException(
                    "copyInto requires same runtime class. target=" + cls.getName()
                            + " source=" + source.getClass().getName()
            );
        }

        Field[] fields = FIELD_CACHE.computeIfAbsent(cls, Save::computeFields);

        for (Field f : fields) {
            try {
                Object value = f.get(source);
                f.set(target, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Failed copying field " + f.getDeclaringClass().getName() + "#" + f.getName(), e);
            }
        }
    }

    private static Field[] computeFields(Class<?> cls) {
        // gather fields from class hierarchy
        java.util.ArrayList<Field> out = new java.util.ArrayList<>();

        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                int m = f.getModifiers();

                if (Modifier.isStatic(m)) continue;
                if (Modifier.isFinal(m)) continue;      // recommended default
                if (f.isSynthetic()) continue;
                if (Modifier.isTransient(m)) continue;  // optional: skip transient
//                if (f.getAnnotation(CopyIgnore.class) != null) continue;

                // Enable access once up front
                f.setAccessible(true);
                out.add(f);
            }
        }

        return out.toArray(Field[]::new);
    }
}
