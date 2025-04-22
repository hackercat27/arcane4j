#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureUV;

out vec2 pass_TextureUV;

uniform mat4 transform;
uniform mat4 camera;
uniform mat4 projection;

void main() {
    pass_TextureUV = textureUV;

    gl_Position = projection * camera * transform * vec4(position, 1.0);
}