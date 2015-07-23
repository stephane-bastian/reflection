package com.thesoftwarefactory.reflection.impl;

import java.lang.reflect.Type;
import java.util.Objects;

import com.thesoftwarefactory.reflection.Property;
import com.thesoftwarefactory.reflection.Pojo;

public class PojoImpl implements Pojo {
	
	public Property[] properties;
	private Type type = null;

	/**
	 * 
	 * @param cls
	 */
	public PojoImpl(Type type, Property[] properties) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(properties);

		this.type = type;
		this.properties = properties;
	}

	@Override
	public Property[] getProperties() {
		return properties;
	}

	@Override
	public Property getProperty(String name) {
		if (properties!=null) {
			for (Property property: properties) {
				if (property!=null && name.equals(property.getName())) {
					return property;
				}
			}
		}
		return null;
	}
	
	@Override
	public Property getPropertyBySaveAsName(String name) {
		if (properties!=null) {
			for (Property property: properties) {
				if (property!=null && name.equals(property.getSaveAsName())) {
					return property;
				}
				else if (property!=null && name.equals(property.getName())) {
					return property;
				}
			}
		}
		return null;
	}

	@Override
	public Type getType() {
		return type;
	}
	
}
