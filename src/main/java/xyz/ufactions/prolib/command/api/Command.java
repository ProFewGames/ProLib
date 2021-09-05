package xyz.ufactions.prolib.command.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] aliases();

    int arguments() default 1;

    String description() default "";

    String usage() default "";

    String permission() default "";
}