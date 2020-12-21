package de.nb.aventiure2.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation hängt mit dem {@link de.nb.aventiure2.processor.VerbValenzAnnotationProcessor}
 * zusammen:  Wenn man eine Verb-Valenz-Klasse mit dieser Annotation versieht, dann werden
 * automatisch Klassen erzeugt, in denen die entsprechenden Felder, die mit
 * {@link Argument} versehen sind, noch nicht gesetzt sind. Man kann also typsicher die
 * Argumente der Reihe nach angeben.
 */
@Documented
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.CLASS)
public @interface VerbValenz {
}