package com.kwin.util;

import com.kwin.graphics.Color;

public class Vertex {
	private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
	private float[] st = new float[] { 0f, 0f };
	
	private Color color;
	
	public static final int elementBytes = 4;
	
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;
	
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorByteCount = colorElementCount * elementBytes;
	public static final int textureByteCount = textureElementCount * elementBytes;
	
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int textureByteOffset = colorByteOffset + colorByteCount;
	
	public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;
	public static final int stride = positionBytesCount + colorByteCount + textureByteCount;
	
	public static final int sizeInBytes = elementBytes * elementCount;
	
	public Vertex(float x, float y, float z, float s, float t) {
		this(x, y, z, 1f, s, t, Color.WHITE);
	}
	
	public Vertex(float x, float y, float z, float w, float s, float t, Color c) {
		this.setXYZW(x, y, z, w);
		this.setST(s, t);
		this.setColor(c);
	}
	
	public Vertex() {
	}
	
	public void setXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1f);
	}
	
	public void setXYZW(float x, float y, float z, float w) {
		this.xyzw = new float[] { x, y, z, w };
	}
	
	public void setST(float s, float t) {
		this.st = new float[] { s, t };
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public float[] getXYZ() {
		return new float[] { xyzw[0], xyzw[1], xyzw[2] };
	}
	
	public float[] getXYZW() {
		return new float[] { xyzw[0], xyzw[1], xyzw[2], xyzw[3] };
	}
	
	public Color getColor() {
		return color;
	}
	
	public float[] getST() {
		return new float[] { st[0], st[1] };
	}
	
	public float[] getElements() {
		float[] out = new float[elementCount];
		
		out[0] = this.xyzw[0];
		out[1] = this.xyzw[1];
		out[2] = this.xyzw[2];
		out[3] = this.xyzw[3];
		
		out[4] = this.color.red();
		out[5] = this.color.green();
		out[6] = this.color.blue();
		out[7] = this.color.alpha();
		
		out[8] = this.st[0];
		out[9] = this.st[1];
		
		return out;
	}
}
