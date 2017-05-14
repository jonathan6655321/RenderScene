package RenderScene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SceneParser {
	final static boolean LOG = true;
	/**
	 * Parses the scene file and creates the scene. Change this function so it
	 * generates the required objects.
	 * 
	 * @throws FileNotFoundException
	 */
	public static Scene getSceneFromTextFile(String pathToTextFile) throws IOException, RayTracerException {
		BufferedReader reader = new BufferedReader(new FileReader(pathToTextFile));
		String currentObjectString = null;
		int lineNum = 0;
		
		ParserableObject setParserData = null;
		ParserableObject cameraParserData = null;
		ArrayList<ParserableObject> materialsParserArray = new ArrayList<>();
		ArrayList<ParserableObject> renderableObjectParserArray = new ArrayList<>();
		ArrayList<ParserableObject> lightSourceParserArray = new ArrayList<>();
		
		if (LOG)
			System.out.println("Started parsing scene file " + pathToTextFile);

		while ((currentObjectString = reader.readLine()) != null) {
			currentObjectString = currentObjectString.trim();
			++lineNum;

			if (currentObjectString.isEmpty() || (currentObjectString.charAt(0) == '#')) { // comment																	// comment
				continue;
			} else {
				String currentObjectType = currentObjectString.substring(0, 3).toLowerCase();
				String[] currentObjectParams = currentObjectString.substring(3).trim().toLowerCase().split("\\s+");

				if (currentObjectType.equals("cam")) {
					cameraParserData = new ParserableObject(currentObjectType, currentObjectParams);
				} else if (currentObjectType.equals("set")) {
					setParserData = new ParserableObject(currentObjectType, currentObjectParams);
				} else if (currentObjectType.equals("mtl")) {
					materialsParserArray.add( new ParserableObject(currentObjectType, currentObjectParams));
				} else if (currentObjectType.equals("sph") || currentObjectType.equals("pln") || currentObjectType.equals("trg")) {
					renderableObjectParserArray.add( new ParserableObject(currentObjectType, currentObjectParams));
				} else if (currentObjectType.equals("lgt")) {
					lightSourceParserArray.add( new ParserableObject(currentObjectType, currentObjectParams));
				} else {
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", currentObjectType, lineNum));
					continue;
				}
				if (LOG)
					System.out.println(String.format("Parsed %s (line %d)", currentObjectType, lineNum));
			}
		}
		reader.close();
		
		if(setParserData == null){
			System.out.println("Missing Set Data");
		}else if(cameraParserData == null){
			System.out.println("Missing Set Data");
		}
		Scene scene = generateSceneWithSetData(setParserData.objectParams);
		
		ArrayList<Material> MaterialsArray = new ArrayList<>();
		for(ParserableObject parserData: materialsParserArray){
			MaterialsArray.add(generateMaterial(parserData.objectParams));
		}

		System.out.println("Finished parsing scene file " + pathToTextFile);
		
		return scene;
	}

	private static Scene generateSceneWithSetData(String[] setParams){
		byte red, green, blue;
		red = Byte.parseByte(setParams[0]);
		green = Byte.parseByte(setParams[1]);
		blue = Byte.parseByte(setParams[2]);
		Color backgroundColor = new Color(red, green,blue);
		
		int rootNumberOfShadowRay = Integer.parseInt(setParams[3]);
		int maximumNumberOfRecurtsions = Integer.parseInt(setParams[4]);
		int superSampling = Integer.parseInt(setParams[5]);
		
		return new Scene(backgroundColor, rootNumberOfShadowRay, maximumNumberOfRecurtsions, superSampling);
	}
	
	private static Camera generateCamera(String[] cameraParams){
		//Vector3D cameraPosition = 
	}

	
	private static Material generateMaterial(String[] materialParams){
		byte red, green, blue;
		red = Byte.parseByte(materialParams[0]);
		green = Byte.parseByte(materialParams[1]);
		blue = Byte.parseByte(materialParams[2]);
		Color diffuseColor = new Color(red, green,blue);
		
		red = Byte.parseByte(materialParams[3]);
		green = Byte.parseByte(materialParams[4]);
		blue = Byte.parseByte(materialParams[5]);
		Color specularColor = new Color(red, green,blue);

		red = Byte.parseByte(materialParams[6]);
		green = Byte.parseByte(materialParams[7]);
		blue = Byte.parseByte(materialParams[8]);
		Color reflectionColor = new Color(red, green,blue);

		int phongSpecularity = Integer.parseInt(materialParams[9]);
		double transperancy = Double.parseDouble(materialParams[10]);
		return new Material(diffuseColor, specularColor, reflectionColor, phongSpecularity, transperancy);
	}
	
	private static class ParserableObject {
		public String objectType;
		public String[] objectParams;
		public ParserableObject(String objectType, String[] objectParams){
			this.objectType = objectType;
			this.objectParams = objectParams;
		}
	}
	

}
