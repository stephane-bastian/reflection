package com.thesoftwarefactory.reflection;

import java.lang.reflect.Type;

/**
 * Defines a Plain Old Java Object and its properties. Note that a property does *not* have to be a javaBean type property.
 * It can be a property which is accessible solely via a Field (and no getter/setter). The property can be transient/volatile or any other modifier.
 * The only restriction is that the property must exists
 * 
 * @author stephane
 *
 */
public interface Pojo {

	public abstract Property[] getProperties();

	public abstract Property getProperty(String name);

	public abstract Property getPropertyBySaveAsName(String name);

	public abstract Type getType();

}
