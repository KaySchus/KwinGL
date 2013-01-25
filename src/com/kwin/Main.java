package com.kwin;

import com.kwin.display.DisplayContext;

public class Main {
	public static void main(String[] args) {
		DisplayContext context = DisplayContext.createInstance(1920, 1080, new Game());
		context.start();
	}
}
