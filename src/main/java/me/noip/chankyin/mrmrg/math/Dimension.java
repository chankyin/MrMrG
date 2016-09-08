package me.noip.chankyin.mrmrg.math;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({LOCAL_VARIABLE, FIELD, METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dimension{
	public int value();
}
