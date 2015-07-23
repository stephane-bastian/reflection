package com.thesoftwarefactory.reflection;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.thesoftwarefactory.reflection.type.TypeToken;
import com.thesoftwarefactory.reflection.type.Types;

public class TestTypes {

	@Test
	public void isPrimitiveOrWrapper() {
		assertTrue(Types.isPrimitiveOrWrapper(boolean.class));
		assertTrue(Types.isPrimitiveOrWrapper(char.class));
		assertTrue(Types.isPrimitiveOrWrapper(byte.class));
		assertTrue(Types.isPrimitiveOrWrapper(short.class));
		assertTrue(Types.isPrimitiveOrWrapper(int.class));
		assertTrue(Types.isPrimitiveOrWrapper(long.class));
		assertTrue(Types.isPrimitiveOrWrapper(float.class));
		assertTrue(Types.isPrimitiveOrWrapper(double.class));
		assertTrue(Types.isPrimitiveOrWrapper(void.class));

		assertTrue(Types.isPrimitiveOrWrapper(Boolean.class));
		assertTrue(Types.isPrimitiveOrWrapper(Character.class));
		assertTrue(Types.isPrimitiveOrWrapper(Byte.class));
		assertTrue(Types.isPrimitiveOrWrapper(Short.class));
		assertTrue(Types.isPrimitiveOrWrapper(Integer.class));
		assertTrue(Types.isPrimitiveOrWrapper(Long.class));
		assertTrue(Types.isPrimitiveOrWrapper(Float.class));
		assertTrue(Types.isPrimitiveOrWrapper(Double.class));
		assertTrue(Types.isPrimitiveOrWrapper(Void.class));
	}
	
	@Test
	public void isAssignableClassToClass() {
		assertTrue(Types.isAssignable(String.class, String.class));

		assertTrue(Types.isAssignable(String.class, Object.class));

		assertTrue(Types.isAssignable(Integer.class, Number.class));

		assertTrue(Types.isAssignable(String.class, new TypeToken<String>() {}.type()));

		assertTrue(Types.isAssignable(String.class, new TypeToken<Object>() {}.type()));

		assertTrue(!Types.isAssignable(Object.class, String.class));

		assertTrue(!Types.isAssignable(Object.class, new TypeToken<String>() {}.type()));

		assertTrue(!Types.isAssignable(Collection.class, List.class));

		assertTrue(Types.isAssignable(List.class, Collection.class));
	}

	@Test
	public void isAssignableClassToGeneric() {		
		assertTrue(Types.isAssignable(Collection.class, new TypeToken<Collection<Object>>() {}.type()));

		assertTrue(!Types.isAssignable(Collection.class, new TypeToken<Collection<String>>() {}.type()));
		
		assertTrue(Types.isAssignable(List.class, new TypeToken<Collection<Object>>() {}.type()));

		assertTrue(!Types.isAssignable(List.class, new TypeToken<Collection<String>>() {}.type()));
	}

	@Test
	public void isAssignableGenericToClass() {
		assertTrue(Types.isAssignable(new TypeToken<Collection<String>>() {}.type(), Collection.class));

		assertTrue(Types.isAssignable(new TypeToken<List<String>>() {}.type(), Collection.class));

		assertTrue(Types.isAssignable(new TypeToken<List<String>>() {}.type(), Collection.class));

		assertTrue(Types.isAssignable(new TypeToken<List<?>>() {}.type(), Collection.class));

		assertTrue(Types.isAssignable(new TypeToken<List<? extends String>>() {}.type(), Collection.class));

		assertTrue(Types.isAssignable(new TypeToken<Collection<String>>() {}.type(), Collection.class));

	}

