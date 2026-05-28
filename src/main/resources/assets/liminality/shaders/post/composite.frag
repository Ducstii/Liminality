#include frex:shaders/api/header.glsl
#include canvas:shaders/pipeline/pipeline.glsl
#include frex:shaders/api/world.glsl

/******************************************************
  liminality:shaders/post/composite.frag
  Backrooms post-processing composite:
  - Fluorescent yellow color grading
  - Vignette (darkened edges)
  - Animated film grain
******************************************************/

uniform sampler2D _cvu_base;

in vec2 _cvv_texcoord;
out vec4 fragColor;

void main() {
    vec2 uv = _cvv_texcoord;
    vec3 c = texture(_cvu_base, uv).rgb;

    // Fluorescent yellow tint - warm highlights, cool shadows pulled yellow
    c.r *= 1.08;
    c.g *= 1.04;
    c.b *= 0.82;

    // Slight contrast boost
    c = (c - 0.5) * 1.05 + 0.5;

    // Vignette - darkens edges and corners
    vec2 vigUV = uv * 2.0 - 1.0;
    float vig = 1.0 - dot(vigUV, vigUV) * 0.40;
    c *= clamp(vig, 0.0, 1.0);

    // Film grain - hash noise animating at 24fps
    vec2 noiseUV = floor(uv * vec2(800.0, 450.0));
    float frame = floor(frx_renderSeconds * 24.0);
    float n = fract(sin(dot(noiseUV + frame, vec2(127.1, 311.7))) * 43758.5453);
    c += (n - 0.5) * 0.022;

    fragColor = vec4(clamp(c, 0.0, 1.0), 1.0);
}