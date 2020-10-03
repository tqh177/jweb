package cn.tqhweb.jweb.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassKit {
	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static List<Class<?>> findClassesByFile(String packageName, String packagePath, final boolean recursive) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return classes;
		}
		File[] dirfiles = dir.listFiles((file) -> {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				classes.addAll(findClassesByFile(packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive));
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					classes.add(Class.forName(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}

	/**
	 * 
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * 
	 * @return
	 * 
	 */
	public static List<Class<?>> getClasses(String packageName) {
		// 第一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = ClassKit.class.getClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					classes.addAll(findClassesByFile(packageName, filePath, recursive));
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								boolean flag = true;
								if (idx != packageDirName.length()) {
									// 如果可以迭代下去 并且是一个包
									if (!recursive) {
										flag = false;
									}
								}
								if (flag && name.endsWith(".class")) {
									// 去掉后面的".class" 获取真正的类名
									String className = name.substring(0, name.length() - 6).replace('/', '.');
									try {
										// 添加到classes
										classes.add(Class.forName(className));
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	/** 通过方法名称查找public方法 */
	public static Method getDeclaredMethodByName(Class<?> clazz, String methodName) throws NoSuchMethodException {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName) && Modifier.isPublic(method.getModifiers())) {
				return method;
			}
		}
		throw new NoSuchMethodException();
	}

	/** 判断包是否存在 */
	public static boolean PackageExists(String packageName) {
		String packageDirName = packageName.replace('.', '/');
		try {
			Enumeration<URL> enumeration = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			if (enumeration != null && enumeration.hasMoreElements()) {
				return true;
			}
		} catch (IOException e) {
		}
		return false;

	}

	/** 判断类是否存在 */
	public static boolean ClassExists(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			return clazz != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
