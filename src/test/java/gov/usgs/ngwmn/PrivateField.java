package gov.usgs.ngwmn;


import static org.junit.Assert.*;

import java.lang.reflect.Field;


public abstract class PrivateField {

	
	
	public static double getDouble(Object o, String fieldName) {
		Object value = getPrivateField(o, fieldName);
		assertDouble(o, fieldName, value);
		return (Double)value;
	}
	
	public static int getInteger(Object o, String fieldName) {
		Object value = getPrivateField(o, fieldName);
		assertInteger(o, fieldName, value);
		return (Integer)value;
	}
	
	public static String getString(Object o, String fieldName) {
		Object value = getPrivateField(o, fieldName);
		assertString(o, fieldName, value);
		return (String)value;
	}
	
	public static boolean getBoolean(Object o, String fieldName) {
		Object value = getPrivateField(o, fieldName);
		assertBoolean(o, fieldName, value);
		return (Boolean)value;
	}
	
	
	public static Object getPrivateField(Object o, String fieldName) {
		/* Check we have valid arguments */
		assertNotNull(o);
		assertNotNull(fieldName);
		/* Go and find the private field... */
		Field field = getPrivate(o.getClass(), fieldName);
		try {
			field.setAccessible(true);
			return field.get(o);
		} catch (IllegalAccessException ex) {
			fail("IllegalAccessException accessing " + fieldName);
		}
		return null;
	}
	
	private static Field getPrivate(Class<?> clazz, String fieldName) {
		/* Go and find the private field... */
		final Field fields[] = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
					return fields[i];
			}
		}
	    if (clazz.getSuperclass() != null) {
	        return getPrivate(clazz.getSuperclass(), fieldName);
	    }
		fail("Field '" + fieldName + "' not found on class" + clazz.getName());
		return null;
	}
	
	public static void assertString(Object o, String fieldName, Object value) {
		assertType(String.class, o, fieldName, value);
	}
	public static void assertBoolean(Object o, String fieldName, Object value) {
		assertType(Boolean.class, o, fieldName, value);
	}
	public static void assertCharacter(Object o, String fieldName, Object value) {
		assertType(Character.class, o, fieldName, value);
	}
	public static void assertNumber(Object o, String fieldName, Object value) {
		assertType(Number.class, o, fieldName, value);
	}
	public static void assertInteger(Object o, String fieldName, Object value) {
		assertType(Integer.class, o, fieldName, value);
	}
	public static void assertDouble(Object o, String fieldName, Object value) {
		assertType(Double.class, o, fieldName, value);
	}
	public static void assertLong(Object o, String fieldName, Object value) {
		assertType(Long.class, o, fieldName, value);
	}
	private static void assertType(Class<?> type, Object o, String fieldName, Object value) {
		assertTrue("Value of "+fieldName+" from class "+o.getClass().getName()
				+" is not "+type, value.getClass().isAssignableFrom(type));
	}
}
