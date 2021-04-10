package com.sadna.app.ws.MySCRUM;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *  Spring Application context class for Bean management
 */
public class SpringApplicationContext implements ApplicationContextAware {
    private static  ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }

    /**
     * get a instance of a class
     * @param beanName class name
     * @return instance of class
     */
    public static Object getBean(String beanName){
        return CONTEXT.getBean(beanName);

    }


}
