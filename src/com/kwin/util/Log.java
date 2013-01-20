package com.kwin.util;

import java.io.*;

public class Log {
	private static Log instance = null;
	
	private File file;
	private Writer output = null;
	
	private boolean commandLog = true;
	
	public static Log getInstance() {
		if (instance == null) 
			instance = new Log();
		
		return instance;
	}
	
	public void enableCommandPromptLogging() {
		commandLog = true;
	}
	
	public void disableCommandPromptLogging() {
		commandLog = false;
	}
	
	protected Log() {
		file = new File("log.txt");
		
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			System.out.println("Unable to create log file.  Continuing onwards.");
			e.printStackTrace();
		}
	}
	
	public void write(String str) {
		try {
			output.write(str + "\n");
			if (commandLog) System.out.println(str);
		} catch (IOException e) {
			System.out.println("Error writing content string to disk.  Continuing.");
			e.printStackTrace();
		}
	}
	
	public void flush() {
		try {
			output.close();
		} catch (IOException e) {
			System.out.println("Unable to flush log contents to disk.");
			e.printStackTrace();
		}
	}
}
