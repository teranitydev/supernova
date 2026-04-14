package supernova.experimental;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes("*") // Scan all classes in the module
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class ExperimentalProcessor extends AbstractProcessor {

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        // 1. Pass the original wrapper to super
        super.init(processingEnv);

        // 2. Unwrap it specifically for the Trees API
        ProcessingEnvironment unwrapped = jbUnwrap(ProcessingEnvironment.class, processingEnv);
        this.trees = Trees.instance(unwrapped);
    }

    // This is the magic helper IntelliJ was asking for
    private static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
        } catch (Throwable ignored) {}
        return unwrapped != null ? unwrapped : wrapper;
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Iterate over every class being compiled
        for (Element rootElement : roundEnv.getRootElements()) {
            if (rootElement.getKind() == ElementKind.CLASS) {

                // If the class is ALREADY enabled or experimental itself, skip it
                if (rootElement.getAnnotation(EnableExperimental.class) != null ||
                        rootElement.getAnnotation(Experimental.class) != null) {
                    continue;
                }

                // Otherwise, scan the class for any mention of @Experimental types
                checkUsage(rootElement);
            }
        }
        return false;
    }

    private void checkUsage(Element element) {
        TreePath path = trees.getPath(element);
        Scanner scanner = new Scanner(element);
        scanner.scan(path, null);
    }

    private class Scanner extends TreePathScanner<Void, Void> {
        private final Element owner;

        Scanner(Element owner) { this.owner = owner; }

        @Override
        public Void visitIdentifier(IdentifierTree node, Void unused) {
            Element referred = trees.getElement(getCurrentPath());
            if (referred != null && referred.getAnnotation(Experimental.class) != null) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Class '" + owner.getSimpleName() + "' uses experimental API '" + referred.getSimpleName() +
                                "'. Annotate with @EnableExperimental to allow this.",
                        owner
                );
            }
            return super.visitIdentifier(node, unused);
        }
    }
}