/*
 * Copyright (c) 2008 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.collections.alias;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang.StringUtils;

import com.mysema.query.grammar.types.Path;
import com.mysema.query.grammar.types.PathMetadata;
import com.mysema.query.grammar.types.ColTypes.ExtString;
import com.mysema.query.grammar.types.Path.PCollection;

/**
 * PropertyAccessInvocationHandler provides
 *
 * @author tiwe
 * @version $Id$
 */
class PropertyAccessInvocationHandler implements MethodInterceptor{
    
    private AliasFactory aliasFactory;
    
    private final Map<String,Object> propToObj = new HashMap<String,Object>();
    
    private final Map<String,Path<?>> propToPath = new HashMap<String,Path<?>>();
    
    public PropertyAccessInvocationHandler(AliasFactory aliasFactory){
        this.aliasFactory = aliasFactory;
    }
    
    public Object intercept(Object proxy, Method method, Object[] args,
            MethodProxy methodProxy) throws Throwable {        
        if (isGetter(method)){
            String ptyName = propertyNameForGetter(method);
            Class<?> ptyClass = method.getReturnType();
            
            Object rv;
            if (propToObj.containsKey(ptyName)){
                rv = propToObj.get(ptyName);
            }else{
                Path<?> parentPath = aliasFactory.pathForAlias(proxy);
                if (parentPath == null) throw new IllegalArgumentException("No path for " + proxy);
                PathMetadata<String> pm = PathMetadata.forProperty(parentPath, ptyName);
                rv = makeNew(ptyClass, proxy, ptyName, pm);            
            }       
            aliasFactory.setCurrent(propToPath.get(ptyName));                        
            return rv; 
            
        }else if (method.getName().equals("size")){
            String ptyName = "_size";
            Object rv;
            if (propToObj.containsKey(ptyName)){
                rv = propToObj.get(ptyName);
            }else{
                Path<?> parentPath = aliasFactory.pathForAlias(proxy);
                if (parentPath == null) throw new IllegalArgumentException("No path for " + proxy);
                PathMetadata<Integer> pm = PathMetadata.forSize((PCollection<?>) parentPath);
                rv = makeNew(Integer.class, proxy, ptyName, pm);            
            }       
            aliasFactory.setCurrent(propToPath.get(ptyName));
            return rv; 
            
        }else{
            return methodProxy.invokeSuper(proxy, args);    
        }        
    }
    
    private boolean isGetter(Method m){
        return m.getParameterTypes().length == 0 && 
            (m.getName().startsWith("is") || m.getName().startsWith("get"));
    }

    @SuppressWarnings("unchecked")
    private <T> T makeNew(Class<T> type, Object parent, String prop, PathMetadata<?> pm) {        
        Path<?> path;
        T rv;
        
        if (String.class.equals(type)) {
            path = new ExtString(pm);
            rv = (T) new String();
            
        } else if (Integer.class.equals(type)) {
            path = new Path.PComparable<Integer>(Integer.class,pm);
            rv =  (T) new Integer(42);
            
        } else if (Date.class.equals(type)) {
            path = new Path.PComparable<Date>(Date.class,pm);
            rv =  (T) new Date();
            
        } else if (Long.class.equals(type)) {
            path = new Path.PComparable<Long>(Long.class,pm);
            rv =  (T) new Long(42);
            
        } else if (Short.class.equals(type)) {
            path = new Path.PComparable<Short>(Short.class,pm);
            rv =  (T) new Short((short) 42);
            
        } else if (Double.class.equals(type)) {
            path = new Path.PComparable<Double>(Double.class,pm);
            rv =  (T) new Double(42);
            
        } else if (Float.class.equals(type)) {
            path = new Path.PComparable<Float>(Float.class,pm);
            rv =  (T) new Float(42);
            
        } else if (BigInteger.class.equals(type)) {
            path = new Path.PComparable<BigInteger>(BigInteger.class,pm);
            rv =  (T) new BigInteger("42");
            
        } else if (BigDecimal.class.equals(type)) {
            path = new Path.PComparable<BigDecimal>(BigDecimal.class,pm);
            rv =  (T) new BigDecimal(42);
            
        } else if (Boolean.class.equals(type)) {
            path = new Path.PComparable<Boolean>(Boolean.class,pm);
            rv =  (T) new Boolean(true);
            
        } else if (List.class.isAssignableFrom(type)) {
            path = new Path.PComponentList(null,pm);
            rv = (T) aliasFactory.createAliasForProp(List.class, parent, prop, path);
            
        } else if (Set.class.isAssignableFrom(type)) {
            path = new Path.PComponentCollection(null,pm);
            rv = (T) aliasFactory.createAliasForProp(Set.class, parent, prop, path);
            
        } else if (Collection.class.isAssignableFrom(type)) {
            path = new Path.PComponentCollection(null,pm);
            rv = (T) aliasFactory.createAliasForProp(Collection.class, parent, prop, path);
            
        } else if (Map.class.isAssignableFrom(type)) {
            path = new Path.PComponentMap(null,null,pm);
            rv = (T) aliasFactory.createAliasForProp(Map.class, parent, prop, path);
            
        } else if (Enum.class.isAssignableFrom(type)) {
            path = new Path.PSimple<T>(type, pm);
            rv =  type.getEnumConstants()[0];
            
        } else {
            path = new Path.PSimple<T>(type, pm);
            rv = (T) aliasFactory.createAliasForProp(type, parent, prop, path);            
        }
        propToObj.put(prop, rv);
        propToPath.put(prop, path);        
        return rv;
    }

    private String propertyNameForGetter(Method method) {
        String name = method.getName();
        name = name.startsWith("is") ? name.substring(2) : name.substring(3);
        return StringUtils.uncapitalize(name);
    }

}
