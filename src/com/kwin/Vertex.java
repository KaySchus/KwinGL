package com.kwin;

public class Vertex {
	private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
	private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
	private float[] st = new float[] { 0f, 0f };
	
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
	
	public void setXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1f);
	}
	
	public void setXYZW(float x, float y, float z, float w) {
		this.xyzw = new float[] { x, y, z, w };
	}
	
	public void setRGB(float r, float g, float b) {
		this.setRGBA(r, g, b, 1f);
	}
	
	public void setST(float s, float t) {
		this.st = new float[] { s, t };
	}
	
	public void setRGBA(float r, float g, float b, float a) {
		this.rgba = new float[] { r, g, b, a };
	}
	
	public float[] getXYZ() {
		return new float[] { xyzw[0], xyzw[1], xyzw[2] };
	}
	
	public float[] getXYZW() {
		return new float[] { xyzw[0], xyzw[1], xyzw[2], xyzw[3] };
	}
	
	public float[] getRGBA() {
		return new float[] { rgba[0], rgba[1], rgba[2], rgba[3] };
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
		
		out[4] = this.rgba[0];
		out[5] = this.rgba[1];
		out[6] = this.rgba[2];
		out[7] = this.rgba[3];
		
		out[8] = this.st[0];
		out[9] = this.st[1];
		
		return out;
	}
}
