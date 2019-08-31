# Bundi Engine

OpenGL rendering engine written in Java. 

## Screenshots
#### Quadtree Demo
![quadtree-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/quadtree-demo.png)

#### Blinn-Phong Lighting
![dragon-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/dragon-demo.png)

#### Perlin Noise terrain generation
![terrain-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/terrain-demo.png)

#### Deferred rendering pipeline
![blinn-deferred-demo](https://raw.githubusercontent.com/bran-williams/test-engine/master/screenshots/blinn-deferred-demo.png)


## Usage
- Using this application requires cloning this repo and creating run configurations for the submodules.
- This project is built using gradle build scripts, so it may be necessary to import the gradle project into your IDE.

### Clone
- Clone this repository.

<pre>
git clone https://github.com/bran-williams/bundi.git
</pre>

### IntelliJ
- To run a submodule, it is essential to create a new run configuration.
    * New Run Configuration... -> Application

- Give it a name, set the main class to whatever main class is defined within the submodule
    * e.g. `com.branwilliams.bundi.voxel.VoxelMain`

- The launcher expects a launch configuration, so set the program arguments to a launch configuration.
    * e.g. `voxel.json`

- Set working directory to /run/

- Set module to the submodule of interest.
    * e.g. `voxel.main`

### Dependencies

* [lwjgl](https://github.com/LWJGL/lwjgl3) - OpenGL & OpenAL & STB
* [glfw](http://www.glfw.org/) - Cross Platform Window API for OpenGL
* [joml](https://github.com/JOML-CI/JOML) - Java OpenGL math library (for linear algebra operations)
* [assimp](http://www.assimp.org/) - Open-Asset-Importer-Lib (for 3D models)
* [reflections](https://github.com/ronmamo/reflections) - Java runtime metadata analysis tool (loading from classpaths)
* [gson](https://github.com/google/gson) - Json serialization/deserialization
* [PngDecoder](http://twl.l33tlabs.org/) - Loading PNGs

### Resources used
