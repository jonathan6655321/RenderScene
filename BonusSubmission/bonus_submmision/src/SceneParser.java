package RenderScene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

public class SceneParser {
	final static boolean LOG = false;
	final static String FRAME_SEPARATOR = "<FRAME_SPERATOR>";

	/**
	 * Parses the scene file and creates the scene. Change this function so it
	 * generates the required objects.
	 * 
	 * @throws FileNotFoundException
	 */
	public static Scene[] getScenesFromTextFile(Request request) throws IOException, RayTracerException {

		ParserableScene[] parserableScenes = generateParserableScenesFromFile(request.pathToSceneDescription);

		Scene[] scenes = new Scene[parserableScenes.length];
		for (int frameNumber = 0; frameNumber < parserableScenes.length; frameNumber++) {
			System.out.print("Creating frame number " + (frameNumber + 1) + "...		");
			scenes[frameNumber] = generateSceneFromParserableScene(parserableScenes[frameNumber], request);
			System.out.println("Finished!");
		}
		if (LOG)
			System.out.println("Finished parsing scene file " + request.pathToSceneDescription);

		return scenes;
	}

	private static ParserableScene[] generateParserableScenesFromFile(String pathToTextFile)
			throws IOException, RayTracerException {
		System.out.println("Started parsing scene file " + pathToTextFile);

		Scanner scanner = new Scanner(new FileReader(pathToTextFile));
		// read until end of file
		String fileInString = scanner.useDelimiter("\\A").next();
		scanner.close();

		String[] frames = fileInString.split("<FRAME_SPERATOR>");
		int numberOfFrames = frames.length;
		ParserableScene[] parserableScenes = new ParserableScene[numberOfFrames];
		for (int frameNumber = 0; frameNumber < numberOfFrames; frameNumber++) {
			System.out.print("Parsing frame number " + (frameNumber + 1) + "...		");
			parserableScenes[frameNumber] = generateParserableSceneFromString(frames[frameNumber]);
			System.out.println("Finished!");
		}
		return parserableScenes;
	}

	private static ParserableScene generateParserableSceneFromString(String sceneTextData)
			throws IOException, RayTracerException {

		BufferedReader reader = new BufferedReader(new StringReader(sceneTextData));
		String currentObjectString = null;
		int lineNum = 0;

		ParserableScene parserableScene = new ParserableScene();

		while ((currentObjectString = reader.readLine()) != null) {
			currentObjectString = currentObjectString.trim();
			++lineNum;

			if (currentObjectString.isEmpty() || (currentObjectString.charAt(0) == '#')) { // comment
																							// //
																							// comment
				continue;
			} else {
				String currentObjectType = currentObjectString.substring(0, 3).toLowerCase();
				String[] currentObjectParams = currentObjectString.substring(3).trim().toLowerCase().split("\\s+");

				if (currentObjectType.equals("cam")) {
					parserableScene.cameraParserData = new ParserableObject(currentObjectType, currentObjectParams);
				} else if (currentObjectType.equals("set")) {
					parserableScene.setParserData = new ParserableObject(currentObjectType, currentObjectParams);
				} else if (currentObjectType.equals("mtl")) {
					parserableScene.materialsParserArray
							.add(new ParserableObject(currentObjectType, currentObjectParams));
				} else if (currentObjectType.equals("sph") || currentObjectType.equals("pln")
						|| currentObjectType.equals("trg")) {
					parserableScene.renderableObjectParserArray
							.add(new ParserableObject(currentObjectType, currentObjectParams));
				} else if (currentObjectType.equals("lgt")) {
					parserableScene.lightSourceParserArray
							.add(new ParserableObject(currentObjectType, currentObjectParams));
				} else {
					System.out.println(
							String.format("ERROR: Did not recognize object: %s (line %d)", currentObjectType, lineNum));
					continue;
				}
				if (LOG)
					System.out.println(String.format("Parsed %s (line %d)", currentObjectType, lineNum));
			}
		}
		if (parserableScene.setParserData == null) {
			System.out.println("Missing Set Data");
			reader.close();
			throw new IOException();
		} else if (parserableScene.cameraParserData == null) {
			System.out.println("Missing Set Data");
			reader.close();
			throw new IOException();
		}

		reader.close();

		return parserableScene;
	}

