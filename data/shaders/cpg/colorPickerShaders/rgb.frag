#version 120

uniform vec3 currColor;

void main()
{ // the square will only allow changing of x and y components, so z is static
    vec2 uv = (gl_TexCoord[0].st);

    gl_FragColor = vec4(uv, currColor.z, 1.);
}
