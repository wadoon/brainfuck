package weigl.bf;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class test {
	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Class<?> c = test.class.getClassLoader().loadClass("Code");
		for (Field m : c.getFields()){
			System.out.println(m);
		}

		Method m = c.getMethod("checkLoop", null);
		Field f = c.getField("memory");
		int i  = c.getField("pointer").getInt(null);
		
		invokeCheckLoop(m);
		increaseMemory(i, f,'0');
		invokeCheckLoop(m);
		increaseMemory(i,f,'1');
		invokeCheckLoop(m);
		increaseMemory(i,f,'2');

	}

	private static void increaseMemory(int i, Field f, char c) throws IllegalArgumentException, IllegalAccessException {
		char[] a = (char[])f.get(null);
		a[i]=c;
		System.out.println(a[a.length/2]);
	}

	private static void invokeCheckLoop(Method m) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		System.out.println(m.invoke(null));
	}
}
