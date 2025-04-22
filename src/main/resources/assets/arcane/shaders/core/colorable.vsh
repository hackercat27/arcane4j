#version 330 core

layout(location = 0) in vec3 position;

uniform mat4 transform;
uniform mat4 camera;
uniform mat4 projection;

uniform vec4 color;

out vec4 out_Color;

void main()
{
    out_Color = color;
    gl_Position = projection * camera * transform * vec4(position, 1.0);
}
