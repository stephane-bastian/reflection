/*
 * Copyright (c) 2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package com.thesoftwarefactory.reflection.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:stephane.bastian.dev@gmail.com">Stephane Bastian</a>
 *
 */
public class Types {

	private static void getParameterTypes(Type type, List<Type> result) {
	    if (type instanceof Class) {
	    	Class<?> cls = (Class<?>) type;
	    	if (cls.isArray()) {
	    		getParameterTypes(cls.getComponentType(), result);
	    	}
	    	else if (cls.getTypeParameters().length>0) {
	    		for (Type parameterType: cls.getTypeParameters()) {
		    		getParameterTypes(parameterType, result);
	    		}
	    	}
	    	else {
	    		result.add(type);
	    	}
	    }
	    else if (type instanceof ParameterizedType) {
	        ParameterizedType parameterizedType = (ParameterizedType) type;
    		for (Type typeArgument: parameterizedType.getActualTypeArguments()) {
	    		getParameterTypes(typeArgument, result);
    		}
	    }
	    else if (type instanceof GenericArrayType) {
	        GenericArrayType genericArrayType = (GenericArrayType) type;
    		getParameterTypes(genericArrayType.getGenericComponentType(), result);
	    }
	    else if (type instanceof TypeVariable) {
	    	TypeVariable<?> typeVariable = (TypeVariable<?>) type;
    		for (Type bound: typeVariable.getBounds()) {
	    		getParameterTypes(bound, result);
    		}
	    }
	    else if (type instanceof WildcardType) {
	    	WildcardType wildcardType = (WildcardType) type;
    		if (wildcardType.getLowerBounds().length>0) {
		    	for (Type lowerBound: wildcardType.getLowerBounds()) {
		    		getParameterTypes(lowerBound, result);
	    		}
    		}
    		else {
	    		for (Type upperBound: wildcardType.getUpperBounds()) {
		    		getParameterTypes(upperBound, result);
	    		}	
    		}
	    }
	}
	
	public static Type[] getParameterTypes(Type type) {
		List<Type> result = new ArrayList<>();
		getParameterTypes(type, result);
		return result.toArray(new Type[result.size()]);
	}
		
