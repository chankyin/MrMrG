package me.noip.chankyin.mrmrg.utils.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Warning: Should be used with {@link lombok.NoArgsConstructor}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SavedObject{
	short value();
}
