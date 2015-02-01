package org.tensin.sonos.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface BeanField.
 * 
 * Methods or fields annotated with @BeanField will then be automatically included in .hashCode, .equals and .toString() methods (done through BeanHelper.equals()).
 * 
 * Example :
 * 
 * <pre>
 * private static final class StupidBean {
 * 
 *     &#064;BeanField
 *     private String name;
 * 
 *     &#064;BeanField
 *     private int value;
 * 
 *     &#064;BeanField(include = Method.TO_STRING)
 *     private String onlyOnToString;
 * 
 *     &#064;BeanField(exclude = Method.EQUALS)
 *     private String everywhereExceptEquals;
 * 
 *     private String dontCare;
 * 
 *     &#064;Override
 *     public boolean equals(final Object obj) {
 *         return BeanHelper.equals(this, obj);
 *     }
 * 
 *     &#064;Override
 *     public int hashCode() {
 *         return BeanHelper.hashCode(this);
 *     }
 * 
 *     &#064;Override
 *     public String toString() {
 *         return BeanHelper.toString(this);
 *     }
 * }
 * </pre>
 *
 * @author Serge SIMON
 * @since 17 juin 2014
 * @See com.inetpsa.slb.bean.BeanHelper.class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface BeanField {

    /**
     * Exclude.
     *
     * @return the method[]
     */
    Method[] exclude() default Method.NONE;

    /**
     * Include.
     *
     * @return the method[]
     */
    Method[] include() default Method.ALL;
}