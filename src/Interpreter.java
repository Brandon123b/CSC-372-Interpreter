import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Interpreter {

	static String inputFile;
	static String programName;
	static String outputFile;
	static boolean hasError = false;

	public static void main(String[] args) {

		// Get input file from command line
		if (args.length == 1) {
			inputFile = args[0];
			programName = inputFile.substring(inputFile.lastIndexOf('/') + 1, inputFile.lastIndexOf('.'));
			outputFile = programName + ".java";
		} else {
			System.err.println("Usage: java Interpreter <input file>");
			System.exit(1);
		}

		// Read input file
		String input = Utils.ReadFileAsString(inputFile);

		// Parse the input into functions
		List<Function> functions = splitIntoFunctions(input);

		// Check for errors before continuing
		if (hasError)
			System.exit(1);

		// Interpret the functions
		StringBuilder functionDecl = new StringBuilder();
		for (Function function : functions) {
			functionDecl.append(HandleFunction(function));
		}

		// Check for errors before continuing
		if (hasError)
			System.exit(1);

		//
		// Write to the output file
		//

		// Open the template file
		String template = Utils.ReadFileAsString("src/MainTemplate.java");

		// Replace the name of the template
		template = template.replace("MainTemplate", programName);
		// TODO: Replace global variables
		// Call first function from main() (With command line args)
		template = template.replace("/* {Call_Start} */", CallMainFunction(functions.get(0)));
		// TODO: Gameloop
		// Replace the function declarations
		template = template.replace("/* {Functions} */", functionDecl.toString());

		Utils.WriteStringToFile(template, outputFile);
	}

	/* -------------------------------------------------------------------------- */
	/*                               Convert to Java                              */
	/* -------------------------------------------------------------------------- */

	/* -------------------------------- Function -------------------------------- */

	// Convert a Function object to a Java function string
	public static String HandleFunction(Function function){
		StringBuilder sb = new StringBuilder();

		// Add the function declaration
		sb.append("\tpublic static void " + function.name + "(" + HandleArgs(function.parameters) + "){\n\n");

		// Add the function body here
		sb.append(function.body);

		// Add the function closing
		sb.append("\t}\n");

		return sb.toString();
	}

	// Convert a List<Arg> to a Java function string
	public static String HandleArgs(List<Arg> args){
		StringBuilder sb = new StringBuilder();


		// Add every argument to the string
		for (Arg arg : args) {
			// TODO: Boolean fix
			if (arg.type.equals("bool"))
				sb.append("boolean" + " " + arg.name + ", ");	// bool is actually boolean in Java
			else
				sb.append(arg.type + " " + arg.name + ", ");
		}

		// Remove the last comma
		if (args.size() > 0)
			sb.delete(sb.length() - 2, sb.length());

		return sb.toString();
	}

	// Convert the main function to a Java function call string
	// Args are converted and passed automatically with error checking at runtime
	public static String CallMainFunction(Function function) {
		StringBuilder sb = new StringBuilder();
		sb.append("if(args.length != " + function.parameters.size() + ") {\n");
		sb.append("\t\t\tSystem.err.println(\"Invalid number of arguments specified!\");\n");
		sb.append("\t\t\tSystem.exit(1);\n");
		sb.append("\t\t}\n");

		sb.append("\t\ttry {\n");
		sb.append("\t\t\t" + function.name + "(");

		boolean first = true;
		for (int i = 0; i < function.parameters.size(); i++) {
			if (!first) {
				sb.append(", ");
			}

			switch(function.parameters.get(i).type) {
				case "string":
					sb.append("args[" + i + "]");
					break;
				case "int":
					sb.append("Integer.parseInt(args[" + i + "])");
					break;
				case "bool":
					sb.append("Boolean.parseBoolean(args[" + i + "])");
					break;
				case "double":
					sb.append("Double.parseDouble(args[" + i + "])");
					break;
			}

			first = false;
		}

		sb.append(");\n");

		sb.append("\t\t} catch (Exception e) {\n");
		sb.append("\t\t\tSystem.err.println(\"Invalid arguments specified!\");\n");
		sb.append("\t\t\tSystem.exit(1);\n");
		sb.append("\t\t}");
		return sb.toString();
	}

	/* -------------------------------------------------------------------------- */
	/*                              Parse From String                             */
	/* -------------------------------------------------------------------------- */

	/* -------------------------------- Function -------------------------------- */

	// Splits the input string into a list of function objects
	public static List<Function> splitIntoFunctions(String input) {
        List<Function> functions = new ArrayList<>();

		Pattern pattern = Pattern.compile("(\\s*)Begin a function called ([a-zA-Z0-9]+) ?([^.]+)?\\.(.*?)(Leave the function\\.)(\\s*)", Pattern.DOTALL);
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
				System.out.println("(Is your function declaration correct?)");

				// Print the function
				Utils.PrintFunctionError(input, lastIndex, end, lastIndex, start);

				hasError = true;
            }
		
			// Add the function call to the list
			int lineNumber = Utils.getLineNumber(input, matcher.start(2));
			int endLineNumber = Utils.getLineNumber(input, matcher.start(5)) + 1;
			String name = matcher.group(2);
			List<Arg> args = ParseArgs(matcher.group(3), lineNumber);

			String body = ParseBlock(matcher.group(4), "\t\t", input, lineNumber, endLineNumber);

			Function function = new Function(name, args, body, lineNumber);
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
			System.out.println("(Is your function declaration correct?)");

			// Print the function
			Utils.PrintFunctionError(input, start, input.length(), lastIndex, input.length());

			hasError = true;
        }

        return functions;
    }


	// Parses the arguments of a function call and returns a list of Arg objects
	static List<Arg> ParseArgs(String argStr, int lineNumber) {
		if (argStr == null || argStr.isEmpty())
			return new ArrayList<>();
	
		List<Arg> args = new ArrayList<>();
	
		Pattern pattern1 = Pattern.compile("^with an? (\\S*) called ([^,\n ]*)");
		Pattern pattern2 = Pattern.compile(", an? (\\S*) called ([^,\n ]*)");
		Pattern pattern3 = Pattern.compile("and an? (\\S*) called ([^,\n .]*)");
	
		Matcher matcher1 = pattern1.matcher(argStr);
		Matcher matcher2 = pattern2.matcher(argStr);
		Matcher matcher3 = pattern3.matcher(argStr);

		// Match the first argument
		if (matcher1.find()) {
			// Add the argument to the list
			args.add(new Arg(matcher1.group(2), matcher1.group(1)));
		}
		else{
			System.err.println("ERROR: Invalid argument declaration on line " + lineNumber + ": ");
			System.err.println("Given: \"" + argStr + "\"");
			System.err.println("Expected arguements in the following form: ");
			System.err.println("	\"with a <type> called <name>\"");
			System.err.println("	\"with a <type> called <name> and a <type> called <name>\"");
			System.err.println("	\"with a <type> called <name>, a <type> called <name>, and a <type> called <name>\"");
			System.err.println();

			hasError = true;
			return args;
		}

		int lastIndex = matcher1.end();
		
		// Match arg 2 through n-1
		while (matcher2.find()) {
			// Add the argument to the list
			args.add(new Arg(matcher2.group(2), matcher2.group(1)));

			lastIndex = matcher2.end();
		}
		
		// Match the last argument
		if (matcher3.find()) {
			// Add the argument to the list
			args.add(new Arg(matcher3.group(2), matcher3.group(1)));

			lastIndex = matcher3.end();
		}

		// If not all arguments were matched, print an error
		if (lastIndex < argStr.length()) {
			System.err.println("ERROR: Invalid argument declaration: ");
			System.err.println("Given: \"" + argStr + "\"");
			System.err.println("Expected arguements in the following form: ");
			System.err.println("	\"with a <type> called <name>\"");
			System.err.println("	\"with a <type> called <name> and a <type> called <name>\"");
			System.err.println("	\"with a <type> called <name>, a <type> called <name>, and a <type> called <name>\"");
			System.err.println();
		}
	
		return args;
	}

	// Parse a block by matching expressions until either the block is empty or no match is found
	public static String ParseBlock(String input, String indent, String file, int start, int end) {
		return ParseBlock(Arrays.asList(input.split("[\\.:]")), indent, file, start, end, new HashMap<>());
	}

	public static String ParseBlock(List<String> input, String indent, String file, int start, int end, Map<String, String> blockVars) {
		StringBuilder sb = new StringBuilder();
		Stack<String> nesting = new Stack<>();

		Pattern loopStartPattern    = Pattern.compile("^While (.+)$");
		Pattern loopEndPattern      = Pattern.compile("^Exit the while$");
		Pattern condStartPattern    = Pattern.compile("^If (.+), then$");
		Pattern condEndPattern      = Pattern.compile("^Leave the if statement$");
		Pattern varSetPattern       = Pattern.compile("^Set ([A-Za-z0-9]+) to (.+)");
		Pattern functionCallPattern = Pattern.compile("^Call the function (.+)");

		int curLine = start;

		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).contains("\n")) {
				curLine++;
			}
			String line = input.get(i).strip();
			if (line.equals("")) continue;

			Matcher loopStart = loopStartPattern.matcher(line);
			Matcher loopEnd = loopEndPattern.matcher(line);
			Matcher condStart = condStartPattern.matcher(line);
			Matcher condEnd = condEndPattern.matcher(line);
			Matcher varSet = varSetPattern.matcher(line);
			Matcher functionCall = functionCallPattern.matcher(line);

			if (loopStart.find()) {
				nesting.push("loop");
				sb.append(indent + "while (" + ParseEvalExpr(loopStart.group(1)) + ") {\n");
				indent += "\t";
			} else if (loopEnd.find()) {
				if (nesting.empty() || !nesting.pop().equals("loop")) {
					// error
					System.out.println("SYNTAX ERROR: tried to exit a nonexistent while statement");
					Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					return "";
				}
				indent = indent.substring(1);
				sb.append(indent + "}\n");
			} else if (condStart.find()) {
				nesting.push("cond");
				sb.append(indent + "if (" + ParseEvalExpr(condStart.group(1)) + ") {\n");
				indent += "\t";
			} else if (condEnd.find()) {
				if (nesting.empty() || !nesting.pop().equals("cond")) {
					// error
					System.out.println("SYNTAX ERROR: tried to exit a nonexistent if statement");
					Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					return "";
				}
				indent = indent.substring(1);
				sb.append(indent + "}\n");
			} else if (varSet.find()) {
				// special logic for doubles because of "." character ambiguity
				String val;
				if (i + 1 < input.size() && Pattern.compile("[0-9]+").matcher(input.get(i + 1)).matches()) {
					val = varSet.group(2) + "." + input.get(i + 1);
					i++;
				} else {
					val = varSet.group(2);
				}
				String tmp = ParseVarSet(varSet.group(1), val, blockVars, file, start, end, curLine);
				if (tmp.equals("")) {
					return "";
				} else {
					sb.append(indent + tmp + ";\n");
				}
			} else if (functionCall.find()) {
				sb.append(indent + ParseFunctionCall(line) + "\n");
			} else {
				// error
				System.out.println("SYNTAX ERROR: unrecognized statement " + line);
				Utils.PrintParseError(file, start, end, curLine, curLine + 1);
				return "";
			}
		}

		// if we get here we're done parsing and we didn't find any errors
		return sb.toString();
	}

	public static String ParseVarSet(String name, String val, Map<String, String> blockVars, String file, int start, int end, int curLine) {
		if (blockVars.containsKey(name)) {
			switch(blockVars.get(name)) {
				case "string":
					if (Pattern.compile("^\".*\"$").matcher(val).matches()) {
						return name + " = " + val;
					} else {
						// error
						System.out.println("SYNTAX ERROR: variable " + name + " has already been defined as a string, but tried to assign a non-string value");
						Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					}
					break;
				case "int":
					try {
						return name + " = " + Integer.parseInt(val);
					} catch (Exception e) {
						// error
						System.out.println("SYNTAX ERROR: variable " + name + " has already been defined as an int, but tried to assign a non-int value");
						Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					}
					break;
				case "double":
					try {
						return name + " = " + Double.parseDouble(val);
					} catch (Exception e) {
						// error
						System.out.println("SYNTAX ERROR: variable " + name + " has already been defined as a double, but tried to assign a non-double value");
						Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					}
					break;
				case "boolean":
					if (val.equals("true") || val.equals("false")) {
						return name + " = " + val;
					} else {
						// error
						System.out.println("SYNTAX ERROR: variable " + name + " has already been defined as a boolean but tried to assign a non-boolean value");
						Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					}
					break;
				default:
					System.out.println("if you see this, there's a bug in Interpreter.java :(");
			}
		} else {
			if (Pattern.compile("^\".*\"$").matcher(val).matches()) {
				blockVars.put(name, "string");
				return "String " + name + " = " + val;
			} else if (Pattern.compile("^[0-9]+$").matcher(val).matches()) {
				blockVars.put(name, "int");
				return "int " + name + " = " + val;
			} else if (Pattern.compile("^[0-9]+\\.[0-9]+$").matcher(val).matches()) {
				blockVars.put(name, "double");
				return "double " + name + " = " + val;
			} else if (val.equals("true") || val.equals("false")) {
				blockVars.put(name, "boolean");
				return "boolean " + name + " = " + val;
			} else {
				// error
				System.out.println("SYNTAX ERROR: " + val + " is not a valid variable value");
				Utils.PrintParseError(file, start, end, curLine, curLine + 1);
			}
		}

		return "";
	}

	public static String ParseFunctionCall(String input) {
		return "";
	}

	public static String ParseEvalExpr(String input) {
		return "";
	}

	public static String ParseMathExpr(String input) {
		return "";
	}

	public static String ParseReturnStmt(String input) {
		return "";
	}
	
	/* -------------------------------------------------------------------------- */
	/*                               Data Structures                              */
	/* -------------------------------------------------------------------------- */

	static class Function{

		public String name;				// The name of the function
		public List<Arg> parameters;		// The parameters of the function
		public String body;				// The body of the function
		public int lineNumber;			// The line number of the function call 

		public Function(String name, List<Arg> parameters, String body, int lineNumber){
			this.name = name;
			this.parameters = parameters;
			this.body = body;
			this.lineNumber = lineNumber;
		}

		public void Print(){
			System.out.println("Function " + name + ": ");
			
			System.out.println("	Parameters: ");
			for (Arg arg : parameters) {
				arg.Print();
			}

			System.out.println("	Start Line: " + lineNumber);
			Utils.PrintLines(body);

			System.out.println();
		}
	}

	static class Arg{
		public String name;
		public String type;

		public Arg(String name, String type){
			this.name = name;
			this.type = type;
		}

		public void Print(){
			System.out.println("	Arg (" + name + ": " + type + ")");
		}
	}
}