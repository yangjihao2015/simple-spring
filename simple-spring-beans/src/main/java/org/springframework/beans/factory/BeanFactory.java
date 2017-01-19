/*
 * Copyright 2002-2015 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;

/**
 * 用于访问spring bean容器的根接口.
 * 这是bean容器的基本客户端视图;
 * 接下来的接口，　比如ListableBeanFactory和org.springframework.beans.factory.config.ConfigurableBeanFactory
 * 都有被用于特定的目的.
 *
 * <p>实现该接口的对象都拥有多个bean definitions, 每个都通过字符串类型的名字唯一标识．
 * 根据bean definition, 该工厂将会返回一个独立的实例（原型设计模式），　
 * 或者返回一个共享的单例实例（是单例设计模式的一个更好的替代方案，　其中实例是工厂范围内的单例）.
 * 返回哪种类型的实例依赖于bean factory的配置：　api是相同的．
 * 从spring2.0以后，　取决于具体的应用上下文，更多的scope是可用的（比如：　在web环境下的request和session）
 *
 * <p>这种方法的要点是：　BeanFactory是应用组件的中央注册器，　并集中应用组建的配置
 *
 * <p>注意：　依赖注入通常是一个更好的方式去配置应用对象，　它是通过set或者constructor而不是通过beanFactory查找．
 * spring的依赖注入功能是使用beanFactory接口和它的子接口实现的．
 *
 * <p>通常BeanFactory将加载存储在配置源（比如xml文档）中的bean definition,
 * 并且使用org.springframework.beans包配置beans
 * 然而，也可以直接在Java代码中直接返回它创建的Java对象
 * 如何存储bean definition没有约束: LDAP, RDBMS, XML,　properties文件等都行．
 * 鼓励在beans中支持引用（依赖注入）的实现方式
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * as far as possible. The full set of initialization methods and their standard order is:<br>
 * 1. BeanNameAware's {@code setBeanName}<br>
 * 2. BeanClassLoaderAware's {@code setBeanClassLoader}<br>
 * 3. BeanFactoryAware's {@code setBeanFactory}<br>
 * 4. ResourceLoaderAware's {@code setResourceLoader}
 * (only applicable when running in an application context)<br>
 * 5. ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)<br>
 * 6. MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)<br>
 * 7. ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)<br>
 * 8. ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)<br>
 * 9. {@code postProcessBeforeInitialization} methods of BeanPostProcessors<br>
 * 10. InitializingBean's {@code afterPropertiesSet}<br>
 * 11. a custom init-method definition<br>
 * 12. {@code postProcessAfterInitialization} methods of BeanPostProcessors
 *
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:<br>
 * 1. DisposableBean's {@code destroy}<br>
 * 2. a custom destroy-method definition
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

    /**
     * Used to dereference a {@link FactoryBean} instance and distinguish it from
     * beans <i>created</i> by the FactoryBean. For example, if the bean named
     * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
     * will return the factory, not the instance returned by the factory.
     */
    String FACTORY_BEAN_PREFIX = "&";


    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>This method allows a Spring BeanFactory to be used as a replacement for the
     * Singleton or Prototype design pattern. Callers may retain references to
     * returned objects in the case of Singleton beans.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to retrieve
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no bean definition
     * with the specified name
     * @throws BeansException if the bean could not be obtained
     */
    Object getBean(String name) throws BeansException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
     * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
     * required type. This means that ClassCastException can't be thrown on casting
     * the result correctly, as can happen with {@link #getBean(String)}.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to retrieve
     * @param requiredType type the bean must match. Can be an interface or superclass
     * of the actual class, or {@code null} for any match. For example, if the value
     * is {@code Object.class}, this method will succeed whatever the class of the
     * returned instance.
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
     * @throws BeansException if the bean could not be created
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * {@code null} is disallowed.
     * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
     * but may also be translated into a conventional by-name lookup based on the name
     * of the given type. For more extensive retrieval operations across sets of beans,
     * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
     * @return an instance of the single bean matching the required type
     * @throws NoSuchBeanDefinitionException if no bean of the given type was found
     * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
     * @throws BeansException if the bean could not be created
     * @since 3.0
     * @see ListableBeanFactory
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>Allows for specifying explicit constructor arguments / factory method arguments,
     * overriding the specified default arguments (if any) in the bean definition.
     * @param name the name of the bean to retrieve
     * @param args arguments to use when creating a bean instance using explicit arguments
     * (only applied when creating a new instance as opposed to retrieving an existing one)
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     * @throws BeanDefinitionStoreException if arguments have been given but
     * the affected bean isn't a prototype
     * @throws BeansException if the bean could not be created
     * @since 2.5
     */
    Object getBean(String name, Object... args) throws BeansException;

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     * <p>Allows for specifying explicit constructor arguments / factory method arguments,
     * overriding the specified default arguments (if any) in the bean definition.
     * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
     * but may also be translated into a conventional by-name lookup based on the name
     * of the given type. For more extensive retrieval operations across sets of beans,
     * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * {@code null} is disallowed.
     * @param args arguments to use when creating a bean instance using explicit arguments
     * (only applied when creating a new instance as opposed to retrieving an existing one)
     * @return an instance of the bean
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     * @throws BeanDefinitionStoreException if arguments have been given but
     * the affected bean isn't a prototype
     * @throws BeansException if the bean could not be created
     * @since 4.1
     */
    <T> T getBean(Class<T> requiredType, Object... args) throws BeansException;


    /**
     * Does this bean factory contain a bean definition or externally registered singleton
     * instance with the given name?
     * <p>If the given name is an alias, it will be translated back to the corresponding
     * canonical bean name.
     * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
     * be found in this factory instance.
     * <p>If a bean definition or singleton instance matching the given name is found,
     * this method will return {@code true} whether the named bean definition is concrete
     * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
     * return value from this method does not necessarily indicate that {@link #getBean}
     * will be able to obtain an instance for the same name.
     * @param name the name of the bean to query
     * @return whether a bean with the given name is present
     */
    boolean containsBean(String name);

    /**
     * Is this bean a shared singleton? That is, will {@link #getBean} always
     * return the same instance?
     * <p>Note: This method returning {@code false} does not clearly indicate
     * independent instances. It indicates non-singleton instances, which may correspond
     * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
     * check for independent instances.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return whether this bean corresponds to a singleton instance
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @see #getBean
     * @see #isPrototype
     */
    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

    /**
     * Is this bean a prototype? That is, will {@link #getBean} always return
     * independent instances?
     * <p>Note: This method returning {@code false} does not clearly indicate
     * a singleton object. It indicates non-independent instances, which may correspond
     * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
     * check for a shared singleton instance.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return whether this bean will always deliver independent instances
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.0.3
     * @see #getBean
     * @see #isSingleton
     */
    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    /**
     * Check whether the bean with the given name matches the specified type.
     * More specifically, check whether a {@link #getBean} call for the given name
     * would return an object that is assignable to the specified target type.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @param typeToMatch the type to match against (as a {@code ResolvableType})
     * @return {@code true} if the bean type matches,
     * {@code false} if it doesn't match or cannot be determined yet
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 4.2
     * @see #getBean
     * @see #getType
     */
    boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

    /**
     * Check whether the bean with the given name matches the specified type.
     * More specifically, check whether a {@link #getBean} call for the given name
     * would return an object that is assignable to the specified target type.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @param typeToMatch the type to match against (as a {@code Class})
     * @return {@code true} if the bean type matches,
     * {@code false} if it doesn't match or cannot be determined yet
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.0.1
     * @see #getBean
     * @see #getType
     */
    boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

    /**
     * Determine the type of the bean with the given name. More specifically,
     * determine the type of object that {@link #getBean} would return for the given name.
     * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
     * as exposed by {@link FactoryBean#getObjectType()}.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return the type of the bean, or {@code null} if not determinable
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 1.1.2
     * @see #getBean
     * @see #isTypeMatch
     */
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    /**
     * Return the aliases for the given bean name, if any.
     * All of those aliases point to the same bean when used in a {@link #getBean} call.
     * <p>If the given name is an alias, the corresponding original bean name
     * and other aliases (if any) will be returned, with the original bean name
     * being the first element in the array.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the bean name to check for aliases
     * @return the aliases, or an empty array if none
     * @see #getBean
     */
    String[] getAliases(String name);

}
