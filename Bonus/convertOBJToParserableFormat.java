package RenderScene.Bonus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class convertOBJToParserableFormat {

	public static void main(String[] args) throws IOException {

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
			minX= minY = minZ = Double.POSITIVE_INFINITY;
			
			
			for (double[] point: vertices) {
				if (maxX < point[0])
				{
					maxX = point[0];
				} 
				if (minX > point[0])
				{
					minX = point[0];
				}
				if (maxY < point[1])
				{
					maxY = point[1];
				} 
				if (minY > point[1])
				{
					minY = point[1];
				}
				if (maxZ < point[2])
				{
					maxZ = point[2];
				} 
				if (minZ > point[2])
				{
					minZ = point[2];
				}
			}
			
			double[] lookAt = {(maxX + minX)/2, (maxY + minY)/2, (maxZ + minZ)/2};
			double[] cameraPosition = {(maxX + minX)/2 + (maxX - minX)*2, (maxY + minY)/2, (maxZ + minZ)/2};
			
			try {
				PrintWriter writer = new PrintWriter(args[1]);
				writer.format("cam %f %f %f %f %f %f %f %f %f %f %f\n", 
						cameraPosition[0], cameraPosition[1], cameraPosition[2],
						lookAt[0], lookAt[1], lookAt[2], 0.0, 0.0, 1.0, (maxX - minX)*0.25, lookAt[0]);
				
				
				for (double[][] triangle : triangles) {
					writer.println("trg " + triangle[0][0] + " " + triangle[0][1] + " " + triangle[0][2] + " "
							+ triangle[1][0] + " " + triangle[1][1] + " " + triangle[1][2] + " " + triangle[2][0] + " "
							+ triangle[2][1] + " " + triangle[2][2] + " 1");
				}
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
