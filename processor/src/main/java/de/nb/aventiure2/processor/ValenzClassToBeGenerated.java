package de.nb.aventiure2.processor;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.processing.Filer;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
class ValenzClassToBeGenerated {
    private final String packageName;
    private final String qualifiedNameWithoutLeerstellen;
    private final ImmutableList<ArgumentField> alleArgumente;
    private final ImmutableList<ArgumentField> gefuellteArgumente;
    private final ImmutableList<ArgumentField> leerstellen;
    private final ImmutableList<VariableElement> sonstigeFelder;


    ValenzClassToBeGenerated(final String packageName,
                             final String qualifiedNameWithoutLeerstellen,
                             final Collection<ArgumentField> alleArgumente,
                             final Collection<ArgumentField> gefuellteArgumente,
                             final Collection<ArgumentField> leerstellen,
                             final Collection<VariableElement> sonstigeFelder) {
        this.qualifiedNameWithoutLeerstellen = qualifiedNameWithoutLeerstellen;
        checkArgument(!leerstellen.isEmpty(), "leerstellen war leer!");

        this.packageName = packageName;
        this.alleArgumente = ImmutableList.copyOf(alleArgumente);
        this.gefuellteArgumente = ImmutableList.copyOf(gefuellteArgumente);
        this.leerstellen = ImmutableList.copyOf(leerstellen);
        this.sonstigeFelder = ImmutableList.copyOf(sonstigeFelder);
    }

    void generateCode(final Types typeUtils, final Elements elementUtils,
                      final Filer filer) throws IOException {
        final String className = buildClassName();
        final JavaFileObject jfo = filer.createSourceFile(
                packageName + "." + className);

        final AnnotationSpec generated = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", "Module processor")
                .build();

        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addJavadoc(createClassJavadoc())
                .addAnnotation(generated)
                .addModifiers(Modifier.PUBLIC);

        addSonstigeFelder(classBuilder);
        addGefuellteArgumente(classBuilder);
        addLeerstellen(classBuilder);

        final TypeSpec typeSpec = classBuilder.build();
        final JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();

        try (Writer writer = jfo.openWriter()) {
            javaFile.writeTo(writer);
        }
    }

