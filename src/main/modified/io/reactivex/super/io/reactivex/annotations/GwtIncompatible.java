package io.reactivex.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.TYPE, ElementType.METHOD,
        ElementType.CONSTRUCTOR, ElementType.FIELD })
@Documented
public @interface GwtIncompatible {
    /**
     * Describes why the annotated element is incompatible with GWT. Since this is
     * generally due to a dependence on a type/method which GWT doesn't support,
     * it is sufficient to simply reference the unsupported type/method. E.g.
     * "Class.isInstance".
     */
    String value() default  "";
}
