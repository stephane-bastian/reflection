package com.thesoftwarefactory.reflection.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.thesoftwarefactory.reflection.Property;
import com.thesoftwarefactory.reflection.type.Types;

public abstract class BaseProperty implements Property {
	
	private Map<String, Object> attributes = null;
	private boolean isArray = false;
	private boolean isCollection = false;
	private boolean isMap = false;
	private int modifiers;
	private String name = null;
	private Class<?> rawClass = null;
	private String saveAsName = null;
	private Type type = null;

	public BaseProperty(String name, Type type, int modifiers) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		
		this.name = name;
		this.type = type;
		this.modifiers = modifiers;
		this.rawClass = Types.getRawClass(getType());
		if (rawClass!=null) {
			this.isArray = rawClass.isArray();
			this.isCollection = Types.isCollection(rawClass);
			this.isMap = Types.isMap(rawClass);
		}
	}
	
	@Override
	public Map<String, Object> attributes() {
		if (attributes==null) {
			attributes = new HashMap<String, Object>();
		}
		return attributes;
	}

	@Override
	public int getModifiers() {
		return modifiers;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getRawClass() {
		return rawClass;
	}

	@Override
	public String getSaveAsName() {
		return saveAsName;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean isCollection() {
		return isCollection;
	}

	@Override
	public boolean isMap() {
		return isMap;
	}

	public void setSaveAsName(String name) {
		this.saveAsName = name;
	}

}
