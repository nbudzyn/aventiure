package de.nb.aventiure2.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Eine Komplement im Rahmen einer @{@link Valenz}.
 */
@Documented
@Target(FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Komplement {
}