	@Test
	public void isAssignableGenericToGeneric() {
		assertTrue(Types.isAssignable(new TypeToken<Collection<String>>() {}.type(), new TypeToken<Collection<String>>() {}.type()));

		assertTrue(Types.isAssignable(new TypeToken<List<String>>() {}.type(), new TypeToken<Collection<String>>() {}.type()));

		assertTrue(Types.isAssignable(new TypeToken<List<String>>() {}.type(), new TypeToken<Collection<Object>>() {}.type()));

		assertTrue(Types.isAssignable(new TypeToken<List<?>>() {}.type(), new TypeToken<Collection<Object>>() {}.type()));

		assertTrue(Types.isAssignable(new TypeToken<List<? extends String>>() {}.type(), new TypeToken<Collection<Object>>() {}.type()));

		assertTrue(Types.isAssignable(new TypeToken<List<? extends String>>() {}.type(), new TypeToken<Collection<String>>() {}.type()));

		assertTrue(Types.isAssignable(new TypeToken<List<? extends Integer>>() {}.type(), new TypeToken<Collection<? extends Number>>() {}.type()));

		assertTrue(!Types.isAssignable(new TypeToken<List<? extends Integer>>() {}.type(), new TypeToken<Collection<? super Number>>() {}.type()));

		assertTrue(!Types.isAssignable(new TypeToken<List<? extends Number>>() {}.type(), new TypeToken<Collection<? extends Integer>>() {}.type()));

		assertTrue(!Types.isAssignable(new TypeToken<List<? extends Number>>() {}.type(), new TypeToken<Collection<? super Integer>>() {}.type()));

		assertTrue(!Types.isAssignable(new TypeToken<Collection<? extends String>>() {}.type(), new TypeToken<Collection<Number>>() {}.type()));
	}
	
	@Test
	public void isListOf() {
		assertTrue( Types.isListOf(new TypeToken<List<? extends Integer>>() {}.type(), Number.class));
		assertTrue( Types.isListOf(new TypeToken<List<? extends Integer>>() {}.type(), Integer.class));
		assertTrue( Types.isListOf(new TypeToken<ArrayList<? extends Integer>>() {}.type(), Number.class));
		assertTrue( !Types.isListOf(new TypeToken<ArrayList<? extends Integer>>() {}.type(), Long.class));
		assertTrue( !Types.isListOf(new TypeToken<List<? extends Number>>() {}.type(), String.class));
	}

	@Test
	public void isSetOf() {
		assertTrue( Types.isSetOf(new TypeToken<Set<? extends Integer>>() {}.type(), Number.class));
		assertTrue( Types.isSetOf(new TypeToken<Set<? extends Integer>>() {}.type(), Integer.class));
		assertTrue( Types.isSetOf(new TypeToken<HashSet<? extends Integer>>() {}.type(), Number.class));
		assertTrue( !Types.isSetOf(new TypeToken<HashSet<? extends Integer>>() {}.type(), Long.class));
		assertTrue( !Types.isSetOf(new TypeToken<Set<? extends Number>>() {}.type(), String.class));
	}

	@Test
	public void isCollectionOf() {
		assertTrue( Types.isCollectionOf(new TypeToken<Collection<? extends Integer>>() {}.type(), Number.class));
		assertTrue( Types.isCollectionOf(new TypeToken<Collection<? extends Integer>>() {}.type(), Integer.class));
		assertTrue( Types.isCollectionOf(new TypeToken<List<? extends Integer>>() {}.type(), Number.class));
		assertTrue( !Types.isCollectionOf(new TypeToken<Set<? extends Integer>>() {}.type(), Long.class));
		assertTrue( !Types.isCollectionOf(new TypeToken<Set<? extends Number>>() {}.type(), String.class));
	}

	@Test
	public void isArrayOf() {
		assertTrue( Types.isArrayOf(Integer[].class, Number.class));
		assertTrue( !Types.isArrayOf(Integer[].class, String.class));
		assertTrue( Types.isArrayOf(new TypeToken<List<? extends Integer>[]>() {}.type(), new TypeToken<Collection<Number>>(){}.type()));
		assertTrue( !Types.isArrayOf(new TypeToken<List<? extends Integer>[]>() {}.type(), new TypeToken<Collection<String>>(){}.type()));
	}

	@Test
	public void getParameterTypes() {
		Type[] parameterTypes = Types.getParameterTypes(List.class);
		assertTrue( parameterTypes.length==1 );
		assertTrue( parameterTypes[0].equals(Object.class) );				

		parameterTypes = Types.getParameterTypes(new TypeToken<List<String>>() {}.type());
		assertTrue( parameterTypes.length==1 );
		assertTrue( parameterTypes[0].equals(String.class) );		

		parameterTypes = Types.getParameterTypes(new TypeToken<List<? extends String>>() {}.type());
		assertTrue( parameterTypes.length==1 );
		assertTrue( parameterTypes[0].equals(String.class) );		
		
		parameterTypes = Types.getParameterTypes(new TypeToken<List<? super String>>() {}.type());
		assertTrue( parameterTypes.length==1 );
		assertTrue( parameterTypes[0].equals(String.class) );		

	}

}
