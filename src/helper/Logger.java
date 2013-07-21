package helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import core.Tuonela;

public class Logger {

	private static final SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss a");
	private static Map<LogType, Boolean> logMap = new HashMap<LogType, Boolean>();

	public static void log(LogType type, Object msg) {
		if(logMap.get(type))
			System.out.println(Tuonela.instanceID + " " + ft.format(new Date()) + "	" +Thread.currentThread().getStackTrace()[2].getClassName() + "	" + msg);
	}

	public static void logf(LogType type, Object msg, Object... pars) {
		if(logMap.get(type))
			System.out.printf(Tuonela.instanceID + " " + ft.format(new Date()) + " " +Thread.currentThread().getStackTrace()[2].getClassName() + " " + msg, pars);
	}

	public static void logErr(LogType type, Object msg) {
		if(logMap.get(type))
			System.err.println(Tuonela.instanceID + " " + ft.format(new Date()) + " " +Thread.currentThread().getStackTrace()[2].getClassName() + " " + msg);
	}

	public static void log(Object msg) {
		System.out.println(Tuonela.instanceID + " " + ft.format(new Date()) + " " +Thread.currentThread().getStackTrace()[2].getClassName() + " " + msg);
	}

	static {
		logMap.put(LogType.GENERAL, true);

		logMap.put(LogType.LOG_PACKET, true);
		logMap.put(LogType.LOG_PACKET_SENDING, false);
		logMap.put(LogType.LOG_PACKET_RECEIVING, false);
		logMap.put(LogType.LOG_PACKET_VARS, true);
	}

}
