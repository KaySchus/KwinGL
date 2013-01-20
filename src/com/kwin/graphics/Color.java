package com.kwin.graphics;

public class Color {
	
	public static final Color TRANSPARENT = new Color(0.0f, 0.0f, 0.0f, 0.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	
	public float r;
	public float g;
	public float b;
	public float a;
	
	public Color(float red, float green, float blue, float alpha) {
		this.r = red; this.g = green;
		this.b = blue; this.a = alpha;
	}
	
	public Color(float red, float green, float blue) {
		this(red, green, blue, 1f);
	}
	
	public Color(int red, int green, int blue, int alpha) {
		this((float) red / 255.0f, (float) green / 255.0f, (float) blue / 255.0f, (float) alpha / 255.0f);
	}
	
	public Color(int red, int green, int blue) {
		this(red, green, blue, 255);
	}
	
	public int red() { return (int) (r * 255); }
	public int green() { return (int) (g * 255); }
	public int blue() { return (int) (b * 255); }
	public int alpha() { return (int) (a * 255); }
	
	public String toString() {
		return "Color (" + r +", " + g + ", " + b + ", " + a + ")";
	}
}
