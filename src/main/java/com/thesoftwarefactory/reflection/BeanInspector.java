package com.thesoftwarefactory.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.googlecode.gentyref.GenericTypeReflector;
import com.thesoftwarefactory.reflection.impl.Classes;
import com.thesoftwarefactory.reflection.impl.PojoImpl;
import com.thesoftwarefactory.reflection.impl.PropertyField;
import com.thesoftwarefactory.reflection.impl.PropertyMethod;
import com.thesoftwarefactory.reflection.type.Types;

public class BeanInspector {

	private Map<Type, Pojo> propertyHolders = new HashMap<>(); 
	private Predicate<Property>[] filters = null;
	
	public BeanInspector(Predicate<Property>... filters) {
		Objects.requireNonNull(filters);
		
		this.filters = filters;
	}

	public final static BeanInspector commonInspector() {
		Predicate<Property> filter = new Predicate<Property>() {
			@Override
			public boolean test(Property property) {
				return Modifier.isStatic(property.getModifiers()) 
						|| Modifier.isTransient(property.getModifiers())
						|| Modifier.isVolatile(property.getModifiers());
			}
		};
		return new BeanInspector(filter);
	}
	
	public synchronized Pojo getBean(Type type) {
		Objects.requireNonNull(type);
		
		Pojo result = null;
		// check if the pojo is already in the cache
		result = propertyHolders.get(type);
		if (result==null) {
			// create the pojo and put it in the cache for later reuse
			result = new PojoImpl(type, getProperties(type));
			propertyHolders.put(type, result);
		}
		return result;
	}
	
	/**
	 * returns a Property if there is matching getter and setter method:
	 *  X name() / name(X x)
	 *  
	 * @param cls
	 * @param getter
	 * @return
	 */
	private Property getFluentProperty(Type ownerType, Class<?> cls, Method getter) {
		Objects.requireNonNull(ownerType);
		Objects.requireNonNull(cls);
		Objects.requireNonNull(getter);

		Property result = null;
		String propertyName = getter.getName();
		String getterMethodName = getter.getName();
		// find a matching setter
		String setterMethodName = getterMethodName;
		Method setterMethod = Classes.getMethod(cls, setterMethodName, getter.getReturnType());
		if (setterMethod!=null) {
			Type propertyType = getReifiedType(ownerType, getter);
			result = new PropertyMethod(propertyName, propertyType, getter.getModifiers(), getter, setterMethod);
		}
		return result;
	}

	/**
	 * returns a Property if there is matching getter and setter method:
	 *  X getName() / setName(X x)
	 *  X isName() / setName(X x)
	 *  
	 * @param cls
	 * @param getter
	 * @return
	 */
	private Property getJavaBeanProperty(Type ownerType, Class<?> cls, Method getter) {
		Objects.requireNonNull(ownerType);
		Objects.requireNonNull(cls);
		Objects.requireNonNull(getter);

		Property result = null;
		String propertyName = getter.getName();
		String getterMethodName = getter.getName();
		if (getterMethodName.startsWith("get")) {
			propertyName = getterMethodName.substring(3);
			propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		}
		else if (getterMethodName.startsWith("is")) {
			propertyName = getterMethodName.substring(2);
			propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		}
		// find a matching setter
		String setterMethodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		Method setterMethod = Classes.getMethod(cls, setterMethodName, getter.getReturnType());
		if (setterMethod!=null) {
			Type propertyType = getReifiedType(ownerType, getter);
			result = new PropertyMethod(propertyName, propertyType, getter.getModifiers(), getter, setterMethod);
		}
		return result;
	}

	/**
	 * returns all matching getter and setter methods of the given class and superclasses. 
	 * ie: for a property whose name is name then the following combination of getter/setter would match:
	 *  X getName() / setName(X x)
	 *  X isName() / setName(X x)
	 *  X name() / name(X x)	-> for fluent apis
	 *  X getName() / name(X x)	-> for semi fluent apis
	 *  X name() / setName(X x)	-> for semi fluent apis
	 * 
	 * @param cls
	 * @return
	 */
	private Property[] getProperties(Type type) {
		Objects.requireNonNull(type);
		
		Class<?> cls = Types.getRawClass(type);
		Map<String, Property> result = new HashMap<>();
		Method[] getterMethods = Classes.getGetters(cls);
		for (Method getterMethod: getterMethods) {
			Property property = getJavaBeanProperty(type, cls, getterMethod);
			if (property!=null && !isFiltered(property)) {
				if (!result.containsKey(property.getName())) {
					result.put(property.getName(), property);
				}
			}
			else {
				property = getFluentProperty(type, cls, getterMethod);
				if (property!=null && !isFiltered(property)) {
					if (!result.containsKey(property.getName())) {
						result.put(property.getName(), property);
					}
				}				
			}
		}
		// add all public non static fields
		for (Field field: cls.getFields()) {
			Property property = getPropertyField(type, cls, field);
			if (property!=null && !isFiltered(property)) {
				if (!result.containsKey(property.getName())) {
					result.put(property.getName(), property);
				}
			}				
		}
		return result.values().toArray(new Property[0]);
	}

	/**
	 * returns a Property for the specified field 
	 *  
	 * @param cls
	 * @param field
	 * @return
	 */
	private Property getPropertyField(Type ownerType, Class<?> cls, Field field) {
		Objects.requireNonNull(ownerType);
		Objects.requireNonNull(cls);
		Objects.requireNonNull(field);

		Type propertyType = getReifiedType(ownerType, field);
		return new PropertyField(field.getName(), propertyType, field.getModifiers(), field);
	}

	private Type getReifiedType(Type ownerType, Field field) {
		Type result = GenericTypeReflector.getExactFieldType(field, ownerType);
		return result;
	}

	private Type getReifiedType(Type ownerType, Method method) {
		Type result = GenericTypeReflector.getExactReturnType(method, ownerType);
		return result;
	}

	boolean isFiltered(Property property) {
		Objects.requireNonNull(property);

		for (Predicate<Property> filter: filters) {
			if (filter.test(property)) {
				return true;
			}
		}
		return false;
	}

}