	private static Scene generateSceneFromParserableScene(ParserableScene parserableScene, Request request) {
		Scene scene = generateSceneWithSetData(parserableScene.setParserData.objectParams);

		scene.setCamera(generateCamera(parserableScene.cameraParserData.objectParams, request.resultImageHeight,
				request.resultImageWidth));

		ArrayList<Material> MaterialsArray = new ArrayList<>();
		for (ParserableObject parserData : parserableScene.materialsParserArray) {
			MaterialsArray.add(generateMaterial(parserData.objectParams));
		}

		for (ParserableObject renderableObjectParserData : parserableScene.renderableObjectParserArray) {
			scene.addRenderableObject(generateRenderableObject(renderableObjectParserData, MaterialsArray));
		}

		for (ParserableObject parserData : parserableScene.lightSourceParserArray) {
			scene.addLightSource(generateLightSource(parserData.objectParams));
		}
		return scene;
	}

	private static Scene generateSceneWithSetData(String[] setParams) {
		double red, green, blue;
		int i = 0;
		red = Double.parseDouble(setParams[i++]);
		green = Double.parseDouble(setParams[i++]);
		blue = Double.parseDouble(setParams[i++]);
		Color backgroundColor = new Color(red, green, blue);

		int rootNumberOfShadowRay = Integer.parseInt(setParams[i++]);
		int maximumNumberOfRecurtsions = Integer.parseInt(setParams[i++]);
		int superSampling = 1;
		if (setParams.length > i) {
			superSampling = Integer.parseInt(setParams[i++]);
		}

		return new Scene(backgroundColor, rootNumberOfShadowRay, maximumNumberOfRecurtsions, superSampling);
	}

	private static Camera generateCamera(String[] cameraParams, int imageHeightInPixels, int imageWidthInPixels) {
		int i = 0;

		double x, y, z;
		x = Double.parseDouble(cameraParams[i++]);
		y = Double.parseDouble(cameraParams[i++]);
		z = Double.parseDouble(cameraParams[i++]);
		Vector3D position = new Vector3D(x, y, z);

		x = Double.parseDouble(cameraParams[i++]);
		y = Double.parseDouble(cameraParams[i++]);
		z = Double.parseDouble(cameraParams[i++]);
		Vector3D lookAtPoint = new Vector3D(x, y, z);

		x = Double.parseDouble(cameraParams[i++]);
		y = Double.parseDouble(cameraParams[i++]);
		z = Double.parseDouble(cameraParams[i++]);
		Vector3D upDirection = new Vector3D(x, y, z);

		double screenDistance = Double.parseDouble(cameraParams[i++]);
		double screenWidthRelativeToScene = Double.parseDouble(cameraParams[i++]);

		return new Camera(position, lookAtPoint, upDirection, screenDistance, screenWidthRelativeToScene,
				imageHeightInPixels, imageWidthInPixels);
	}

	private static Material generateMaterial(String[] materialParams) {
		int i = 0;

		double red, green, blue;
		red = Double.parseDouble(materialParams[i++]);
		green = Double.parseDouble(materialParams[i++]);
		blue = Double.parseDouble(materialParams[i++]);
		Color diffuseColor = new Color(red, green, blue);

		red = Double.parseDouble(materialParams[i++]);
		green = Double.parseDouble(materialParams[i++]);
		blue = Double.parseDouble(materialParams[i++]);
		Color specularColor = new Color(red, green, blue);

		red = Double.parseDouble(materialParams[i++]);
		green = Double.parseDouble(materialParams[i++]);
		blue = Double.parseDouble(materialParams[i++]);
		Color reflectionColor = new Color(red, green, blue);

		int phongSpecularity = Integer.parseInt(materialParams[i++]);
		double transperancy = Double.parseDouble(materialParams[i++]);
		return new Material(diffuseColor, specularColor, reflectionColor, phongSpecularity, transperancy);
	}

	private static RenderableObject generateRenderableObject(ParserableObject renderableParserObject,
			ArrayList<Material> MaterialsArray) {
		switch (renderableParserObject.objectType) {
		case "sph":
			return generateRenderableSphere(renderableParserObject.objectParams, MaterialsArray);
		case "pln":
			return generateRenderablePlane(renderableParserObject.objectParams, MaterialsArray);
		case "trg":
			return generateRenderableTriangle(renderableParserObject.objectParams, MaterialsArray);
		}
		return null;
	}

