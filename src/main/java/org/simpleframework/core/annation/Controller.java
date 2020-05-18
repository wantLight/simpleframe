package org.simpleframework.core.annation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xyzzg on 2020/4/20.
 */
// 作用在类上的
@Target(ElementType.TYPE)
// 运行时获得
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
