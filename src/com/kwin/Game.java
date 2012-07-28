package com.kwin;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Game {
	
	private TextureManager tManager = new TextureManager();
	private ShaderManager sManager = new ShaderManager();
	
	private int vaoID, vboID, vboiID, indicesCount, vsID, fsID, pID, projectionMatrixLocation, viewMatrixLocation, modelMatrixLocation;
	private int[] texIDs = new int[2];
	
	private int texSelector = 0;
	private Vertex[] vertices = new Vertex[4];
	
	private long lastFrame = 0;
	public float totalTime = 0.0f;
	
	private ByteBuffer verticesByteBuffer = null;
	
	private Log log = Log.getInstance();
	
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	private Vector3f modelPos = null;
	private Vector3f modelAngle = null;
	private Vector3f modelScale = null;
	private Vector3f cameraPos = null;
	private FloatBuffer matrix44Buffer = null;
	
	private int dWidth, dHeight;
	
	public void Initialize(int width, int height) {
		dWidth = width;
		dHeight = height;
		
		getDelta();
		
		log.write("\n");
		log.write("Beginning Initialization.... ");
		
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 1.0f);
		
		setupQuad();
		setupShaders();
		setupTextures();
		setupMatrices();
		
		int setupTime = getDelta();
		log.write("Setup Time - " + setupTime + "ms");	
	}
	
	public void setupQuad() {
		Vertex v0 = new Vertex(); v0.setXYZ(-0.5f, 0.5f, 0f); v0.setRGB(1, 0, 0); v0.setST(0, 0);
		Vertex v1 = new Vertex(); v1.setXYZ(-0.5f, -0.5f, 0f); v1.setRGB(0, 1, 0); v1.setST(0, 1);
		Vertex v2 = new Vertex(); v2.setXYZ(0.5f, -0.5f, 0f); v2.setRGB(0, 0, 1); v2.setST(1, 1);
		Vertex v3 = new Vertex(); v3.setXYZ(0.5f, 0.5f, 0f); v3.setRGB(1, 1, 1); v3.setST(1, 0);
		
		vertices = new Vertex[] {v0, v1, v2, v3};

		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * Vertex.stride);				
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();
		
		byte[] indices = { 0, 1, 2,
				           2, 3, 0 };
		
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		log.write("Created, stored, and flipped Vertices, Color, and Indices Buffers respectively.");
		
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(0, Vertex.positionElementCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.positionByteOffset);
		GL20.glVertexAttribPointer(1, Vertex.colorElementCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.colorByteOffset);
		GL20.glVertexAttribPointer(2, Vertex.textureElementCount, GL11.GL_FLOAT, false, Vertex.stride, Vertex.textureByteOffset);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		
		vboiID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		log.write("Created one large VAO storing independent VBO's for Vertices and Indices. Interleaved!");
		
		modelPos = new Vector3f(0, 0, 0);
		modelAngle = new Vector3f(0, 0, 0);
		modelScale = new Vector3f(1, 1, 1);
		cameraPos = new Vector3f(0, 0, 0);
	}
	
	public void setupShaders() {
		int errorCheck = GL11.glGetError();
		
		vsID = sManager.loadShader("src/com/kwin/vertex.vert", GL20.GL_VERTEX_SHADER);
		fsID = sManager.loadShader("src/com/kwin/fragment.frag", GL20.GL_FRAGMENT_SHADER);
		
		log.write("Succesfully loaded vertex and fragment shaders.");
		
		pID = GL20.glCreateProgram();
		GL20.glAttachShader(pID, vsID);
		GL20.glAttachShader(pID, fsID);
		GL20.glLinkProgram(pID);
		
		log.write("Linking shaders.....");
		
		GL20.glBindAttribLocation(pID, 0, "in_Position");
		GL20.glBindAttribLocation(pID, 1, "in_Color");
		GL20.glBindAttribLocation(pID,  2, "in_TextureCoord");
		
		log.write("Binding shader variables...");
		
		projectionMatrixLocation = GL20.glGetUniformLocation(pID, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(pID, "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(pID, "modelMatrix");
		
		GL20.glValidateProgram(pID);
		
		log.write("Validated.");
		
		errorCheck = GL11.glGetError();
		if (errorCheck != GL11.GL_NO_ERROR) {
			log.write("Error - Could not create the shaders : " + GLU.gluErrorString(errorCheck));
			System.exit(-1);
		}
	}
	
	public void setupTextures() {
		texIDs[0] = tManager.loadTexture("Assets/stGrid1.png", GL13.GL_TEXTURE0);
		texIDs[1] = tManager.loadTexture("Assets/stGrid2.png", GL13.GL_TEXTURE0);
	}
	
	public void setupMatrices() {
		projectionMatrix = new Matrix4f();
		
		float fieldOfView = 60f;
		float aspectRatio = (float) dWidth / (float) dHeight;
		float nearPlane = 0.1f;
		float farPlane = 100f;
		
		float yScale = MathUtils.cot(MathUtils.degToRad(fieldOfView / 2f));
		float xScale = yScale / aspectRatio;
		
		float frustrumLength = farPlane - nearPlane;

		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((farPlane + nearPlane) / frustrumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustrumLength);
		
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void Update() {
		
		float scaleFactor = 0.01f;
		
		while(Keyboard.next()) {
			if (!Keyboard.getEventKeyState()) continue;

			switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_1:
					texSelector = 0;
					break;
				case Keyboard.KEY_2:
					texSelector = 1;
					break;
			}
			
			switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_RETURN:
					modelScale = new Vector3f(1, 1, 1);
					modelPos = new Vector3f(0, 0, 0);
					modelAngle = new Vector3f(0, 0, 0);
					break;
			
				case Keyboard.KEY_NUMPAD8:
					modelScale.y = modelScale.y + scaleFactor;
					break;
				case Keyboard.KEY_NUMPAD2:
					modelScale.y = modelScale.y - scaleFactor;
					break;
				case Keyboard.KEY_NUMPAD4:
					modelScale.x = modelScale.x - scaleFactor;
					break;
				case Keyboard.KEY_NUMPAD6:
					modelScale.x = modelScale.x + scaleFactor;
					break;
					
			
				case Keyboard.KEY_UP:
					modelPos.y = modelPos.y + scaleFactor;
					break;
				case Keyboard.KEY_DOWN:
					modelPos.y = modelPos.y - scaleFactor;
					break;
				case Keyboard.KEY_LEFT:
					modelPos.x = modelPos.x - scaleFactor;
					break;
				case Keyboard.KEY_RIGHT:
					modelPos.x = modelPos.x + scaleFactor;
					break;	
			}
		}
		
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		
		Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
		Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
		Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
		Matrix4f.rotate(MathUtils.degToRad(modelAngle.z), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.rotate(MathUtils.degToRad(modelAngle.y), new Vector3f(0, 1, 0), modelMatrix, modelMatrix);
		Matrix4f.rotate(MathUtils.degToRad(modelAngle.x), new Vector3f(1, 0, 0), modelMatrix, modelMatrix);
		
		GL20.glUseProgram(pID);
		
		projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
		viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
		
		GL20.glUseProgram(0);
	}
	
	public void Render() {
		int delta = getDelta();
		float timePassed = delta / 1000.0f;
		totalTime += timePassed;
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL20.glUseProgram(pID);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIDs[texSelector]);
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		GL20.glUseProgram(0);
	}
	
	public void Dispose() {
		GL11.glDeleteTextures(texIDs[0]);
		GL11.glDeleteTextures(texIDs[1]);
		
		GL20.glUseProgram(0);
		GL20.glDetachShader(pID, vsID);
		GL20.glDetachShader(pID, fsID);
		
		GL20.glDeleteShader(vsID);
		GL20.glDeleteShader(fsID);
		GL20.glDeleteProgram(pID);
		
		GL30.glBindVertexArray(vaoID);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboID);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiID);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoID);
		
		System.out.println("Total Time - " + totalTime + "s");
	}
	
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		
		return delta;
	}
}
