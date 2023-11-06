
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {
	
	/* -------------------------------------------------------------------------- */
	/*                                 File Utils                                 */
	/* -------------------------------------------------------------------------- */

	// Reads the entire file into a String
	static String ReadFileAsString(String filename) {
		String fileContents = "";
		try {
			fileContents = new String(Files.readAllBytes(Paths.get(filename)));
		} catch (IOException e) {
			System.err.println("Error reading file: " + filename);
			System.exit(1);
		}
		return fileContents;
	}

	// Writes the string to the file
	public static void WriteStringToFile(String content, String filename) {
		try {
			Path filePath = Paths.get(filename);
			Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.println("Error writing to file: " + filename);
			System.exit(1);
		}
	}

	/* -------------------------------------------------------------------------- */
	/*                                 Print Utils                                */
	/* -------------------------------------------------------------------------- */

	// Prints the string while adding a frontStr to the beginning of each line
	static void PrintLines(String input) {
		String correct = " -> ";
		input = input.replaceAll("\n", "\n" + correct);
		System.err.println(correct + input);
	}

	/*
	 * Prints the input string with the line numbers and a beginning string
	 * indicating the error. Includes line numbers.
	 * input: The entire file as a string
	 * start: The start index to print
	 * end: The end index to print
	 * beginError: The start index of the error
	 * endError: The end index of the error
	 */
	public static void PrintFunctionError(String input, int start, int end, int beginError, int endError) {
        if (beginError < 0 || endError > end || beginError > endError) {
            System.out.println("Invalid range specified in PrintFunctionError().");
            return;
        }

		// Line beginning strings
		String correct = " -> ";
		String error = " =? ";

        StringBuilder result = new StringBuilder();
        String[] lines = input.split("\n");

        int lineStart = 0;
		// Loop though each line (To count the line numbers)
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineEnd = lineStart + line.length();
			
			// Only print the line if it is within the range
            if (lineEnd >= start && lineStart < end) {

				// Print the line with the correct beginning string
                if (lineEnd >= beginError && lineStart < endError && !line.trim().isEmpty()) {
                	result.append(i + 1).append(error).append(line).append("\n");
                }
				else{
					result.append(i + 1).append(correct).append(line).append("\n");
				}
            }

            lineStart = lineEnd + 1; // +1 to account for the newline character
        }

        System.out.println(result.toString());
    }

	public static void PrintParseError(String input, int start, int end, int beginError, int endError) {
        if (beginError < 0 || endError > end || beginError > endError) {
            System.out.println("Invalid range specified in PrintParseError().");
            return;
        }

		// Line beginning strings
		String correct = " -> ";
		String error = " =? ";

        StringBuilder result = new StringBuilder();
        String[] lines = input.split("\n");

        for (int i = start; i <= lines.length; i++) {
			if (i >= end) break;

			// Print the line with the correct beginning string
			if (i >= beginError && i < endError) {
				result.append(i).append(error).append(lines[i-1]).append("\n");
			}
			else{
				result.append(i).append(correct).append(lines[i-1]).append("\n");
			}
        }

        System.out.println(result.toString());
    }

	/* -------------------------------------------------------------------------- */
	/*                                String Utils                                */
	/* -------------------------------------------------------------------------- */

	// Gets the line number of the specified index
	public static int getLineNumber(String input, int startIndex) {
        int lineNumber = 1;
        for (int i = 0; i < startIndex; i++) {
            if (input.charAt(i) == '\n') {
                lineNumber++;
            }
        }
        return lineNumber;
    }
}