	private static RenderableTriangle generateRenderableTriangle(String[] objectParams,
			ArrayList<Material> MaterialsArray) {
		int i = 0;

		double x, y, z;
		x = Double.parseDouble(objectParams[i++]);
		y = Double.parseDouble(objectParams[i++]);
		z = Double.parseDouble(objectParams[i++]);
		Vector3D vertex1 = new Vector3D(x, y, z);

		x = Double.parseDouble(objectParams[i++]);
		y = Double.parseDouble(objectParams[i++]);
		z = Double.parseDouble(objectParams[i++]);
		Vector3D vertex2 = new Vector3D(x, y, z);

		x = Double.parseDouble(objectParams[i++]);
		y = Double.parseDouble(objectParams[i++]);
		z = Double.parseDouble(objectParams[i++]);
		Vector3D vertex3 = new Vector3D(x, y, z);

		Material material = MaterialsArray.get(Integer.parseInt(objectParams[i++]) - 1);

		Vector3D[] vertices = new Vector3D[] { vertex1, vertex2, vertex3 };
		return new RenderableTriangle(vertices, material);
	}

	private static RenderablePlane generateRenderablePlane(String[] objectParams, ArrayList<Material> MaterialsArray) {
		int i = 0;

		double x, y, z;
		x = Double.parseDouble(objectParams[i++]);
		y = Double.parseDouble(objectParams[i++]);
		z = Double.parseDouble(objectParams[i++]);
		Vector3D planeNormal = new Vector3D(x, y, z);

		double planeOffset = Double.parseDouble(objectParams[i++]);
		Material material = MaterialsArray.get(Integer.parseInt(objectParams[i++]) - 1);

		return new RenderablePlane(planeNormal, planeOffset, material);
	}

	private static RenderableSphere generateRenderableSphere(String[] objectParams,
			ArrayList<Material> MaterialsArray) {
		int i = 0;

		double x, y, z;
		x = Double.parseDouble(objectParams[i++]);
		y = Double.parseDouble(objectParams[i++]);
		z = Double.parseDouble(objectParams[i++]);
		Vector3D sphereCenterPosition = new Vector3D(x, y, z);

		double sphereRadius = Double.parseDouble(objectParams[i++]);
		Material material = MaterialsArray.get(Integer.parseInt(objectParams[i++]) - 1);

		return new RenderableSphere(sphereCenterPosition, sphereRadius, material);
	}

	private static LightSource generateLightSource(String[] lightSourceParams) {
		int i = 0;

		double x, y, z;
		x = Double.parseDouble(lightSourceParams[i++]);
		y = Double.parseDouble(lightSourceParams[i++]);
		z = Double.parseDouble(lightSourceParams[i++]);
		Vector3D position = new Vector3D(x, y, z);

		double red, green, blue;
		red = Double.parseDouble(lightSourceParams[i++]);
		green = Double.parseDouble(lightSourceParams[i++]);
		blue = Double.parseDouble(lightSourceParams[i++]);
		Color lightColor = new Color(red, green, blue);

		double specularIntensity = Double.parseDouble(lightSourceParams[i++]);
		double shadowIntensity = Double.parseDouble(lightSourceParams[i++]);
		double lightRadius = Double.parseDouble(lightSourceParams[i++]);

		return new LightSource(position, lightColor, specularIntensity, shadowIntensity, lightRadius);
	}

	private static class ParserableScene {
		public ParserableObject setParserData = null;
		public ParserableObject cameraParserData = null;
		public ArrayList<ParserableObject> materialsParserArray = new ArrayList<>();
		public ArrayList<ParserableObject> renderableObjectParserArray = new ArrayList<>();
		public ArrayList<ParserableObject> lightSourceParserArray = new ArrayList<>();
	}

	private static class ParserableObject {
		public String objectType;
		public String[] objectParams;

		public ParserableObject(String objectType, String[] objectParams) {
			this.objectType = objectType;
			this.objectParams = objectParams;
		}
	}

}