	public final static boolean isArrayOf(Type type, Type expectedParameterType) {
		if (type instanceof Class) {
			Class<?> cls = (Class<?>) type;
			return cls.isArray() && isAssignable(cls.getComponentType(), expectedParameterType);
		}
		else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return isAssignable(genericArrayType.getGenericComponentType(), expectedParameterType);
		}
		return false;
	}
	
	/**
	 * returns whether the source type can be converted to the specified target type
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean isAssignable(Type source, Type target) {
		if ( (source==null && target==null)
			|| (source!=null && target==null) 
			|| (source==null && target!=null)) {
			return false;
		}
		if (source==target) {
			return true;
		}
	    if (source instanceof Class) {
	    	Class<?> sourceClass = (Class<?>) source;
	    	if (target instanceof Class) {
	    		return ((Class<?>)target).isAssignableFrom(sourceClass);
	    	}
	    	else if (target instanceof ParameterizedType) {
	    		ParameterizedType targetParameterizedType = (ParameterizedType) target;
	    		if (sourceClass.getTypeParameters().length == targetParameterizedType.getActualTypeArguments().length) {
		    		if (isAssignable(sourceClass, targetParameterizedType.getRawType())) {
			        	for (int i=0; i<targetParameterizedType.getActualTypeArguments().length; i++) {
			        		Type sourceTypeArgument = sourceClass.getTypeParameters()[i];
			        		Type targetTypeArgument = targetParameterizedType.getActualTypeArguments()[i];
			        		if (!isAssignable(sourceTypeArgument, targetTypeArgument)) {
			        			return false;
			        		}
			        	}
			        	return true;
			        }
	    		}
	    		else {
	    			return false;
	    		}
	    	}
	    }
	    else if (source instanceof ParameterizedType) {
    		ParameterizedType sourceParameterizedType = (ParameterizedType) source;
	    	if (target instanceof Class) {
	    		if (isAssignable(sourceParameterizedType.getRawType(), target)) {
		        	for (Type typeArgument: sourceParameterizedType.getActualTypeArguments()) {
		        		if (!isAssignable(typeArgument, Object.class)) {
		        			return false;
		        		}
		        	}
		        	return true;
		        }
	    	}
	    	else if (target instanceof ParameterizedType) {
	    		ParameterizedType targetParameterizedType = (ParameterizedType) target;
	    		if (sourceParameterizedType.getActualTypeArguments().length != targetParameterizedType.getActualTypeArguments().length) {
	    			return false;
	    		}
	    		if (isAssignable(sourceParameterizedType.getRawType(), targetParameterizedType.getRawType())) {
		        	for (int i=0; i<sourceParameterizedType.getActualTypeArguments().length; i++) {
		        		if (!isAssignable(sourceParameterizedType.getActualTypeArguments()[i], targetParameterizedType.getActualTypeArguments()[i])) {
		        			return false;
		        		}
		        	}
		        	return true;
		        }
	    	}
	    }
	    else if (source instanceof GenericArrayType) {
	        GenericArrayType soruceGenericArrayType = (GenericArrayType)source;
//	        return getClass(genericArrayType.getGenericComponentType());
	    }
	    else if (source instanceof TypeVariable) {
	    	TypeVariable<?> sourceTypeVariable = (TypeVariable<?>) source;
	    	if (target instanceof Class) {
	    		Class<?> targetClass = (Class<?>) target;
	    		if (sourceTypeVariable.getBounds().length==1) {
	    			return isAssignable(sourceTypeVariable.getBounds()[0], targetClass);
	    		}
	    	}
	    	throw new IllegalArgumentException("TypeVariable is not supported");
	    }
	    else if (source instanceof WildcardType) {
	    	WildcardType sourceWildcardType = (WildcardType) source;
	    	if (target instanceof Class) {
	    		Class<?> targetClass = (Class<?>) target;
		    	if (sourceWildcardType.getUpperBounds().length==1) {
		    		return isAssignable(sourceWildcardType.getUpperBounds()[0], targetClass);
		    	}
	    	}
	    	else if (target instanceof WildcardType) {
		    	WildcardType targetWildcardType = (WildcardType) target;
		    	if (sourceWildcardType.getLowerBounds().length>0 || targetWildcardType.getLowerBounds().length>0) {
		    		// TODO: tricky case, return false for now but we need to revisit this
    				return false;
		    	}
		    	else if (sourceWildcardType.getUpperBounds().length>0 && sourceWildcardType.getUpperBounds().length == targetWildcardType.getUpperBounds().length) {
		    		for (int i=0; i<sourceWildcardType.getUpperBounds().length; i++) {
		    			Type sourceUpperBound = sourceWildcardType.getUpperBounds()[i];
		    			Type targetUpperBound = targetWildcardType.getUpperBounds()[i];
		    			if (!isAssignable(sourceUpperBound, targetUpperBound)) {
		    				return false;
		    			}
		    		}
		    		return true;
		    	}
		    	return false;
	    	}
	    }
		return false;
	}

	public final static boolean isCollectionOf(Type type, Type expectedItemType) {
		return isParameterizedType(type, Collection.class, expectedItemType);
	}

	public final static boolean isListOf(Type type, Type expectedItemType) {
		return isParameterizedType(type, List.class, expectedItemType);
	}

	public final static boolean isParameterizedType(Type type, Type expectedRawType, Type expectedParameterType) {
		if (type instanceof ParameterizedType) {
	    	ParameterizedType parameterizedType = (ParameterizedType) type;
	    	if (parameterizedType.getActualTypeArguments().length==1) {
	        	if (isAssignable(parameterizedType.getRawType(), expectedRawType) && isAssignable(parameterizedType.getActualTypeArguments()[0], expectedParameterType)) {
	            	return true;
	        	}
	    	}
		}
		return false;
	}

	public final static boolean isPrimitiveOrWrapper(Class<?> cls) {
		boolean result = cls.isPrimitive();
		if (!result) {
			if (Boolean.class.equals(cls)) {
				return true;
			}
			else if (Character.class.equals(cls)) {
				return true;
			}
			else if (Byte.class.equals(cls)) {
				return true;
			}
			else if (Short.class.equals(cls)) {
				return true;
			}
			else if (Integer.class.equals(cls)) {
				return true;
			}
			else if (Long.class.equals(cls)) {
				return true;
			}
			else if (Float.class.equals(cls)) {
				return true;
			}
			else if (Double.class.equals(cls)) {
				return true;
			}
			else if (Void.class.equals(cls)) {
				return true;
			}
		}
		return result;
	}

	public final static boolean isSetOf(Type type, Type expectedItemType) {
		return isParameterizedType(type, Set.class, expectedItemType);
	}

	public static Class<?> toClass(Type expectedType) {
	    if (expectedType instanceof Class)
	        return (Class<?>) expectedType;
	    return null;
	}

	public final static Type getComponentType(Type type) {
		Objects.requireNonNull(type);
	
		Type result = null;
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			if (parameterizedType.getActualTypeArguments().length==1) {
				result = parameterizedType.getActualTypeArguments()[0];
			}
		}
		else if (type instanceof GenericArrayType) {
			result = ((GenericArrayType) type).getGenericComponentType();
		}
		return result;
	}

	public final static Class<?> getRawClass(Type type) {
			if (type==null) {
				System.out.println("xx");
			}
			Objects.requireNonNull(type);
	
			Class<?> result = null;
			if (type instanceof Class<?>) {
				result = (Class<?>) type;
			}
			else if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				result = getRawClass(parameterizedType.getRawType());
			}
			else if (type instanceof GenericArrayType) {
				Type componentType = ((GenericArrayType) type).getGenericComponentType();
				Class<?> componentClass = getRawClass(componentType);
			    if (componentClass != null ) {
	//		    	Object tmpArray = Array.newInstance(componentClass, 0);
	//		        result = tmpArray.getClass();
			    	result = componentClass;
			    }
			    else {
			    	result = Object[].class;
			    }
			}
			return result;
		}

	public final static boolean isCollection(Class<?> cls) {
		if (cls!=null) {
			return Collection.class.isAssignableFrom(cls);
		}
		return false;
	}

	public final static boolean isMap(Class<?> cls) {
		if (cls!=null) {
			return Map.class.isAssignableFrom(cls);
		}
		return false;
	}

}
