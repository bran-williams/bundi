# Bundi Engine

OpenGL rendering engine written in Java. 

## Features

* Entity-Component-System
* Deferred rendering
* Simple text rendering 

## Shading models
* Physically based
* Phong
* Blinn-Phong

### Screenshots
#### Quadtree Demo
![quadtree-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/quadtree-demo.png)

#### Blinn-Phong Lighting
![dragon-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/dragon-demo.png)

#### Perlin Noise terrain generation
![terrain-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/terrain-demo.png)

#### Deferred rendering pipeline
![blinn-deferred-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/blinn-deferred-demo.png)

#
### Dependencies

* [lwjgl](https://github.com/LWJGL/lwjgl3) - OpenGL & OpenAL & STB
* [glfw](http://www.glfw.org/) - Cross Platform Window API for OpenGL
* [joml](https://github.com/JOML-CI/JOML) - Java OpenGL math library (for linear algebra operations)
* [assimp](http://www.assimp.org/) - Open-Asset-Importer-Lib (for 3D models)
* [reflections](https://github.com/ronmamo/reflections) - Java runtime metadata analysis tool (loading from classpaths)
* [gson](https://github.com/google/gson) - Json serialization/deserialization
* [PngDecoder](http://twl.l33tlabs.org/) - Loading PNGs

#
### Resources used