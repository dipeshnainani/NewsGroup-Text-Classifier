import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.regex.*;

/**
 * This class takes the input data and performs cleaning to be loaded into weka classifiers.
 */

/**
* @author Dipesh Nainani
*/

public class CleanFiles {

	public static void cleanData(String dirname) {

		try {

			File folder = new File(dirname);
			File[] listOfFiles = folder.listFiles();
			BufferedWriter out = null;

			for (File f : listOfFiles) {
				if (f.isDirectory()) {
					System.out.println(f);
					for (File file : f.listFiles()) {
						if (!file.getName().startsWith(".")) {
							String path = System.getProperty("user.dir")
									+ "/Cleaned_Data/" + f.getName() + "/"
									+ file.getName();
							BufferedReader rawInputFile;
							String line;
							rawInputFile = new BufferedReader(new FileReader(file));
							String pattern = ".*:.*";
							Pattern p = Pattern.compile(pattern);
							File newFile = new File(path);
							newFile.getParentFile().mkdirs();
							newFile.createNewFile();
							FileWriter fstream = new FileWriter(newFile, true);
							out = new BufferedWriter(fstream);

							while ((line = rawInputFile.readLine()) != null) {
								Matcher m = p.matcher(line);
								if (m.find()) {
									continue;
								} else {

									line = line.replaceAll(
											"\\d", " ");
									line = line.replaceAll(
											"\\p{Punct}", "");
									line = line.replaceAll(
											"\\s+", " ").toLowerCase();
									out.write(line);
								}

							}
							out.close();

						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

}
