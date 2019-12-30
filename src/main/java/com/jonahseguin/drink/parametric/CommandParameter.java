package com.jonahseguin.drink.parametric;

import com.google.common.collect.ImmutableList;
import com.jonahseguin.drink.annotation.Classifier;
import com.jonahseguin.drink.annotation.Modifier;
import com.jonahseguin.drink.modifier.annotation.Optional;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
public class CommandParameter {

    private final Class<?> type;
    private final Parameter parameter;
    private final List<Annotation> allAnnotations;
    private final List<Annotation> classifierAnnotations;
    private final List<Annotation> modifierAnnotations;

    public CommandParameter(Class<?> type, Parameter parameter, Annotation[] allAnnotations) {
        this.type = type;
        this.parameter = parameter;
        this.allAnnotations = ImmutableList.copyOf(allAnnotations);
        this.classifierAnnotations = loadClassifiers();
        this.modifierAnnotations = loadModifiers();
    }

    public boolean isOptional() {
        return parameter.isAnnotationPresent(Optional.class);
    }

    public String getDefaultOptionalValue() {
        return parameter.getAnnotation(Optional.class).value();
    }

    private List<Annotation> loadClassifiers() {
        List<Annotation> classifiers = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(Classifier.class)) {
                classifiers.add(annotation);
            }
        }
        return ImmutableList.copyOf(classifiers);
    }

    private List<Annotation> loadModifiers() {
        List<Annotation> modifiers = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(Modifier.class)) {
                modifiers.add(annotation);
            }
        }
        return ImmutableList.copyOf(modifiers);
    }

}
