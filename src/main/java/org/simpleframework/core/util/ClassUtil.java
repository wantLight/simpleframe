package org.simpleframework.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xyzzg on 2020/4/20.
 */
@Slf4j
public class ClassUtil {


    public static final String FILE = "file";

    /**
     * 获取包下类集合
     *
     *
     *
     * @param packageName 包名
     * @return 类集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName){
        // 1.获取类加载器
        ClassLoader classLoader = getClassLoader();
        // 2.通过类加载器获取到加载的资源信息 -- 不能让用户传绝对路径 通过url获取
        URL url = classLoader.getResource(packageName.replace(".","/"));
        if ( url == null ){
            log.warn("获取不到任何路径鸭");
            return null;
        }
        // 3.依据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet = null;
        // 过滤出文件类型的资源
        if (url.getProtocol().equalsIgnoreCase(FILE)){
            classSet = new HashSet <>();
            File packageDirectory = new File(url.getPath());
            // 提取所有的class文件
            extractClassFile(classSet, packageDirectory, packageName);
        }
        // todo 增加对其他资源的处理 （如jar）
        return classSet;
    }

    /**
     * 递归-获取目标package里面里面所有的class文件
     * （包括子package里面的）
     * @param emptyClassSet 装载目标类的集合
     * @param fileSource 文件 or 目录
     * @param packageName 包名
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet,File fileSource,String packageName) {
        // 找到文件停住
        if (!fileSource.isDirectory()){
            return;
        }
        //如果是一个文件夹，则调用获取所有的文件/文件夹
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()){
                    return true;
                } else {
                    // class文件 - 获取绝对值路径
                    String absoluteFilePath = fileSource.getAbsolutePath();
                    if (absoluteFilePath.endsWith(".class")){
                        // 加载class
                        addToClassSet(absoluteFilePath);
                    }
                }
                return false;
            }

            private void addToClassSet(String absoluteFilePath) {
                //1.从class文件的绝对值路径里提出包含package的类名
                absoluteFilePath = absoluteFilePath.replace(File.separator,".");
                String className = absoluteFilePath.substring(absoluteFilePath.indexOf(packageName));
                className = className.substring(0,className.lastIndexOf("."));
                //2.通过反射机制获取对应的class对象并加入到classSet里
                Class targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
            }
        });
        // foreach遇到files为空会异常
        if (files != null){
            for ( File f : files ){
                // 递归调用
                extractClassFile(emptyClassSet,f,packageName);
            }
        }

    }

    /**
     * 获取Class对象
     * @param className
     * @return
     */
    public static Class<?> loadClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load error");
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化class
     *
     * @param clazz Class
     * @param <T>   class的类型
     * @param accessible   是否支持创建出私有class对象的实例
     * @return 类的实例化
     */
    public static <T> T newInstance(Class<?> clazz, boolean accessible){
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T)constructor.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置类的属性值
     *
     * @param field      成员变量
     * @param target     类实例
     * @param value      成员变量的值
     * @param accessible 是否允许设置私有属性
     */
    public static void setField(Field field,Object target,Object value,boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取ClassLoader
     * @return
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }
}
