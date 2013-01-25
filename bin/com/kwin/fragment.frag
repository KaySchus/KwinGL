#version 150 core

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	vec4 tex_Color = texture2D(texture_diffuse, pass_TextureCoord);
	
	//out_Color = tex_Color;
	out_Color = vec4(vec3(1.0, 1.0, 1.0) - tex_Color.rgb, 1.0);
}