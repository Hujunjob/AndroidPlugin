package com.hiscene.drone.gps;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hujun on 2019/5/8.
 * 该方法被测试通过
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface Tested {
}
