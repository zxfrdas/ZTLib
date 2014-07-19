package com.zt.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于指出配置类中变量和配置文件中字段的对应关系。即通过此Annotation指定变量在配置文件中对应的名称。
 * @author zhaotong
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetName {
	String value();
}
