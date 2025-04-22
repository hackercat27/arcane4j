#version 330 core

layout(location = 0) in vec3 position;

uniform mat4 transform;
uniform mat4 camera;
uniform mat4 projection;

void main()
{
	gl_Position = projection * camera * transform * vec4(position, 1.0);
}
