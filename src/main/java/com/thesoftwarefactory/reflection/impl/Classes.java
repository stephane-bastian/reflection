package com.thesoftwarefactory.reflection.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Classes {

	public final static Object instantiate(Class<?> cls, Object... args) {
		Objects.requireNonNull(cls);
		Object result = null;
		
		try {
			Class<?>[] argClasses = new Class[args.length];
			for (int i=0; i<argClasses.length; i++) {
				argClasses[i] = args[i]!=null ? args[i].getClass() : null;
			}
			Constructor<?> constructor = cls.getDeclaredConstructor(argClasses);
			if (constructor!=null) {
				constructor.setAccessible(true);
				result = constructor.newInstance(args);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * returns the declared Method in the specified cls or any superclass   
	 * 
	 * @param cls
	 * @param name
	 * @return
	 */
	public final static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
		Objects.requireNonNull(cls);
		Objects.requireNonNull(name);
		
		Method result = null;
		if (name!=null) {
			while (cls!=null) {
				try {
					result = cls.getDeclaredMethod(name, parameterTypes);
				}
				catch (Exception e) {
				}
				if (result!=null) {
					break;
				}
				cls = cls.getSuperclass();
			}
		}
		return result;
	}

	/**
	 * returns the declared Field in the specified cls or any superclass   
	 * 
	 * @param cls
	 * @param name
	 * @return
	 */
	public final static Field getField(Class<?> cls, String name) {
		Objects.requireNonNull(cls);
		Objects.requireNonNull(name);
		
		Field result = null;
		if (name!=null) {
			while (cls!=null) {
				try {
					result = cls.getDeclaredField(name);
				}
				catch (Exception e) {
				}
				if (result!=null) {
					break;
				}
				cls = cls.getSuperclass();
			}
		}
		return result;
	}

	/**
	 * returns all getter methods of the given class and superclasses:
	 * 		no arguments
	 * 		returning a value
	 * 		public
	 * 		not static
	 * 
	 * @param cls
	 * @return
	 */
	public final static Method[] getGetters(Class<?> cls) {
		Objects.requireNonNull(cls);
		
		Map<String, Method> result = new HashMap<>();
		while (cls!=null) {
			Method[] declaredMethods = cls.getDeclaredMethods();
			for (Method declaredMethod: declaredMethods) {
				if (declaredMethod.getReturnType()!=null && declaredMethod.getParameterTypes().length==0) {
				// returns a value and it has no parameters
					if (!result.containsKey(declaredMethod.getName())) {
					// it does not already exist (defined by a subclass)
						result.put(declaredMethod.getName(), declaredMethod);
					}
				}
			}
			// get the methods of the superclass
			cls = cls.getSuperclass();
		}
		return result.values().toArray(new Method[0]);
	}

}
