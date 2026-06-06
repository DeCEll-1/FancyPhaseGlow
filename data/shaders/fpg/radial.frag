#version 120

uniform sampler2D textureSampler;
uniform float time;
uniform int armCount;
uniform float speed;
// uniform float thickness;

uniform vec3 colors[16];
uniform int numColors;

uniform float texWidth;
uniform float texHeight;

void main()
{
    vec2 offset = vec2(texWidth, texHeight);
    vec2 coordinateOffset1 = vec2(0.5 * offset.x, 0.5 * offset.y);
    vec2 uv = (gl_TexCoord[0].st) - coordinateOffset1;
    vec4 texColor = texture2D(textureSampler, gl_TexCoord[0].st);

    float radius = length(uv);
    float angle = atan(uv.y, uv.x);

    angle += time * speed;

    float colorIndex = mod(angle * float(armCount) / (3.14159 * 2.0), float(numColors));
    int idx1 = int(floor(colorIndex));
    int idx2 = idx1 + 1;
    if (idx2 >= numColors) idx2 = 0;
    float frac = fract(colorIndex);

    vec3 spiralColor = mix(colors[idx1], colors[idx2], frac);
    spiralColor *= (1.0 - radius * 0.6);

    vec3 finalRGB = spiralColor;
    float finalAlpha = texColor.a;

    gl_FragColor = vec4(finalRGB * gl_Color.rgb, finalAlpha * gl_Color.a);
}
