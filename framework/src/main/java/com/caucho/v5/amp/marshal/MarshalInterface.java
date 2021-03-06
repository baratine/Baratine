/*
 * Copyright (c) 1998-2015 Caucho Technology -- all rights reserved
 *
 * This file is part of Baratine(TM)
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Baratine is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Baratine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Baratine; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.v5.amp.marshal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

import sun.misc.Unsafe;

/**
 * Marshals arguments and results from a module import. 
 */
class MarshalInterface implements ModuleMarshal
{
  private final Class<?> _sourceClass;
  private final Class<?> _targetClass;
  
  private RampImport _moduleImport;

  MarshalInterface(RampImport moduleImport,
              Class<?> sourceClass,
              Class<?> targetClass)
  {
    Objects.requireNonNull(sourceClass);
    Objects.requireNonNull(targetClass);
    
    if (targetClass.isPrimitive() || targetClass.isArray()) {
      throw new IllegalArgumentException(String.valueOf(targetClass));
    }
    
    if (! targetClass.isInterface()
        && ! Modifier.isAbstract(targetClass.getModifiers())) {
      throw new IllegalArgumentException(String.valueOf(targetClass));
    }
    
    _sourceClass = sourceClass;
    _targetClass = targetClass;
    
    _moduleImport = moduleImport;
  }
  
  @Override
  public boolean isValue()
  {
    return false;
  }
  
  @Override
  public Object convert(Object sourceValue)
  {
    if (sourceValue == null) {
      return null;
    }
    
    Class<?> sourceClass = sourceValue.getClass();
    
    if (sourceClass.equals(_sourceClass)) {
      throw new IllegalStateException(sourceClass.getName());
    }

    ModuleMarshal marshal;
      
    marshal = _moduleImport.marshal(sourceClass, _targetClass);
      // marshal = _moduleImport.marshal(sourceClass);
          
    Object targetValue = marshal.convert(sourceValue);
      
    return targetValue;
  }
}