    private void addSonstigeFelder(final TypeSpec.Builder classBuilder) {
        for (final VariableElement sonstigesFeld : sonstigeFelder) {
            final DeclaredType declaredType =
                    (DeclaredType) sonstigesFeld.asType();
            final ClassName className = ClassName.bestGuess(declaredType.asElement().toString());

            final FieldSpec.Builder fieldSpecBuilder =
                    FieldSpec.builder(className, sonstigesFeld.getSimpleName().toString())
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL);

            for (final AnnotationMirror annotationMirror : sonstigesFeld.getAnnotationMirrors()) {
                fieldSpecBuilder.addAnnotation(annotationMirror.getAnnotationType().getClass());
            }

            final FieldSpec field = fieldSpecBuilder.build();

            classBuilder.addField(field);
        }
    }

    private String createClassJavadoc() {
        return "Ein Praedikat, gesetzt "
                + (gefuellteArgumente.size() == 1 ? "ist" : "sind")
                + " "
                + (gefuellteArgumente.isEmpty() ?
                "noch nichts" :
                Joiner.on(", ").join(toNames(gefuellteArgumente)))
                + " und fuer "
                + Joiner.on(", ").join(toNames(leerstellen))
                + " "
                + (leerstellen.size() == 1 ?
                "besteht eine Leerstelle" :
                "bestehen Leerstellen")
                + ".";
    }

    private void addGefuellteArgumente(final TypeSpec.Builder classBuilder) {
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        for (final VariableElement sonstigesFeld : sonstigeFelder) {
            final DeclaredType declaredType =
                    (DeclaredType) sonstigesFeld.asType();
            final ClassName className = ClassName.bestGuess(declaredType.asElement().toString());

            final String fieldName = sonstigesFeld.getSimpleName().toString();
            constructorBuilder.addParameter(className, fieldName)
                    .addStatement("this.$N = $N", fieldName, fieldName);
        }

        for (final ArgumentField argument : gefuellteArgumente) {
            final ClassName argumentClassName = ClassName.bestGuess(argument.getType());

            final FieldSpec argumentField = FieldSpec.builder(argumentClassName, argument.getName())
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .addAnnotation(Nonnull.class)
                    .build();

            classBuilder.addField(argumentField);

            constructorBuilder
                    .addParameter(argumentClassName, argument.getName())
                    .addStatement("this.$N = $N", argument.getName(), argument.getName());
        }

        final MethodSpec constructor = constructorBuilder.build();
        classBuilder.addMethod(constructor);
    }

    private void addLeerstellen(final TypeSpec.Builder classBuilder) {
        if (leerstellen.size() == 1) {
            final ArgumentField leerstelle = leerstellen.iterator().next();
            if (leerstelle.getType().equals("de.nb.aventiure2.german.base.SubstantivischePhrase")) {
                classBuilder.addSuperinterface(ClassName.bestGuess(
                        "de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle"));

                final ClassName leerstelleClassName = ClassName.bestGuess(leerstelle.getType());

                final MethodSpec mit = MethodSpec.methodBuilder("mit")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(qualifiedNameWithoutLeerstellen))
                        .addParameter(leerstelleClassName, "phrase")
                        .addCode("return mit")
                        .addCode(capitalize(leerstelle.getName()))
                        .addCode("(phrase);")
                        .build();

                classBuilder.addMethod(mit);
            } else if (leerstelle.getType().equals("de.nb.aventiure2.german.base.Praedikativum")) {
                classBuilder.addSuperinterface(ClassName.bestGuess(
                        "de.nb.aventiure2.german.praedikat"
                                + ".PraedikatMitEinerPraedikativumLeerstelle"));

                final ClassName leerstelleClassName = ClassName.bestGuess(leerstelle.getType());

                final MethodSpec mit = MethodSpec.methodBuilder("mit")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(qualifiedNameWithoutLeerstellen))
                        .addParameter(leerstelleClassName, "phrase")
                        .addCode("return mit")
                        .addCode(capitalize(leerstelle.getName()))
                        .addCode("(phrase);")
                        .build();

                classBuilder.addMethod(mit);
            } else if (leerstelle.getType()
                    .equals("de.nb.aventiure2.german.base.WoertlicheRede")) {
                classBuilder.addSuperinterface(ClassName.bestGuess(
                        "de.nb.aventiure2.german.praedikat"
                                + ".PraedikatMitEinerLeerstelleFuerWoertlicheRede"));
            }
        } else {
            classBuilder.addSuperinterface(ClassName.bestGuess(
                    "de.nb.aventiure2.german.praedikat.Praedikat"));
        }

        for (final ArgumentField leerstelle : leerstellen) {
            addLeerstelle(classBuilder, leerstelle);
        }
    }

    private void addLeerstelle(final TypeSpec.Builder classBuilder,
                               final ArgumentField leerstelle) {
        final ClassName leerstelleClassName = ClassName.bestGuess(leerstelle.getType());

        final Collection<ArgumentField> leerstelleAufgefuellt = fuelleAuf(leerstelle);

        final ClassName leerstelleAufgefuelltClassName = ClassName.bestGuess(
                buildClassName(qualifiedNameWithoutLeerstellen, alleArgumente,
                        leerstelleAufgefuellt));
        final MethodSpec.Builder mitLeerstelleMethodBuilder = MethodSpec.methodBuilder(
                "mit" + capitalize(leerstelle.getName()))
                .addModifiers(Modifier.PUBLIC)
                .returns(leerstelleAufgefuelltClassName)
                .addParameter(leerstelleClassName, leerstelle.getName())
                .addCode("return new $T(", leerstelleAufgefuelltClassName);

        boolean first = true;
        for (final VariableElement sonstigesFeld : sonstigeFelder) {
            if (!first) {
                mitLeerstelleMethodBuilder.addCode(", ");
            }

            final String fieldName = sonstigesFeld.getSimpleName().toString();
            mitLeerstelleMethodBuilder.addCode("$N", fieldName);
            first = false;
        }

        for (final ArgumentField argument : leerstelleAufgefuellt) {
            if (!first) {
                mitLeerstelleMethodBuilder.addCode(", ");
            }
            mitLeerstelleMethodBuilder.addCode(argument.getName());
            first = false;
        }

        final MethodSpec mitLeerstelle = mitLeerstelleMethodBuilder
                .addCode(");")
                .build();

        classBuilder.addMethod(mitLeerstelle);
    }

    private Collection<ArgumentField> fuelleAuf(final ArgumentField leerstelle) {
        final ImmutableList.Builder<ArgumentField> res = ImmutableList.builder();

        for (final ArgumentField argument : alleArgumente) {
            if (gefuellteArgumente.contains(argument) || leerstelle.equals(argument)) {
                res.add(argument);
            }
        }

        return res.build();
    }

    private static Iterable<?> toNames(final Collection<ArgumentField> argumente) {
        return argumente.stream().map(ArgumentField::getName)
                .map(ValenzClassToBeGenerated::capitalize)
                .collect(ImmutableList.toImmutableList());
    }

    private String buildClassName() {
        return buildClassName(qualifiedNameWithoutLeerstellen, alleArgumente, gefuellteArgumente);
    }

    private static String buildClassName(
            final String qualifiedNameWithoutLeerstellen,
            final Collection<ArgumentField> alleArgumente,
            final Collection<ArgumentField> gefuellteArgumente) {
        final Sets.SetView<ArgumentField> leerstellen = Sets.difference(
                Sets.newLinkedHashSet(alleArgumente),
                Sets.newLinkedHashSet(gefuellteArgumente));

        if (leerstellen.isEmpty()) {
            return qualifiedNameWithoutLeerstellen;
        }

        final StringBuilder res = new StringBuilder();
        res.append(buildBaseName(qualifiedNameWithoutLeerstellen));
        // appendNames(res, gefuellteArgumente);

        res.append("Mit");
        if (leerstellen.size() == 1) {
            res.append("Einer");
        }
        appendNames(res, leerstellen);
        res.append(leerstellen.size() == 1 ? "Leerstelle" : "Leerstellen");

        return res.toString();
    }

    private static String buildBaseName(final String qualifiedNameWithoutLeerstellen) {
        final int startOfSimpleName = qualifiedNameWithoutLeerstellen.lastIndexOf(".");
        final String simpleNameWithoutLeerstellen =
                qualifiedNameWithoutLeerstellen.substring(startOfSimpleName + 1);

        if (simpleNameWithoutLeerstellen.endsWith("OhneLeerstellen")) {
            return simpleNameWithoutLeerstellen.substring(0,
                    simpleNameWithoutLeerstellen.length() - "OhneLeerstellen".length());
        }

        return simpleNameWithoutLeerstellen;
    }

    private static void appendNames(final StringBuilder res,
                                    final Collection<ArgumentField> leerstellen) {
        for (final ArgumentField argument : leerstellen) {
            res.append(capitalize(argument.getName()));
        }
    }

    private static String capitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public String toString() {
        return "ValenzClassToBeGenerated{" +
                "packageName='" + packageName + '\'' +
                ", qualifiedNameWithoutLeerstellen='" + qualifiedNameWithoutLeerstellen + '\'' +
                ", alleArgumente=" + alleArgumente +
                ", gefuellteArgumente=" + gefuellteArgumente +
                ", leerstellen=" + leerstellen +
                ", sonstigeFelder=" + sonstigeFelder +
                '}';
    }
}
