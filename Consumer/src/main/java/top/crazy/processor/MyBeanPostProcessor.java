package top.crazy.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.crazy.anno.RpcReference;
import top.crazy.proxy.ProxyFactory;

import java.lang.reflect.Field;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.processor
 * @Author: crazyPang
 * @CreateTime: 2023-05-15  19:25
 * @Description: 对所有bean 下面的所有 field注入proxy对象
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    ProxyFactory proxyFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //查看bean的字段中是否对应注解 RpcReference
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for(Field filed:declaredFields){
            RpcReference annotation = filed.getAnnotation(RpcReference.class);
            if(null != annotation){
                //获取代理对象
                Object proxy = proxyFactory.getProxy(filed.getType());
                try {
                    filed.setAccessible(true);
                    filed.set(bean,proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
