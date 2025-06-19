#version 330 core

in vec4 out_Color;
in vec2 out_Texture;
in float out_CornerRadius;
out vec4 fragColor;

void main()
{
    vec2 p = (out_Texture - vec2(0.5)) * 2;
    vec2 halfSize = vec2(1);

    vec2 corner = halfSize - vec2(out_CornerRadius);

    vec2 d = abs(p) - corner;
    float dist = length(max(d, 0.0)) - out_CornerRadius;

    if (dist > 0.0) {
        discard;
    }
    fragColor = out_Color;
//    float aa = fwidth(dist);
//    float alpha = 1.0 - smoothstep(0.0, aa, dist);
//
//    fragColor = vec4(out_Color.rgb, out_Color.a * alpha);
}