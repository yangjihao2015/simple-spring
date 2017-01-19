/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;

/**
 * </p>扩展{@link BeanFactory}接口，　它可以枚举他的所有的bean实例，　而不是按照客户端的请求逐个按名称逐个查找
 * 预加载所有bean定义（例如基于XML的工厂）的BeanFactory实现可以实现此接口
 *
 * <p>如果这是一个{@link HierarchicalBeanFactory}，返回值将</ i>不会考虑任何BeanFactory层次结构，但只涉及当前工厂中定义的bean.
 * 使用{@link BeanFactoryUtils}帮助类来获取祖先工厂中的bean.
 *
 * <p>这个接口中的方法只会考虑该factory中的bean definition.
 * 忽略所有通过其他方式比如{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * {@code registerSingleton}方法注册的单例beans，
 * 除了{@code getBeanNamesOfType}和{@code getBeansOfType}，它将检查这样手动注册的单例.
 * Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 *
 * <p><b>NOTE:</b> With the exception of {@code getBeanDefinitionCount}
 * and {@code containsBeanDefinition}, the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * Check if this bean factory contains a bean definition with the given name.
     * <p>Does not consider any hierarchy this factory may participate in,
     * and ignores any singleton beans that have been registered by
     * other means than bean definitions.
     * @param beanName the name of the bean to look for
     * @return if this bean factory contains a bean definition with the given name
     * @see #containsBean
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * Return the number of beans defined in the factory.
     * <p>Does not consider any hierarchy this factory may participate in,
     * and ignores any singleton beans that have been registered by
     * other means than bean definitions.
     * @return the number of beans defined in the factory
     */
    int getBeanDefinitionCount();

    /**
     * Return the names of all beans defined in this factory.
     * <p>Does not consider any hierarchy this factory may participate in,
     * and ignores any singleton beans that have been registered by
     * other means than bean definitions.
     * @return the names of all beans defined in this factory,
     * or an empty array if none defined
     */
    String[] getBeanDefinitionNames();

    /**
     * Return the names of beans matching the given type (including subclasses),
     * judging from either bean definitions or the value of {@code getObjectType}
     * in the case of FactoryBeans.
     * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
     * check nested beans which might match the specified type as well.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>Does not consider any hierarchy this factory may participate in.
     * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
     * to include beans in ancestor factories too.
     * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
     * by other means than bean definitions.
     * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
     * be it singletons, prototypes, or FactoryBeans. In most implementations, the
     * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
     * <p>Bean names returned by this method should always return bean names <i>in the
     * order of definition</i> in the backend configuration, as far as possible.
     * @param type the class or interface to match, or {@code null} for all bean names
     * @return the names of beans (or objects created by FactoryBeans) matching
     * the given object type (including subclasses), or an empty array if none
     * @since 4.2
     * @see #isTypeMatch(String, ResolvableType)
     * @see FactoryBean#getObjectType
     * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
     */
    String[] getBeanNamesForType(ResolvableType type);

    /**
     * Return the names of beans matching the given type (including subclasses),
     * judging from either bean definitions or the value of {@code getObjectType}
     * in the case of FactoryBeans.
     * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
     * check nested beans which might match the specified type as well.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>Does not consider any hierarchy this factory may participate in.
     * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
     * to include beans in ancestor factories too.
     * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
     * by other means than bean definitions.
     * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
     * be it singletons, prototypes, or FactoryBeans. In most implementations, the
     * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
     * <p>Bean names returned by this method should always return bean names <i>in the
     * order of definition</i> in the backend configuration, as far as possible.
     * @param type the class or interface to match, or {@code null} for all bean names
     * @return the names of beans (or objects created by FactoryBeans) matching
     * the given object type (including subclasses), or an empty array if none
     * @see FactoryBean#getObjectType
     * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
     */
    String[] getBeanNamesForType(Class<?> type);

    /**
     * Return the names of beans matching the given type (including subclasses),
     * judging from either bean definitions or the value of {@code getObjectType}
     * in the case of FactoryBeans.
     * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
     * check nested beans which might match the specified type as well.
     * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
     * which means that FactoryBeans will get initialized. If the object created by the
     * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
     * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
     * (which doesn't require initialization of each FactoryBean).
     * <p>Does not consider any hierarchy this factory may participate in.
     * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
     * to include beans in ancestor factories too.
     * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
     * by other means than bean definitions.
     * <p>Bean names returned by this method should always return bean names <i>in the
     * order of definition</i> in the backend configuration, as far as possible.
     * @param type the class or interface to match, or {@code null} for all bean names
     * @param includeNonSingletons whether to include prototype or scoped beans too
     * or just singletons (also applies to FactoryBeans)
     * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
     * <i>objects created by FactoryBeans</i> (or by factory methods with a
     * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
     * eagerly initialized to determine their type: So be aware that passing in "true"
     * for this flag will initialize FactoryBeans and "factory-bean" references.
     * @return the names of beans (or objects created by FactoryBeans) matching
     * the given object type (including subclasses), or an empty array if none
     * @see FactoryBean#getObjectType
     * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
     */
    String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

    /**
     * Return the bean instances that match the given object type (including
     * subclasses), judging from either bean definitions or the value of
     * {@code getObjectType} in the case of FactoryBeans.
     * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
     * check nested beans which might match the specified type as well.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>Does not consider any hierarchy this factory may participate in.
     * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
     * to include beans in ancestor factories too.
     * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
     * by other means than bean definitions.
     * <p>This version of getBeansOfType matches all kinds of beans, be it
     * singletons, prototypes, or FactoryBeans. In most implementations, the
     * result will be the same as for {@code getBeansOfType(type, true, true)}.
     * <p>The Map returned by this method should always return bean names and
     * corresponding bean instances <i>in the order of definition</i> in the
     * backend configuration, as far as possible.
     * @param type the class or interface to match, or {@code null} for all concrete beans
     * @return a Map with the matching beans, containing the bean names as
     * keys and the corresponding bean instances as values
     * @throws BeansException if a bean could not be created
     * @since 1.1.2
     * @see FactoryBean#getObjectType
     * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * Return the bean instances that match the given object type (including
     * subclasses), judging from either bean definitions or the value of
     * {@code getObjectType} in the case of FactoryBeans.
     * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
     * check nested beans which might match the specified type as well.
     * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
     * which means that FactoryBeans will get initialized. If the object created by the
     * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
     * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
     * (which doesn't require initialization of each FactoryBean).
     * <p>Does not consider any hierarchy this factory may participate in.
     * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
     * to include beans in ancestor factories too.
     * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
     * by other means than bean definitions.
     * <p>The Map returned by this method should always return bean names and
     * corresponding bean instances <i>in the order of definition</i> in the
     * backend configuration, as far as possible.
     * @param type the class or interface to match, or {@code null} for all concrete beans
     * @param includeNonSingletons whether to include prototype or scoped beans too
     * or just singletons (also applies to FactoryBeans)
     * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
     * <i>objects created by FactoryBeans</i> (or by factory methods with a
     * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
     * eagerly initialized to determine their type: So be aware that passing in "true"
     * for this flag will initialize FactoryBeans and "factory-bean" references.
     * @return a Map with the matching beans, containing the bean names as
     * keys and the corresponding bean instances as values
     * @throws BeansException if a bean could not be created
     * @see FactoryBean#getObjectType
     * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
     */
    <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException;

    /**
     * Find all names of beans whose {@code Class} has the supplied {@link Annotation}
     * type, without creating any bean instances yet.
     * @param annotationType the type of annotation to look for
     * @return the names of all matching beans
     * @since 4.0
     */
    String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

    /**
     * Find all beans whose {@code Class} has the supplied {@link Annotation} type,
     * returning a Map of bean names with corresponding bean instances.
     * @param annotationType the type of annotation to look for
     * @return a Map with the matching beans, containing the bean names as
     * keys and the corresponding bean instances as values
     * @throws BeansException if a bean could not be created
     * @since 3.0
     */
    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

    /**
     * Find an {@link Annotation} of {@code annotationType} on the specified
     * bean, traversing its interfaces and super classes if no annotation can be
     * found on the given class itself.
     * @param beanName the name of the bean to look for annotations on
     * @param annotationType the annotation class to look for
     * @return the annotation of the given type if found, or {@code null}
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 3.0
     */
    <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException;

}
