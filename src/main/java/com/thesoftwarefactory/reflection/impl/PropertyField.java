package com.thesoftwarefactory.reflection.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Objects;

public class PropertyField extends BaseProperty {

	private Field field = null;
	
	public PropertyField(String name, Type type, int modifiers, Field field) {
		super(name, type, modifiers);
		Objects.requireNonNull(field);
		
		this.field = field;
		if (!Modifier.isPublic(field.getModifiers())) {
		// no public access, set accessible to true to allow access to non-public fields
			this.field.setAccessible(true);
		}
	}

	@Override
	public Object getValue(Object object) {
		try {
			return field.get(object);
		} 
		catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setValue(Object object, Object value) {
		try {
			field.set(object, value);
		} 
		catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
