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

package org.springframework.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *　通过委托ContextLoader和ContextCleanupListener启动和关闭spring的WebApplicationContext的引导侦听器
 *
 *　如果使用org.springframework.web.util.Log4jConfigListener，　在web.xml中，　该侦听器应该配置在Log4jConfigListener后面
 *
 *　从spring 3.1后，　ContextLoaderListener支持通过ContextLoaderListener(WebApplicationContext)构造方法注入WebApplicationContext
 *  在Servlet 3.0以上的环境中，　允许通过程序进行配置，　具体可参见org.springframework.web.WebApplicationInitializer
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 17.02.2003
 * @see #setContextInitializers
 * @see org.springframework.web.WebApplicationInitializer
 * @see org.springframework.web.util.Log4jConfigListener
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    /**
     * 创建一个新的ContextLoaderListener对象，　就会根据servlet的context-param中配置的contextClass和contextConfigLocation
     * 创建一个新的WebApplicationContext，　参见父类ContextLoader的文档查看具体的默认值
     *
     *　在web.xml配置ContextLoaderListener作为监听器时会使用该不带参数的构造方法
     *
     *　被创建的ApplicationContext将被注册到ServletContext中,
     *　对应的属性名是WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
     *　并且该ApplicationContext在监听器的contextDestroyed方法调用时被关闭
     * @see ContextLoader
     * @see #ContextLoaderListener(WebApplicationContext)
     * @see #contextInitialized(ServletContextEvent)
     * @see #contextDestroyed(ServletContextEvent)
     */
    public ContextLoaderListener() {
    }

    /**
     *　使用给定的ApplicationContext创建一个新的ContextLoaderListener，
     *　这个构造方法在Servlet 3.0以上环境是有用的，　通过javax.servlet.ServletContext#addListener可实现给予实例监听器注册
     *
     *　这个上下文可能被org.springframework.context.ConfigurableApplicationContext#refresh()　refreshed, 也可能没有．
     *　如果它（a）实现了ConfigurableWebApplicationContext, 并且（b）还没有被refreshed
     *　则会发生以下情况：
     * <li>如果给定的上下文还没有通过org.springframework.context.ConfigurableApplicationContext#setId分配一个id, 将为其分配</li>
     * <li>ServletContext和ServletConfig对象将被委托给ApplicationContext</li>
     * <li>customizeContext将会被调用</li>
     * <li>任何org.springframework.context.ApplicationContextInitializer都是通过init-param中配置contextInitializerClasses进行指定</li>
     * <li>org.springframework.context.ConfigurableApplicationContext#refresh将会被调用</li>
     *
     * 如果上下文已经被refreshed, 或者没有实现ConfigurableWebApplicationContext，
     * 假设用户根据他或她的特定需要执行了这些操作（或不执行），上述操作都不会发生
     *
     * 参见org.springframework.web.WebApplicationInitializer查看使用样例
     *
     *　无论在任何情况下，给定的ApplicationContext将被注册到ServletContext中,
     *　对应的属性名是WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
     *　并且该ApplicationContext在监听器的contextDestroyed方法调用时被关闭
     *
     * @param context the application context to manage
     * @see #contextInitialized(ServletContextEvent)
     * @see #contextDestroyed(ServletContextEvent)
     */
    public ContextLoaderListener(WebApplicationContext context) {
        super(context);
    }


    /**
     * 初始化根WebApplicationContext
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }


    /**
     * 关闭根WebApplicationContext
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        closeWebApplicationContext(event.getServletContext());
        ContextCleanupListener.cleanupAttributes(event.getServletContext());
    }

}
