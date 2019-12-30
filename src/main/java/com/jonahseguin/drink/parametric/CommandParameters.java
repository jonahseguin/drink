package com.jonahseguin.drink.parametric;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Getter
public class CommandParameters {

    private final int count;
    private final CommandParameter[] parameters;

    public CommandParameters(int count, CommandParameter[] parameters) {
        this.count = count;
        this.parameters = parameters;
    }

    public CommandParameters(Method method) {
        System.out.println("CommandParameters init for method " + method.getName());
        CommandParameter[] parameters = new CommandParameter[method.getParameters().length];
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter param = method.getParameters()[i];
            System.out.println("CommandParameters " + method.getName() + " " + i + ": " + param.getType().getSimpleName() + " annotations: " + method.getParameterAnnotations()[i].length);
            parameters[i] = new CommandParameter(param.getType(), param, method.getParameterAnnotations()[i]);
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                System.out.println("CommandParameters " + method.getName() + " param " + param.getType().getSimpleName() + " annotation: " + annotation.annotationType().getSimpleName());
            }
        }
        this.parameters = parameters;
        this.count = method.getParameterCount();
    }
}
