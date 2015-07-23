package com.thesoftwarefactory.reflection;

import java.lang.reflect.Type;
import java.util.Map;

public interface Property {
	public abstract Map<String, Object> attributes();
	
	public abstract int getModifiers();

	public abstract String getName();

	public abstract String getSaveAsName();

	public abstract Class<?> getRawClass();
	
	public abstract Type getType();

	public Object getValue(Object object);

	public boolean isArray();

	public boolean isCollection();

	public boolean isMap();

	public void setSaveAsName(String name);

	public void setValue(Object object, Object value);

}