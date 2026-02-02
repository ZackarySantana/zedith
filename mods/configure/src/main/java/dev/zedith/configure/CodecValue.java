package dev.zedith.configure;

import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.codec.validation.validator.NonNullValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CodecValue {

    /**
     * @return Optional key override. If empty, the field name will be used.
     */
    String key() default "";

    /**
     * @return Optional documentation string.
     */
    String documentation() default "";

    Class<? extends Validator> validator() default NonNullValidator.class;
}
