package dev.zedith.configure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigValue {

    /**
     * @return Optional key override. If empty, the field name will be used.
     */
    String key() default "";

    /**
     * @return Optional documentation string.
     */
    String documentation() default "";
}
