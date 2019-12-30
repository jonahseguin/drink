package com.jonahseguin.drink.parametric;

import com.google.common.base.Preconditions;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Set;

@Getter
public class DrinkBinding<T> {

    private final Class<T> type;
    private final Set<Class<? extends Annotation>> annotations;
    private final DrinkProvider<T> provider;

    public DrinkBinding(Class<T> type, Set<Class<? extends Annotation>> annotations, DrinkProvider<T> provider) {
        this.type = type;
        this.annotations = annotations;
        this.provider = provider;
    }

    public boolean canProvideFor(@Nonnull CommandParameter parameter) {
        Preconditions.checkNotNull(parameter, "Parameter cannot be null");
        // The parameter and binding need to have exact same annotations
        parameter.getClassifierAnnotations().forEach(c -> System.out.println("Classifier annotation present for param " + parameter.getParameter().getName() + ": " + c.annotationType().getSimpleName()));
        for (Annotation c : parameter.getClassifierAnnotations()) {
            System.out.println("Annotation present for param " + parameter.getParameter().getName() + ": " + c.annotationType().getSimpleName());
            if (!annotations.contains(c.annotationType())) {
                return false;
            }
        }
        for (Class<? extends Annotation> annotation : annotations) {
            System.out.println("canProvideFor provider " + provider.getClass().getSimpleName() + " required classifier: " + annotation.getSimpleName() + " for param: " + parameter.getParameter().getName());
            if (parameter.getClassifierAnnotations().stream().noneMatch(a -> a.annotationType().equals(annotation))) {
                return false;
            }
        }
        return true;
    }

}
