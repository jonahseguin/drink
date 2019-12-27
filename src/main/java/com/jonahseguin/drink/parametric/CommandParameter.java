package com.jonahseguin.drink.parametric;

import com.jonahseguin.drink.annotation.Classifier;
import com.jonahseguin.drink.annotation.Modifier;
import com.jonahseguin.drink.modifier.annotation.Optional;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandParameter {

    private final Class<?> type;
    private final Parameter parameter;
    private final Annotation[] allAnnotations;
    private final Annotation[] classifierAnnotations;
    private final Annotation[] modifierAnnotations;
    private final Annotation[] otherAnnotations;

    public CommandParameter(Class<?> type, Parameter parameter, Annotation[] allAnnotations) {
        this.type = type;
        this.parameter = parameter;
        this.allAnnotations = allAnnotations;
        this.classifierAnnotations = loadClassifiers();
        this.modifierAnnotations = loadModifiers();
        this.otherAnnotations = loadOtherAnnotations();
    }

    public boolean isOptional() {
        return parameter.isAnnotationPresent(Optional.class);
    }

    public String getDefaultOptionalValue() {
        return parameter.getAnnotation(Optional.class).value();
    }

    private Annotation[] loadClassifiers() {
        List<Annotation> classifiers = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(Classifier.class)) {
                classifiers.add(annotation);
            }
        }
        Annotation[] classifierAnnotations = new Annotation[classifiers.size()];
        for (int i = 0; i < classifiers.size(); i++) {
            classifierAnnotations[i] = classifiers.get(i);
        }
        return classifierAnnotations;
    }

    private Annotation[] loadModifiers() {
        List<Annotation> modifiers = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(Modifier.class)) {
                modifiers.add(annotation);
            }
        }
        Annotation[] modifierAnnotations = new Annotation[modifiers.size()];
        for (int i = 0; i < modifiers.size(); i++) {
            modifierAnnotations[i] = modifiers.get(i);
        }
        return modifierAnnotations;
    }

    private Annotation[] loadOtherAnnotations() {
        List<Annotation> others = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (!annotation.annotationType().isAnnotationPresent(Classifier.class)) {
                others.add(annotation);
            }
        }
        Annotation[] otherAnnotations = new Annotation[others.size()];
        for (int i = 0; i < others.size(); i++) {
            otherAnnotations[i] = others.get(i);
        }
        return otherAnnotations;
    }

}
