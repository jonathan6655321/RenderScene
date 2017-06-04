Hey Dear BODEK! 

welcome to our bonus. 

it is comprised of two core features: 

1. We recognized that using triangles alone you are able to render almost any object.
we studied 3D image formats and wrote a program to convert .obj files into the format used in our exercise.
Thus - we are able to render any 3D object!

attached is an example of a 3D yoda image we rendered. yoda is comprised of 600+ triangles!
(see yodaStillImage1 and 2)

2. The second part of our project is creating an animation from a series of still images. 
 the first challenge was writing a program to automatically generate a series of scenes from one scene. 

we invented an algorithm in order to ensure that the looking point is always on the object of interest, and in order
to ensure it is completely in sight. 

then we generated a series of stills where the camera does a 360 rotation around the object. 

the result is attached as an mp4 file named: theYodaFilm



few notes:

1. USAGE:
	<folder_path> <obj_file_name> <scene_txt_file_name> <dest_file_name>
	Example:
	"C:\rayTracing" "yoda.obj" "tempTXTfile.txt" "yoda.mov"

2. defult_values:
	the current program creates animation file with 100 frames in 400x400 resulution, which can be change manualy from the source code:
		width = 400 pixels.
		height = 400 pixels.
		superSampeling = 1.
		rootNumberOfShadowRay = 1.
		recursionDepth = 2.
		numberOfFrames = 100 frames.
		shadowIntencity = 60%.
		frame_rate = 24 fps.

3. binary search tree:
	we noticed that with so many triangles on the scene, we get very slow result for our ray tracing renderer.
	to solve this we implement a "lazy binary search tree" called RenderableBinarySearchObject:
		each scene has ONE RenderableBinarySearchObject which contains all the element in the scene which are not infinite.
		the binary search tree contain center point and radius and act as a sphere that contain all the elements 
			and checks for collision with it's object only if it's sphere contain a collision with the ray.
		each RenderableBinarySearchObject contains 3 RenderableBinarySearchObject.
		it choose randomly 3 object it contains and split all of it's object to 3 groups based on their distance from those 3 objects.
		
4. gifs:
	we implemented a gif output as well, but the colors got very messy because of the 256 colors limit in the gif format.
	it can still be used by changing the USE_GIF_FOR_MOVIE boolean in Main.java to true :)
	
ENJOY!!!! :D