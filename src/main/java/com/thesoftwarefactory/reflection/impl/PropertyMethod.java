package com.thesoftwarefactory.reflection.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Objects;

public class PropertyMethod extends BaseProperty {

	private Method getter = null;
	private Method setter = null;
	
	public PropertyMethod(String name, Type type, int modifiers, Method getter, Method setter) {
		super(name, type, modifiers);
		Objects.requireNonNull(getter);
		Objects.requireNonNull(setter);
			
		this.getter = getter;
		this.setter = setter;
		if (!Modifier.isPublic(modifiers)) {
		// no public access, set accessible to true to allow access to non-public methods
			this.getter.setAccessible(true);
			this.setter.setAccessible(true);
		}
	}

	@Override
	public Object getValue(Object object) {
		try {
			return getter.invoke(object);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} 
		catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} 
		catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		try {
			setter.invoke(object, new Object[]{value});
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} 
		catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} 
		catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
