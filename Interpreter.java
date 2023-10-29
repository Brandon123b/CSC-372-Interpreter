import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

	static String inputFile;
	static boolean hasError = false;

	public static void main(String[] args) {
		
		// Get input file from command line
		if (args.length == 1) {
			inputFile = args[0];
		} else {
			System.err.println("Usage: java Interpreter <input file>");
			System.exit(1);
		}

		// Read input file
		String input = ReadFileAsString(inputFile);

		List<Function> functions = splitIntoFunctions(input);

		// Check for errors before continuing
		if (hasError)
			System.exit(1);

		// Print out the functions
		for (Function function : functions) {
			System.out.println("Found a function: ");
			System.out.println("Name: " + function.name);
			System.out.println("Parameters: " + function.parameters);
			System.out.println("Start Line: " + function.lineNumber);
			PrintLines(function.body);

			System.out.println();
		}
	}

	// Splits the input string into a list of function objects
	public static List<Function> splitIntoFunctions(String input) {
        List<Function> functions = new ArrayList<>();

		Pattern pattern = Pattern.compile("(\\s*)Begin a function called (.*?)\\.(.*?)Leave the function\\.(\\s*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        int lastIndex = 0;
		int start = 0;
		int end = 0;
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            
            // Check for text before the function call
            if (start > lastIndex) {

				// Print the error
				System.out.println("SYNTAX ERROR: Unexpected text outside of function");

				// Print the function
				PrintFunctionError(input, lastIndex, end, lastIndex, start);

				hasError = true;
            }
		
			// Add the function call to the list
			Function function = new Function(matcher.group(2), null, matcher.group(3), getLineNumber(input, matcher.start(2)));
			functions.add(function);

            lastIndex = end;
        }

		// Check for no function calls
		if (end == 0) {
			System.err.println("ERROR: No function calls found.");
			System.exit(1);
		}

        // Check for text after the last function call
        if (lastIndex < input.length()) {

			// Print the error
			System.out.println("SYNTAX ERROR: Unexpected text outside of function");

			// Print the function
			PrintFunctionError(input, start, input.length(), lastIndex, input.length());

			hasError = true;
        }

        return functions;
    }


	/* -------------------------------------------------------------------------- */
	/*                                    Utils                                   */
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
		String error = " -? ";

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

	/* -------------------------------------------------------------------------- */
	/*                               Data Structures                              */
	/* -------------------------------------------------------------------------- */

	static class Function{

		public String name;				// The name of the function
		public String[] parameters;		// The parameters of the function
		public String body;				// The body of the function
		public int lineNumber;			// The line number of the function call 

		public Function(String name, String[] parameters, String body, int lineNumber){
			this.name = name;
			this.parameters = parameters;
			this.body = body;
			this.lineNumber = lineNumber;
		}
	}
}