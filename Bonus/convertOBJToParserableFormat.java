package RenderScene.Bonus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class convertOBJToParserableFormat {

	public static void main(String[] args) throws IOException {
		final int FRAME_NUMBER = 30;

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
			double[] cameraPosition = { (maxX + minX) / 2 + (maxX - minX), (maxY + minY) / 2, (maxZ + minZ) / 2 };

			try {
				StringBuilder scene = new StringBuilder();
				double[] camera = { cameraPosition[0], cameraPosition[1], cameraPosition[2], lookAt[0], lookAt[1],
						lookAt[2], 0.0, 0.0, 1.0, (maxX - minX) * 0.25, lookAt[0] };

				//
				// String camera = String.format("cam %f %f %f %f %f %f %f %f %f
				// %f %f\n",
				// cameraPosition[0], cameraPosition[1], cameraPosition[2],
				// lookAt[0], lookAt[1], lookAt[2], 0.0, 0.0, 1.0, (maxX -
				// minX)*0.25, lookAt[0]);
				// writer.format("cam %f %f %f %f %f %f %f %f %f %f %f\n",
				// cameraPosition[0], cameraPosition[1], cameraPosition[2],
				// lookAt[0], lookAt[1], lookAt[2], 0.0, 0.0, 1.0, (maxX -
				// minX)*0.25, lookAt[0]);

				StringBuilder trianglesString = new StringBuilder();
				for (double[][] triangle : triangles) {
					trianglesString.append("trg " + triangle[0][0] + " " + triangle[0][1] + " " + triangle[0][2] + " "
							+ triangle[1][0] + " " + triangle[1][1] + " " + triangle[1][2] + " " + triangle[2][0] + " "
							+ triangle[2][1] + " " + triangle[2][2] + " 1\n");
				}

				String setMaterialsLights = String.join("\n", "set 		1  	1  	1   	1 	10           1",
						"mtl		0.07	0.97	0.07	1	1	1	0.5	0.5	0.5	30	0",
						"lgt		0	3	0	0.5	0.5	0.3	1	0.9	1", "lgt		-3	3	-3	0.5	0.5	0.3	1	0.9	1",
						"lgt		-3	3	3	0.5	0.5	0.3	1	0.9	1", "lgt		3	3	-3	0.5	0.5	0.3	1	0.9	1",
						"lgt		3	3	3	0.5	0.5	0.3	1	0.9	1\n");

				String delimiter = "<FRAME_SPERATOR>\n";

				double radius = Math.max(maxX - minX, maxY - minY);
				double endRadius = radius * 0.75;
				double startX = lookAt[0]; // center of object
				double startY = lookAt[1];

				PrintWriter writer = new PrintWriter(args[1]);

				for (int i = 0; i < FRAME_NUMBER; i++) {
					// change to radius
					double currentRadius = radius + (endRadius - radius) * i / FRAME_NUMBER;
					double cos = Math.cos(0.5 * Math.PI * (i + 1) / FRAME_NUMBER);
					double sin = Math.sin(0.5 * Math.PI * (i + 1) / FRAME_NUMBER);
					double newX = startX + currentRadius * cos;
					double newY = startY + currentRadius * sin;

					camera[0] = newX;
					camera[1] = newY;

					int k = 0;
					String cameraString = String.format("cam %f %f %f %f %f %f %f %f %f %f %f\n", camera[k++],
							camera[k++], camera[k++], camera[k++], camera[k++], camera[k++], camera[k++], camera[k++],
							camera[k++], camera[k++], camera[k++]);

					writer.println(cameraString);
					writer.println(setMaterialsLights);
					writer.println(trianglesString);
					if (i != FRAME_NUMBER - 1) {
						writer.println(delimiter);
					}

					// scene.append(cameraString);
					// scene.append(setMaterialsLights);
					// scene.append(trianglesString);
					// scene.append(delimiter);

				}

				writer.print(scene);

				writer.close();
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
