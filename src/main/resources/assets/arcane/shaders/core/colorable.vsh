#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texture;

uniform mat4 transform;
uniform mat4 camera;
uniform mat4 projection;

uniform vec4 color;
uniform float cornerRadius;

out vec4 out_Color;
out float out_CornerRadius;
out vec2 out_Texture;

void main()
{
    out_Color = color;
    out_Texture = texture;
    out_CornerRadius = clamp(cornerRadius, 0.0, 1.0);

    gl_Position = projection * camera * transform * vec4(position, 1.0);
}
