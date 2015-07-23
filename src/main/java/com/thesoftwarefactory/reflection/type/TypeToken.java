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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 
 * @author <a href="mailto:stephane.bastian.dev@gmail.com">Stephane Bastian</a>
 *
 * @param <T>
 */
public class TypeToken<T> {

    private final Type type;
    
    protected TypeToken() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        type = superclass.getActualTypeArguments()[0];
    }
    
    @SuppressWarnings("rawtypes")
	@Override 
    public boolean equals (Object o) {
        return o instanceof TypeToken &&
            ((TypeToken)o).type.equals(type);
    }
    
    @Override 
    public int hashCode() {
        return type.hashCode();
    }
	
    public Type type() {
    	return type;
    }
    
}
