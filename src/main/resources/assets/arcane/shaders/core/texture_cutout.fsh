#version 330 core

in vec2 pass_TextureUV;

out vec4 fragColor;

uniform sampler2D diffuseMap;

void main() {
    vec4 col = texture(diffuseMap, pass_TextureUV);
    if (col.a == 0.0) {
        discard;
    }
    fragColor = vec4(col.rgb, 1.0);
}

