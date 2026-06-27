// Custom settings mapped from your Java/Nsight data
const int armCount = 5;
const float speed = 0.25;
const int numColors = 5;

// Your color palette normalized to 0.0 - 1.0 (divided by 255)
const vec3 colors[5] = vec3[](
    vec3(91.0/255.0, 207.0/255.0, 249.0/255.0),  // Light Blue
    vec3(245.0/255.0, 170.0/255.0, 185.0/255.0), // Pink
    vec3(255.0/255.0, 255.0/255.0, 255.0/255.0), // White
    vec3(245.0/255.0, 170.0/255.0, 185.0/255.0), // Pink
    vec3(91.0/255.0, 207.0/255.0, 249.0/255.0)   // Light Blue
);

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    // Normalized device coordinates (from -0.5 to 0.5) corrected for aspect ratio
    vec2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;

    // Optional: Sample your texture (iChannel0) if you have one bound in ShaderToy
    // ShaderToy UVs traditionally range from 0.0 to 1.0 for textures
    vec2 texUV = fragCoord / iResolution.xy;
    vec4 texColor = texture(iChannel0, texUV);

    float radius = length(uv);
    float angle = atan(uv.y, uv.x);

    // Dynamic rotation over time
    angle += iTime * speed;

    // Calculate color indices matching your original radial logic
    float colorIndex = mod(angle * float(armCount) / (3.14159265359 * 2.0), float(numColors));
    
    // Ensure negative angles handle mod smoothly
    if (colorIndex < 0.0) colorIndex += float(numColors);

    int idx1 = int(floor(colorIndex));
    int idx2 = idx1 + 1;
    if (idx2 >= numColors) idx2 = 0;
    float frac = fract(colorIndex);

    // Blend the colors
    vec3 spiralColor = mix(colors[idx1], colors[idx2], frac);
    
    // Darken towards the edges
    spiralColor *= (1.0 - radius * 0.6);

    vec3 finalRGB = spiralColor;
    
    // If you aren't using a texture channel, fallback to 1.0 alpha
    float finalAlpha = (iResolution.z > 0.0) ? texColor.a : 1.0; 

    // Output to screen
    fragColor = vec4(finalRGB, finalAlpha);
}