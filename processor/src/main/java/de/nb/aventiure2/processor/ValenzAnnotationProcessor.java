package de.nb.aventiure2.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SuppressWarnings("WeakerAccess")
public class ValenzAnnotationProcessor extends AbstractProcessor {
    // See https://stablekernel.com/article/the-10-step-guide-to-annotation-processing-in-android
    // -studio/

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Valenz.class.getCanonicalName());

        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        for (final Element annotatedElement : roundEnv.getElementsAnnotatedWith(Valenz.class)) {
            if (annotatedElement.getKind() != ElementKind.CONSTRUCTOR) {
                error(annotatedElement, "Only constructors can be annotated with @%s",
                        Valenz.class.getSimpleName());
                return true;
            }

            final ExecutableElement constructorElement = (ExecutableElement) annotatedElement;
            processAnnotatedConstructor(constructorElement);
        }

        return true;
    }

    private void processAnnotatedConstructor(final ExecutableElement constructorElement) {
        try {
            @Nullable final PackageElement pkg =
                    processingEnv.getElementUtils().getPackageOf(constructorElement);

            final Collection<ArgumentField> argumente = findArgumente(constructorElement);

            final ImmutableList<VariableElement> sonstigeFelder =
                    findSonstigeFelder(constructorElement, argumente);

            final TypeElement typeElement = (TypeElement) constructorElement.getEnclosingElement();

            final ValenzAnnotatedClass valenzAnnotatedClass =
                    new ValenzAnnotatedClass(
                            pkg != null ? pkg.getQualifiedName().toString() : "",
                            typeElement.getSimpleName().toString(),
                            argumente,
                            sonstigeFelder);

            final Collection<ValenzClassToBeGenerated> classesToGenerate =
                    valenzAnnotatedClass.getClassesToGenerate();

            generate(classesToGenerate);
        } catch (final RuntimeException e) {
            error(null, e.getMessage());
        }
    }

    private void generate(final Collection<ValenzClassToBeGenerated> classesToGenerate) {
        for (final ValenzClassToBeGenerated valenzClassToBeGenerated : classesToGenerate) {
            try {
                messager.printMessage(Diagnostic.Kind.NOTE,
                        "Generated: " + valenzClassToBeGenerated.toString());

                valenzClassToBeGenerated.generateCode(typeUtils, elementUtils, filer);
            } catch (final IOException e) {
                error(null, e.getMessage());
            }
        }
    }

    private ImmutableList<ArgumentField> findArgumente(
            final ExecutableElement constructorArgument) {
        final TypeElement typeElement = (TypeElement) constructorArgument.getEnclosingElement();

        final ImmutableList.Builder<ArgumentField> res = ImmutableList.builder();

        final Set<VariableElement> fields = ElementFilter.fieldsIn(
                Sets.newLinkedHashSet(elementUtils.getAllMembers(typeElement)));

        for (final VariableElement field : fields) {
            if (field.getAnnotation(Komplement.class) != null) {
                res.add(buildArgument(field));
            }
        }
        return res.build();
    }

    private static ImmutableList<VariableElement> findSonstigeFelder(
            final ExecutableElement constructorElement,
            final Collection<ArgumentField> argumente) {
        final ImmutableList.Builder<VariableElement> res = ImmutableList.builder();

        for (final VariableElement parameter : constructorElement.getParameters()) {
            if (argumente.stream().map(ArgumentField::getName)
                    .noneMatch(n -> n.equals(parameter.getSimpleName().toString()))) {
                res.add(parameter);
            }
        }

        return res.build();
    }

    private static ArgumentField buildArgument(final VariableElement field) {

        return new ArgumentField(
                field.asType().toString(), field.getSimpleName().toString());
    }

    private void error(final Element e, final String msg, final Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}