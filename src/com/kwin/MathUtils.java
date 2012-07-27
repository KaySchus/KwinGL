package com.kwin;

public class MathUtils {
	public final double PI = 3.14159265358979323846;
	
	public double cot(double rads) {
		return (1 / Math.tan(rads));
	}
	
	public double degToRad(double angle) {
		return (angle * (PI / 180));
	}
}
