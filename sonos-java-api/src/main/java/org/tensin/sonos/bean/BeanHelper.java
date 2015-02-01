package org.tensin.sonos.bean;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosRuntimeException;

import com.google.common.base.Objects;

/**
 * The Class BeanHelper.
 * This class allows to easily write .equals, .hashCode and .toString method on any bean, without writing any boilerplate code.
 *
 * You just have to annotate the various field with @BeanField annotation (without additionnals parameters : that field will be used for .equals, .hashCode and .toString). Optionnal parameters include / exclude allows to redefine where this
 * field will be used).
 *
 * Annotations can be applyed either on fields, either on getters.
 *
 * The .equals, .hashCode and .toString are then just one line (calling the static method of BeanHelper). A good practice is to defined those methods in an abstract upper class (AbstractConfiguration for example).
 *
 * The static buildBeanFieldCartography() method allows to dump a list of every bean and every annotated fields or getters (allows to review / compare beans on the same kind, like configurations beans).
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
 */
public final class BeanHelper {

    /**
     * The Class AccessibleObjectComparator.
     *
     * @author Serge SIMON
     * @version $Revision:1.00 $
     * @since 6 janv. 2015
     */
    private static final class AccessibleObjectComparator implements Comparator<AccessibleObject>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final AccessibleObject o1, final AccessibleObject o2) {
            final boolean o1IsField = o1 instanceof Field;
            final boolean o2IsField = o2 instanceof Field;

            if (!o1IsField && o2IsField) {
                return 1;
            } else if (o1IsField && !o2IsField) {
                return -1;
            }

            if (o1IsField) {
                return ((Field) o1).getName().compareTo(((Field) o2).getName());
            }
            if ((o1 instanceof java.lang.reflect.Method) && (o2 instanceof java.lang.reflect.Method)) {
                return ((java.lang.reflect.Method) o1).getName().compareTo(((java.lang.reflect.Method) o2).getName());
            }
            return -1;
        }
    }

    /**
     * The Class BeanFieldDifference.
     *
     * @author Serge SIMON
     * @version $Revision:1.00 $
     * @since 6 janv. 2015
     */
    public static final class BeanFieldDifference {

        /** The key. */
        @BeanField
        private String key;

        /** The value. */
        @BeanField
        private String value;

        /**
         * Instantiates a new bean field difference.
         *
         * @param key
         *            the key
         * @param value
         *            the value
         */
        public BeanFieldDifference(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            return BeanHelper.equals(this, obj);
        }

        /**
         * Gets the key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return BeanHelper.hashCode(this);
        }

        /**
         * Sets the key.
         *
         * @param key
         *            the new key
         */
        public void setKey(final String key) {
            this.key = key;
        }

        /**
         * Sets the value.
         *
         * @param value
         *            the new value
         */
        public void setValue(final String value) {
            this.value = value;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return BeanHelper.toString(this);
        }
    }

    /**
     * The Interface IToStringHelper.
     *
     * @author Serge SIMON
     * @version $Revision:1.00 $
     * @since 6 janv. 2015
     */
    public interface IToStringHelper {

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper add(String name, boolean value);

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper add(String name, char value);

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper add(String name, double value);

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper add(String name, float value);

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper add(String name, int value);

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper add(String name, long value);

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format. If {@code value} is {@code null}, the string {@code "null"} is used, unless {@link #omitNullValues()} is called, in which case this
         * name/value pair will not be added.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         */
        IToStringHelper add(String name, Object value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, boolean)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper addValue(boolean value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, char)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper addValue(char value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, double)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper addValue(double value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, float)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper addValue(float value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, int)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper addValue(int value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, long)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         * @since 11.0 (source-compatible since 2.0)
         */
        IToStringHelper addValue(long value);

        /**
         * Adds an unnamed value to the formatted output.
         *
         * <p>
         * It is strongly encouraged to use {@link #add(String, Object)} instead and give value a readable name.
         *
         * @param value
         *            the value
         * @return the to string helper
         */
        IToStringHelper addValue(Object value);

        /**
         * Configures the {@link ToStringHelper} so {@link #toString()} will ignore
         * properties with null value. The order of calling this method, relative
         * to the {@code add()}/{@code addValue()} methods, is not significant.
         *
         * @return the to string helper
         * @since 12.0
         */
        IToStringHelper omitNullValues();

        /**
         * Returns a string in the format specified by {@link Objects#toStringHelper(Object)}.
         *
         * <p>
         * After calling this method, you can keep adding more properties to later call toString() again and get a more complete representation of the same object; but properties cannot be removed, so this only allows limited reuse of the
         * helper instance. The helper allows duplication of properties (multiple name/value pairs with the same name can be added).
         *
         * @return the string
         */
        @Override
        String toString();

    }

    /**
     * The Class ToStringHelper.
     * Copied/pasted from Guava to alter toString() formatting.
     *
     * @author Serge SIMON
     * @version $Revision:1.00 $
     * @since 6 janv. 2015
     */
    public static class ToStringHelper implements IToStringHelper {

        /**
         * The Class ValueHolder.
         *
         * @author Serge SIMON
         * @version $Revision:1.00 $
         * @since 6 janv. 2015
         */
        protected static final class ValueHolder {

            /** The name. */
            String name;

            /** The value. */
            Object value;

            /** The next. */
            ValueHolder next;

            /**
             * Name.
             *
             * @return the string
             */
            public String name() {
                return name;
            }

            /**
             * Next.
             *
             * @return the value holder
             */
            public ValueHolder next() {
                return next;
            }

            /**
             * Value.
             *
             * @return the object
             */
            public Object value() {
                return value;
            }

        }

        /**
         * Builds the.
         *
         * @param self
         *            the self
         * @return the to string helper
         */
        public static IToStringHelper build(final Object self) {
            return new ToStringHelper(simpleName(self.getClass()));
        }

        /**
         * {@link Class#getSimpleName()} is not GWT compatible yet, so we
         * provide our own implementation.
         *
         * @param clazz
         *            the clazz
         * @return the string
         */
        protected static String simpleName(final Class<?> clazz) {
            String name = clazz.getName();

            // the nth anonymous class has a class name ending in "Outer$n"
            // and local inner classes have names ending in "Outer.$1Inner"
            name = name.replaceAll("\\$[0-9]+", "\\$");

            // we want the name of the inner class all by its lonesome
            int start = name.lastIndexOf('$');

            // if this isn't an inner class, just find the start of the
            // top level class name.
            if (start == -1) {
                start = name.lastIndexOf('.');
            }
            return name.substring(start + 1);
        }

        /** The class name. */
        protected final String className;

        /** The holder head. */
        protected final ValueHolder holderHead = new ValueHolder();

        /** The holder tail. */
        protected ValueHolder holderTail = holderHead;

        /** The omit null values. */
        protected boolean omitNullValues;

        /**
         * Use {@link Objects#toStringHelper(Object)} to create an instance.
         *
         * @param className
         *            the class name
         */
        protected ToStringHelper(final String className) {
            this.className = checkNotNull(className);
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, boolean)
         */
        @Override
        public IToStringHelper add(final String name, final boolean value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, char)
         */
        @Override
        public IToStringHelper add(final String name, final char value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, double)
         */
        @Override
        public IToStringHelper add(final String name, final double value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, float)
         */
        @Override
        public IToStringHelper add(final String name, final float value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, int)
         */
        @Override
        public IToStringHelper add(final String name, final int value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, long)
         */
        @Override
        public IToStringHelper add(final String name, final long value) {
            return addHolder(name, String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#add(java.lang.String, java.lang.Object)
         */
        @Override
        public IToStringHelper add(final String name, final Object value) {
            return addHolder(name, value);
        }

        /**
         * Adds the holder.
         *
         * @return the value holder
         */
        private ValueHolder addHolder() {
            ValueHolder valueHolder = new ValueHolder();
            holderTail = holderTail.next = valueHolder;
            return valueHolder;
        }

        /**
         * Adds the holder.
         *
         * @param value
         *            the value
         * @return the to string helper
         */
        private IToStringHelper addHolder(final Object value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = value;
            return this;
        }

        /**
         * Adds the holder.
         *
         * @param name
         *            the name
         * @param value
         *            the value
         * @return the to string helper
         */
        private IToStringHelper addHolder(final String name, final Object value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = value;
            valueHolder.name = checkNotNull(name);
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(boolean)
         */
        @Override
        public IToStringHelper addValue(final boolean value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(char)
         */
        @Override
        public IToStringHelper addValue(final char value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(double)
         */
        @Override
        public IToStringHelper addValue(final double value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(float)
         */
        @Override
        public IToStringHelper addValue(final float value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(int)
         */
        @Override
        public IToStringHelper addValue(final int value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(long)
         */
        @Override
        public IToStringHelper addValue(final long value) {
            return addHolder(String.valueOf(value));
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#addValue(java.lang.Object)
         */
        @Override
        public IToStringHelper addValue(final Object value) {
            return addHolder(value);
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#omitNullValues()
         */
        @Override
        public IToStringHelper omitNullValues() {
            omitNullValues = true;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @see com.inetpsa.slb.bean.IToStringHelper#toString()
         */
        @Override
        public String toString() {
            // create a copy to keep it consistent in case value changes
            boolean omitNullValuesSnapshot = omitNullValues;
            final StringBuilder builder = new StringBuilder(32).append(className).append(" : ");
            String nextSeparator = "";
            for (ValueHolder valueHolder = holderHead.next; valueHolder != null; valueHolder = valueHolder.next) {
                if (!omitNullValuesSnapshot || (valueHolder.value != null)) {
                    builder.append(nextSeparator);
                    nextSeparator = ", ";

                    if (valueHolder.name != null) {
                        builder.append(valueHolder.name);
                    }
                    builder.append(" [").append(valueHolder.value).append("]");
                }
            }
            return builder.toString();
        }
    }

    /**
     * Builds the field name.
     *
     * @param ao
     *            the ao
     * @return the string
     */
    private static String buildFieldName(final Field ao) {
        final Attribute attributeAnnotation = ao.getAnnotation(Attribute.class);
        if (attributeAnnotation != null) {
            if (StringUtils.isNotEmpty(attributeAnnotation.name())) {
                return attributeAnnotation.name();
            }
        }
        final Element elementAnnotation = ao.getAnnotation(Element.class);
        if (elementAnnotation != null) {
            if (StringUtils.isNotEmpty(elementAnnotation.name())) {
                return elementAnnotation.name();
            }
        }
        return ao.getName();
    }

    /**
     * Compare to.
     *
     * @param obj1
     *            the obj1
     * @param obj2
     *            the obj2
     * @return the int
     */
    public static int compareTo(final Object obj1, final Object obj2) {
        final CompareToBuilder result = new CompareToBuilder();

        for (final AccessibleObject ao : getAccessibleObjects(BeanField.class, obj1.getClass(), CONSTANT_FILTER_EQUALS)) {
            try {
                if (ao instanceof Field) {
                    // builder.append(invoke((Field) ao, obj1), invoke((Field) ao, obj2));
                    result.append(invoke((Field) ao, obj1), invoke((Field) ao, obj2));
                } else if (ao instanceof java.lang.reflect.Method) {
                    final java.lang.reflect.Method m = (java.lang.reflect.Method) ao;
                    // builder.append(invoke(m, obj1), invoke(m, obj2));
                    result.append(invoke(m, obj1), invoke(m, obj2));
                }
            } catch (IllegalArgumentException e) {
                throw new SonosRuntimeException(e);
            } catch (SonosException e) {
                throw new SonosRuntimeException(e);
            }
        }

        return result.toComparison();
    }

    /**
     * Copy.
     *
     * @param source
     *            the source
     * @param destination
     *            the destination
     * @return the object
     * @throws SonosException
     *             the core exception
     */
    public static Object copy(final Object source, final Object destination) throws SonosException {
        if (source == null) {
            throw new IllegalArgumentException("Source object can't be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination object can't be null");
        }
        for (final AccessibleObject ao : getAccessibleObjects(BeanField.class, source.getClass(), CONSTANT_FILTER_COPY)) {
            try {
                if (ao instanceof Field) {
                    final Object sourceValue = invoke(((Field) ao), source);
                    ((Field) ao).set(destination, sourceValue);
                    // } else if (ao instanceof java.lang.reflect.Method) {
                    // final java.lang.reflect.Method m = (java.lang.reflect.Method) ao;
                    // final Object sourceValue = invoke(m, obj1);
                    // builder.append(invoke(m, obj1), invoke(m, obj2));
                }
            } catch (IllegalArgumentException e) {
                throw new SonosRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new SonosRuntimeException(e);
            }
        }
        return destination;
    }

    /**
     * Equals.
     *
     * @param obj1
     *            the obj1
     * @param obj2
     *            the obj2
     * @return true, if equals
     */
    public static boolean equals(final Object obj1, final Object obj2) {
        if (obj1 == obj2) {
            return true;
        }

        if ((obj2 == null) || (obj2.getClass() != obj1.getClass())) {
            return false;
        }

        final EqualsBuilder builder = new EqualsBuilder();

        for (final AccessibleObject ao : getAccessibleObjects(BeanField.class, obj1.getClass(), CONSTANT_FILTER_EQUALS)) {
            try {
                if (ao instanceof Field) {
                    builder.append(invoke((Field) ao, obj1), invoke((Field) ao, obj2));
                } else if (ao instanceof java.lang.reflect.Method) {
                    final java.lang.reflect.Method m = (java.lang.reflect.Method) ao;
                    builder.append(invoke(m, obj1), invoke(m, obj2));
                }
            } catch (IllegalArgumentException e) {
                throw new SonosRuntimeException(e);
            } catch (SonosException e) {
                throw new SonosRuntimeException(e);
            }
        }

        return builder.isEquals();
    }

    /**
     * Filter.
     *
     * @param field
     *            the field
     * @return the int
     */
    private static int filter(final Annotation field) {
        int filter = 0;

        final Method[] includes = ((BeanField) field).include();
        for (final Method method : includes) {
            switch (method) {
            case ALL:
                filter = filter | CONSTANT_FILTER_ALL;
                break;
            case EQUALS:
                filter = filter | CONSTANT_FILTER_EQUALS;
                break;
            case HASH_CODE:
                filter = filter | CONSTANT_FILTER_HASH_CODE;
                break;
            case TO_STRING:
                filter = filter | CONSTANT_FILTER_TO_STRING;
                break;
            case COPY:
                filter = filter | CONSTANT_FILTER_COPY;
                break;
            case NONE:
                break;
            default:
                break;
            }
        }

        final Method[] excludes = ((BeanField) field).exclude();
        for (final Method method : excludes) {
            switch (method) {
            case ALL:
                filter -= filter & CONSTANT_FILTER_ALL;
                break;
            case EQUALS:
                filter -= filter & CONSTANT_FILTER_EQUALS;
                break;
            case HASH_CODE:
                filter -= filter & CONSTANT_FILTER_HASH_CODE;
                break;
            case COPY:
                filter -= filter & CONSTANT_FILTER_COPY;
                break;
            case TO_STRING:
                filter -= filter & CONSTANT_FILTER_TO_STRING;
                break;
            case NONE:
                break;
            default:
                break;
            }
        }
        return filter;

    }

    /**
     * Gets the accessible objects.
     *
     * @param annotationClass
     *            the annotation class
     * @param obj1
     *            the obj1
     * @param filter
     *            the filter
     * @return the accessible objects
     */
    public static Collection<AccessibleObject> getAccessibleObjects(final Class annotationClass, final Class<?> baseClass, final int filter) {
        Class<?> clazz = baseClass;
        final String name = clazz.getName() + filter + annotationClass.getName();
        Collection<AccessibleObject> result = cache.get(name);
        if (result == null) {
            final List<AccessibleObject> aos = new ArrayList<AccessibleObject>();

            do {
                final Field[] fields = clazz.getDeclaredFields();

                for (final Field field : fields) {
                    final Annotation bk = field.getAnnotation(annotationClass);
                    if ((bk != null) && ((filter(bk) & filter) == filter)) {
                        field.setAccessible(true);
                        aos.add(field);
                    }
                }

                final java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();

                for (final java.lang.reflect.Method method : methods) {
                    final Annotation bk = method.getAnnotation(annotationClass);
                    if ((bk != null) && ((filter(bk) & filter) == filter)) {
                        method.setAccessible(true);
                        aos.add(method);
                    }
                }

                clazz = clazz.getSuperclass();
            } while (clazz != null);

            Collections.sort(aos, new AccessibleObjectComparator());

            result = cache.putIfAbsent(name, aos);
            if (result == null) {
                result = aos;
            }
        }

        return result;
    }

    /**
     * Hash code.
     *
     * @param obj
     *            the obj
     * @return the int
     */
    public static int hashCode(final Object obj) {
        final HashCodeBuilder builder = new HashCodeBuilder();

        for (final AccessibleObject ao : getAccessibleObjects(BeanField.class, obj.getClass(), CONSTANT_FILTER_HASH_CODE)) {
            try {
                if (ao instanceof Field) {
                    builder.append(invoke((Field) ao, obj));
                } else if (ao instanceof java.lang.reflect.Method) {
                    builder.append(invoke((java.lang.reflect.Method) ao, obj));
                }
            } catch (SonosException e) {
                throw new SonosRuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new SonosRuntimeException(e);
            }
        }

        return builder.toHashCode();
    }

    /**
     * Invoke.
     *
     * @param f
     *            the f
     * @param object
     *            the object
     * @return the object
     * @throws SonosException
     *             the core exception
     */
    public static Object invoke(final Field f, final Object object) throws SonosException {
        try {
            return f.get(object);
        } catch (IllegalArgumentException e) {
            throw new SonosException(e);
        } catch (IllegalAccessException e) {
            throw new SonosException(e);
        }
    }

    /**
     * Invoke.
     *
     * @param m
     *            the m
     * @param object
     *            the object
     * @return the object
     * @throws SonosException
     *             the core exception
     */
    public static Object invoke(final java.lang.reflect.Method m, final Object object) throws SonosException {
        try {
            return m.invoke(object, (Object[]) null);
        } catch (IllegalAccessException e) {
            throw new SonosException(e);
        } catch (IllegalArgumentException e) {
            throw new SonosException(e);
        } catch (InvocationTargetException e) {
            throw new SonosException(e);
        }
    }

    /**
     * To string.
     *
     * @param obj
     *            the obj
     * @return the string
     */
    public static String toString(final Object obj) {
        return toString(obj, ToStringHelper.build(obj).omitNullValues());
    }

    /**
     * To string.
     *
     * @param obj
     *            the obj
     * @param toStringHelper
     *            the to string helper
     * @return the string
     */
    public static String toString(final Object obj, final IToStringHelper toStringHelper) {
        for (final AccessibleObject ao : getAccessibleObjects(BeanField.class, obj.getClass(), CONSTANT_FILTER_TO_STRING)) {
            try {
                if (ao instanceof Field) {
                    toStringHelper.add(buildFieldName((Field) ao), invoke((Field) ao, obj));
                } else {
                    toStringHelper.add(((java.lang.reflect.Method) ao).getName(), invoke((java.lang.reflect.Method) ao, obj));
                }
            } catch (final SonosException e) {
                throw new SonosRuntimeException(e);
            }
        }

        return toStringHelper.toString();
    }

    /** The Constant CONSTANT_FILTER_EQUALS. */
    public static final int CONSTANT_FILTER_EQUALS = 1;

    /** The Constant CONSTANT_FILTER_HASH_CODE. */
    public static final int CONSTANT_FILTER_HASH_CODE = 2;

    /** The Constant CONSTANT_FILTER_TO_STRING. */
    public static final int CONSTANT_FILTER_TO_STRING = 4;

    /** The Constant CONSTANT_FILTER_EQUALS. */
    public static final int CONSTANT_FILTER_COPY = 8;

    /** The Constant CONSTANT_FILTER_ALL. */
    public static final int CONSTANT_FILTER_ALL = 15;

    /** The cache. */
    private static ConcurrentMap<String, List<AccessibleObject>> cache = new ConcurrentHashMap<String, List<AccessibleObject>>();

    /**
     * The Constructor.
     */
    private BeanHelper() {

    }
}
