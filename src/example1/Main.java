package example1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		
		  callLog(); 
		  
			Path myDir = Paths.get("C:/workspace/workspace2/classloader-examples/src");

			AutoCompileAndReLoad.registerRecursive(myDir);
		 

//		compileAndReload("C:/workspace/workspace2/java-memory/src/Greet.java",new MyClassLoader(Main.class.getClassLoader()),"Greet");
	}

	public  static void callLog() throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		MyClassLoader classLoader = new
		  MyClassLoader(Main.class.getClassLoader());
		  
		  Class cls = classLoader.loadClass("example1.Greet");
		  
		  Method method = cls.getMethod("log");
		  
		  
		  Object msg = method.invoke(null);
	}

	public static void compileAndReload(ClassLoader  classloader,String fullClassName,String... file) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, file);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Class cls =	classloader.loadClass(fullClassName);
			  
			  Method method = cls.getMethod("log");
			  
			  
			  Object msg = method.invoke(null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
