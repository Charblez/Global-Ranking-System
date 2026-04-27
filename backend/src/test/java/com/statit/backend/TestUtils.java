/**
 * Filename: TestUtils.java
 * Description: Helpers for unit tests - reflective field setters for JPA-managed fields.
 */
package com.statit.backend;

import java.lang.reflect.Field;

public final class TestUtils
{
    private TestUtils() {}

    public static void setField(Object target, String fieldName, Object value)
    {
        try
        {
            Class<?> clazz = target.getClass();
            Field field = null;
            while(clazz != null)
            {
                try
                {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                }
                catch(NoSuchFieldException e)
                {
                    clazz = clazz.getSuperclass();
                }
            }
            if(field == null)
            {
                throw new IllegalArgumentException("No such field: " + fieldName);
            }
            field.setAccessible(true);
            field.set(target, value);
        }
        catch(IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
