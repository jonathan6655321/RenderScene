package RenderScene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SceneParser {
	/**
	 * Parses the scene file and creates the scene. Change this function so it generates the required objects.
	 * @throws FileNotFoundException 
	 */
	public static Scene getSceneFromTextFile(String pathToTextFile) throws IOException, RayTracerException
	{
			FileReader fileReader = new FileReader(pathToTextFile);

			BufferedReader bufferReader = new BufferedReader(fileReader);
			String line = null;
			int lineNum = 0;
			System.out.println("Started parsing scene file " + pathToTextFile);
			
			Scene scene = new Scene();

			while ((line = bufferReader.readLine()) != null)
			{
				line = line.trim();
				++lineNum;

				if (line.isEmpty() || (line.charAt(0) == '#'))
				{  // This line in the scene file is a comment
					continue;
				}
				else
				{
					String code = line.substring(0, 3).toLowerCase();
					// Split according to white space characters:
					String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

					if (code.equals("cam"))
					{
	                                        // Add code here to parse camera parameters

						System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
					}
					else if (code.equals("set"))
					{
	                                        // Add code here to parse general settings parameters

						System.out.println(String.format("Parsed general settings (line %d)", lineNum));
					}
					else if (code.equals("mtl"))
					{
	                                        // Add code here to parse material parameters

						System.out.println(String.format("Parsed material (line %d)", lineNum));
					}
					else if (code.equals("sph"))
					{
	                                        // Add code here to parse sphere parameters

	                                        // Example (you can implement this in many different ways!):
						                    // Sphere sphere = new Sphere();
	                                        // sphere.setCenter(params[0], params[1], params[2]);
	                                        // sphere.setRadius(params[3]);
	                                        // sphere.setMaterial(params[4]);

						System.out.println(String.format("Parsed sphere (line %d)", lineNum));
					}
					else if (code.equals("pln"))
					{
	                                        // Add code here to parse plane parameters

						System.out.println(String.format("Parsed plane (line %d)", lineNum));
					}
					else if (code.equals("trg"))
					{
	                                        // Add code here to parse cylinder parameters

						System.out.println(String.format("Parsed cylinder (line %d)", lineNum));
					}
					else if (code.equals("lgt"))
					{
	                                        // Add code here to parse light parameters

						System.out.println(String.format("Parsed light (line %d)", lineNum));
					}
					else
					{
						System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
					}
				}
			}
			bufferReader.close();
			return scene;
	}
	
}
