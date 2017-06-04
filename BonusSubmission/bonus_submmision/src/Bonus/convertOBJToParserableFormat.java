package RenderScene.Bonus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;



public class convertOBJToParserableFormat {
//	public static String IDO_FOLDER = "F:\\Tau\\Courses\\Computer Science\\2016-2017\\Semester 2\\Graphic\\RenderScene\\git\\RenderScene\\";
//	public static String JOHNATHAN_FOLDER = "C:\\Development\\Graphics\\RenderScene\\";
	public static void main(String[] args) throws IOException {
		
		
//		String currentUserFolder = IDO_FOLDER;
//		String objFile = "temp\\yoda\\yodaobj.obj";
//		String txtDst = "temp\\yodaScene.txt";
//		String movDst = "temp\\yodaScene10.mov";

		String currentUserFolder = args[0];
		String objFile = args[1];
		String txtDst = args[2];
		String movDst = args[3];
		
		
		
		
		Integer width = 400;
		Integer height = 400;
		int superSampeling = 1;
		int rootNumberOfShadowRay = 1;
		int recursionDepth = 2;
		int numberOfFrames = 100;
		double shadowIntencity = 0.6;
		
		System.out.format("objFile:		%s\n", objFile);
		System.out.format("txtDst:			%s\n", txtDst);
		System.out.format("movDst:			%s\n", movDst);
		System.out.format("width:			%d\n", width);
		System.out.format("height:			%d\n", height);
		System.out.format("superSampeling:		%d\n", superSampeling);
		System.out.format("rootNumberOfShadowRay:	%d\n", rootNumberOfShadowRay);
		System.out.format("recursionDepth:		%d\n", recursionDepth);
		System.out.format("numberOfFrames:		%d\n", numberOfFrames);
		System.out.format("shadowIntencity:	%f\n", shadowIntencity);
		
		args = new String[]{currentUserFolder + objFile ,
				currentUserFolder + txtDst ,
				currentUserFolder + movDst , 
				width.toString(),
				height.toString()};

		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			ArrayList<double[]> vertices = new ArrayList<>();
			ArrayList<double[][]> triangles = new ArrayList<>();

			String line = null;
			while ((line = reader.readLine()) != null) {
				// System.out.println(line);
				if (line.length() == 0) {
					continue;
				}
				if (line.charAt(0) == 'v') {
					String[] vertexPointString = line.split(" +");
					double[] vertexPointDouble = new double[3];
					for (int i = 0; i < 3; i++) {
						vertexPointDouble[i] = Double.parseDouble(vertexPointString[i + 1]); // i+1
																								// because
																								// after
																								// v
					}
					vertices.add(vertexPointDouble);
				}
			}

			reader.close();
			reader = new BufferedReader(new FileReader(args[0]));

			while ((line = reader.readLine()) != null) {
				// System.out.println(line);
				if (line.length() == 0) {
					continue;
				} else if (line.charAt(0) == 'f') {
					String[] lineSplit = line.split(" +");

					if (lineSplit.length == 4) // triangles
					{
						String[] trianglePointsString = line.split(" +");
						double[][] trianglePointsDouble = new double[3][3];
						for (int i = 0; i < 3; i++) {
							trianglePointsDouble[i] = vertices
									.get(Integer.parseInt(trianglePointsString[i + 1].split("/")[0]) - 1);
						}
						triangles.add(trianglePointsDouble);
					} else if (lineSplit.length == 5) // rectangles
					{
						String[] rectanglePointsString = line.split(" +");
						double[][] rectanglePointsDouble = new double[4][3];
						for (int i = 0; i < 4; i++) {
							rectanglePointsDouble[i] = vertices
									.get(Integer.parseInt(rectanglePointsString[i + 1].split("/")[0]) - 1);
						}

						int maxIndex = 1;
						double maxDistance = 0;
						for (int i = 1; i < 4; i++) {
							double currentDistance = 0;
							for (int j = 0; j < 3; j++) {
								currentDistance += (rectanglePointsDouble[0][j] - rectanglePointsDouble[i][j])
										* (rectanglePointsDouble[0][j] - rectanglePointsDouble[i][j]);
							}
							if (currentDistance > maxDistance) {
								maxDistance = currentDistance;
								maxIndex = i;
							}
						}

						double[][] triangle = new double[3][];
						int pos = 0;
						for (int j = 0; j < 4; j++) {
							if (j != maxIndex) {
								triangle[pos++] = rectanglePointsDouble[j];
							}
						}

						triangles.add(triangle);

						triangle = new double[3][];
						for (int j = 1; j < 4; j++) {
							triangle[j - 1] = rectanglePointsDouble[j];
						}
						triangles.add(triangle);

					}

				} else {
					continue;
				}
			}

			double maxX, minX, maxY, minY, maxZ, minZ;
			maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
			minX = minY = minZ = Double.POSITIVE_INFINITY;

			for (double[] point : vertices) {
				if (maxX < point[0]) {
					maxX = point[0];
				}
				if (minX > point[0]) {
					minX = point[0];
				}
				if (maxY < point[1]) {
					maxY = point[1];
				}
				if (minY > point[1]) {
					minY = point[1];
				}
				if (maxZ < point[2]) {
					maxZ = point[2];
				}
				if (minZ > point[2]) {
					minZ = point[2];
				}
			}

			double[] lookAt = { (maxX + minX) / 2, (maxY + minY) / 2, (maxZ + minZ) / 2 };
			double cameraPositionZ = (maxZ + minZ) / 2;

			try {
				StringBuilder scene = new StringBuilder();
				StringBuilder trianglesString = new StringBuilder();
				for (double[][] triangle : triangles) {
					trianglesString.append("trg " + triangle[0][0] + " " + triangle[0][1] + " " + triangle[0][2] + " "
							+ triangle[1][0] + " " + triangle[1][1] + " " + triangle[1][2] + " " + triangle[2][0] + " "
							+ triangle[2][1] + " " + triangle[2][2] + " 1\n");
				}

				double radiusConstant = 1.6;
				double screenDistanceConstant = 1.1;
				double radius = Math.max(maxX - minX, maxY - minY) * 0.5;
				double screenWidth = Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ);
				double screenDistance = radius * radiusConstant - radius * screenDistanceConstant;
				double planeDistance = radius * radiusConstant * 1.2;
				double lightDistance = radius * radiusConstant * 1;
				String setMaterialsLights = String.join("\n",
						"set 1 1 1 " + rootNumberOfShadowRay + " " + recursionDepth + " " + superSampeling,
						"mtl		0.07	0.97	0.07	0.2 0.2 0.2	0 0 0	30	0",
						"mtl		0.1 0.1 0.1 0 0 0	1 1 1	30	0",
						"lgt		0	0	" + (lookAt[2] + planeDistance) + "	0.5	0.5	0.3	1	" + shadowIntencity
								+ "	1",
						"lgt		0		" + (lookAt[1] + planeDistance) + " 0	0.5	0.5	0.3	1	" + shadowIntencity
								+ "	1",
						"lgt				" + (lookAt[0] + planeDistance) + " 0 0	0.5	0.5	0.3	1	" + shadowIntencity
								+ "	1",
						"lgt		0	0	-" + (lookAt[2] + planeDistance) + "	0.5	0.5	0.3	1	" + shadowIntencity
								+ "	1",
						"lgt		0		-" + (lookAt[1] + planeDistance) + " 0	0.5	0.5	0.3	1	" + shadowIntencity
								+ "	1",
						"lgt				-" + (lookAt[0] + planeDistance) + " 0 0	0.5	0.5	0.3	1	"
								+ shadowIntencity + "	1",
						"pln		0	1	0	" + (lookAt[1] + lightDistance) + "	2",
						"pln		0	-1	0	" + (lookAt[1] + planeDistance) + "	2",
						"pln		1	0	0	" + (lookAt[0] + planeDistance) + "	2",
						"pln		-1	0	0	" + (lookAt[0] + planeDistance) + "	2",
						"pln		0	0	-1	" + (lookAt[2] + planeDistance) + "	2",
						"pln		0	0	1	" + (lookAt[2] + planeDistance) + "  2");

				String delimiter = "<FRAME_SPERATOR>\n";

				double startX = lookAt[0]; // center of object
				double startY = lookAt[1];

				PrintWriter writer = new PrintWriter(args[1]);

				double[] camera = { 0, 0, cameraPositionZ, lookAt[0], lookAt[1], lookAt[2], 0.0, 0.0, 1.0,
						screenDistance, screenWidth };
				for (int i = 0; i < numberOfFrames; i++) {
					// change to radius
					double cos = Math.cos(2 * Math.PI * (i + 1) / numberOfFrames);
					double sin = Math.sin(2 * Math.PI * (i + 1) / numberOfFrames);
					double newX = startX + radius * radiusConstant * cos;
					double newY = startY + radius * radiusConstant * sin;

					camera[0] = newX;
					camera[1] = newY;
					int k = 0;
					String cameraString = String.format("cam %f %f %f %f %f %f %f %f %f %f %f\n", camera[k++],
							camera[k++], camera[k++], camera[k++], camera[k++], camera[k++], camera[k++], camera[k++],
							camera[k++], camera[k++], camera[k++]);

					writer.println(cameraString);
					writer.println(setMaterialsLights);
					writer.println(trianglesString);
					if (i != numberOfFrames - 1) {
						writer.println(delimiter);
					}
				}

				writer.print(scene);

				writer.close();
				RenderScene.Main.main(new String[] { args[1], args[2], args[3], args[4] });
			} catch (IOException e) {
				// do something
				System.out.println("bug\n");
			}

		} catch (FileNotFoundException e) {
			System.out.println("bugggg\n");
			e.printStackTrace();
		}

	}

}
