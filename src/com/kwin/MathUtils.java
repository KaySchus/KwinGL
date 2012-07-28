package com.kwin;

public class MathUtils {
	public static final double PI = 3.14159265358979323846;
	
	public static float cot(double rads) {
		return (float) (1 / Math.tan(rads));
	}
	
	public static float degToRad(double angle) {
		return (float) (angle * (PI / 180));
	}
}
