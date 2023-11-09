import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
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

	static Map<String, String> globalVars = new HashMap<>();

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
		// Replace global variables
		template = template.replace("/* {Global_Vars} */", GlobalVariables());
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
		sb.append("\tpublic " + function.returnType + " " + function.name + "(" + HandleArgs(function.parameters) + "){\n\n");

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
		sb.append("\t\t\tcanvas." + function.name + "(");

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

	public static String GlobalVariables() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry entry : globalVars.entrySet()) {
			sb.append("\t").append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
		}
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

			Function function = new Function(name, args, "", lineNumber);
			String body = ParseBlock(matcher.group(4), "\t\t", input, lineNumber, endLineNumber, function);
			function.body = body;

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
	public static String ParseBlock(String input, String indent, String file, int start, int end, Function fn) {
		List<String> stmts = new ArrayList<>();
		StringBuilder stmt = new StringBuilder();
		boolean inString = false;
		boolean inLineComment = false;
		boolean inBlockComment = false;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '"') {
				inString = !inString;
			}

			if (inString) {
				stmt.append(input.charAt(i));
				continue;
			}

			if ((input.charAt(i) == '.' && (i <= 0 || !Character.isDigit(input.charAt(i - 1)) 
				|| i + 1 >= input.length() || !Character.isDigit(input.charAt(i + 1))))
			 	|| input.charAt(i) == ':') {
				
				stmts.add(stmt.toString());
				stmt = new StringBuilder();
				continue;
			}
			
			if (input.charAt(i) == '/') {
				if (!inBlockComment && i + 1 < input.length() && input.charAt(i + 1) == '*') {
					inBlockComment = true;
				} else if (inBlockComment && i > 0 && input.charAt(i - 1) == '*') {
					inBlockComment = false;
					continue;
				}
			}
			
			if (input.charAt(i) == '#') {
				inLineComment = true;
			} else if (!inString && input.charAt(i) == '\n' && inLineComment) {
				inLineComment = false;
			}
			
			if ((!inLineComment && !inBlockComment) || input.charAt(i) == '\n') {
				stmt.append(input.charAt(i));
			}
		}
		return ParseBlock(stmts, indent, file, start, end, new HashMap<>(), fn);
	}

	public static String ParseBlock(List<String> input, String indent, 
		String file, int start, int end, Map<String, String> blockVars, Function fn) {

		StringBuilder sb = new StringBuilder();
		Stack<String> nesting = new Stack<>();

		Map<String, Pattern> patterns = new HashMap<>();
		Map<String, Matcher> matchers = new HashMap<>();

		patterns.put("loopStart",    Pattern.compile("^While (.+)$"));
		patterns.put("loopEnd",      Pattern.compile("^Exit the while$"));
		patterns.put("condStart",    Pattern.compile("^If (.+), then$"));
		patterns.put("condEnd",      Pattern.compile("^Leave the if statement$"));
		patterns.put("varSet",       Pattern.compile("^Set ([A-Za-z0-9]+) to (.+)"));
		patterns.put("globalVarSet", Pattern.compile("^Set global ([A-Za-z0-9]+) to (.+)"));
		patterns.put("functionCall", Pattern.compile("^Call the function (.+)"));
		patterns.put("consoleWrite", Pattern.compile("^Print (.+) to the console$"));
		patterns.put("returnStmt",   Pattern.compile("^Return (.+) to the caller$"));

		int curLine = start;

		for (int i = 0; i < input.size(); i++) {
			curLine += input.get(i).chars().filter(c -> c == '\n').count();
			String line = input.get(i).trim();
			if (line.equals("")) continue;

			for (String s : patterns.keySet()) {
				matchers.put(s, patterns.get(s).matcher(line));
			}

			if (matchers.get("loopStart").find()) {
				nesting.push("while");
				sb.append(indent + "while (" + ParseEvalExpr(matchers.get("loopStart").group(1), blockVars) + ") {\n");
				indent += "\t";
			} else if (matchers.get("loopEnd").find()) {
				if (nesting.empty() || !nesting.pop().equals("while")) {
					// error
					System.out.println("SYNTAX ERROR: tried to exit a nonexistent while statement");
					Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					return "";
				}
				indent = indent.substring(1);
				sb.append(indent + "}\n");
			} else if (matchers.get("condStart").find()) {
				nesting.push("if");
				sb.append(indent + "if (" + ParseEvalExpr(matchers.get("condStart").group(1), blockVars) + ") {\n");
				indent += "\t";
			} else if (matchers.get("condEnd").find()) {
				if (nesting.empty() || !nesting.pop().equals("if")) {
					// error
					System.out.println("SYNTAX ERROR: tried to exit a nonexistent if statement");
					Utils.PrintParseError(file, start, end, curLine, curLine + 1);
					return "";
				}
				indent = indent.substring(1);
				sb.append(indent + "}\n");
			} else if (matchers.get("varSet").find()) {
				String tmp = ParseVarSet(matchers.get("varSet").group(1), 
					matchers.get("varSet").group(2), false, blockVars, file, start, end, curLine);
				if (tmp.equals("")) {
					return "";
				} else {
					sb.append(indent + tmp + ";\n");
				}
			} else if (matchers.get("globalVarSet").find()) {
				String tmp = ParseVarSet(matchers.get("globalVarSet").group(1), 
					matchers.get("globalVarSet").group(2), true, blockVars, file, start, end, curLine);
				if (tmp.equals("")) {
					return "";
				} else {
					sb.append(indent + tmp + ";\n");
				}
			} else if (matchers.get("functionCall").find()) {
				sb.append(indent + ParseFunctionCall(line) + "\n");
			} else if (matchers.get("consoleWrite").find()) {
				sb.append(indent + "System.out.println(" 
					+ ParseConsoleWrite(matchers.get("consoleWrite").group(1), blockVars, file, start, end, curLine)
					+ ");\n");
			} else if (matchers.get("returnStmt").find()) {
				sb.append(indent + "return "
					+ ParseReturnStmt(matchers.get("returnStmt").group(1), blockVars, fn, file, start, end, curLine)
					+ ";\n");
			} else {
				// error
				System.out.println("SYNTAX ERROR: unrecognized statement " + line);
				Utils.PrintParseError(file, start, end, curLine, curLine + 1);
				return "";
			}
		}

		// make sure all if and while statements were closed
		if (!nesting.empty()) {
			System.out.println("SYNTAX ERROR: " + nesting.pop() + " statement was entered, but never exited");
			Utils.PrintParseError(file, start, end, start, end);
			return "";
		}

		// if we get here we're done parsing and we didn't find any errors
		return sb.toString();
	}

	public static String ParseVarSet(String name, String val, boolean global, 
		Map<String, String> blockVars, String file, int start, int end, int curLine) {

		String[] valParsed = ParseExpression(val, blockVars);
		if (valParsed[0].equals("")) {
			// error
			System.out.println("SYNTAX ERROR: unable to parse expression " + val);
			Utils.PrintParseError(file, start, end, curLine, curLine + 1);
			return "";
		}
		
		String type = "";
		if (blockVars.containsKey(name)) {
			type = blockVars.get(name);
		} else if (globalVars.containsKey(name)) {
			type = globalVars.get(name);
		}

		if (type.equals("") && global) {
			globalVars.put(name, valParsed[0]);
			return name + " = " + valParsed[1];
		} else if (type.equals("")) {
			blockVars.put(name, valParsed[0]);
			return valParsed[0] + " " + name + " = " + valParsed[1];
		} else if (type.equals(valParsed[0])) {
			return name + " = " + valParsed[1];
		} else {
			// error
			System.out.println("SYNTAX ERROR: type mismatch: " + name + " is defined as type "
				+ type + ", but " + valParsed[1] + " is type " + valParsed[0]);
			Utils.PrintParseError(file, start, end, curLine, curLine + 1);
			return "";
		}
	}

	public static String ParseFunctionCall(String input) {
		return "";
	}

	public static String ParseEvalExpr(String input, Map<String, String> blockVars) {
		StringBuilder sb = new StringBuilder();
		String[] tokens = input.split("((?<=\\(|\\)|\\band|\\bor|\\bnot)|(?=\\(|\\)|\\band|\\bor|\\bnot))");
		Stack<Boolean> parens = new Stack<>();
		String prev = "";
		boolean prevWasOp = false;

		for (int i = 0; i < tokens.length; i++) {
			if (Pattern.compile("\\s").matcher(tokens[i]).matches()) {
				continue;
			} else if (tokens[i].equals("(")) {
				parens.push(true);
				sb.append("(");
			} else if (tokens[i].equals(")")) {
				if (parens.empty()) {
					// error unbalanced parens
					return "";
				}
				parens.pop();
				sb.append(")");
			} else if (tokens[i].equals("and") || tokens[i].equals("or")) {
				if (i <= 0 || i + 1 >= tokens.length 
				|| tokens[i - 1].equals("and") || tokens[i - 1].equals("or") || tokens[i - 1].equals("not")
				|| tokens[i + 1].equals("and") || tokens[i + 1].equals("or")) {

					// error invalid operands to binary op
					return "";
				}

				if (tokens[i].equals("and")) {
					sb.append("&&");
				} else {
					sb.append("||");
				}
			} else if (tokens[i].equals("not")) {
				if (i + 1 >= tokens.length || tokens[i + 1].equals("and") || tokens[i + 1].equals("or")
					|| tokens[i + 1].equals("not")) {
					
					// error invalid operand to unary op
					return "";
				}

				sb.append("!");
			} else {
				String[] var = CheckVariable(tokens[i], blockVars);
				String[] val = ParseValue(tokens[i]);
				String eq = ParseEqualityExpr(tokens[i], blockVars);
				if (var[0].equals("boolean")) {
					sb.append(var[1]);
				} else if (val[0].equals("boolean")) {
					sb.append(val[1]);
				} else if (!eq.equals("")) {
					sb.append(eq);
				} else {
					// not a boolean
					return "";
				}
			}
		}

		if (parens.empty()) {
			return sb.toString();
		} else {
			// error unbalanced parens
			return "";
		}
	}

	public static String ParseEqualityExpr(String input, Map<String, String> blockVars) {
		String[] operands = new String[]{};
		String op = "";
		String[] possibleOps = new String[]{"<=", ">=", "=", "<", ">"};
		for (String o : possibleOps) {
			if (input.contains(o)) {
				operands = input.split(o);
				if (o.equals("=")) {
					op = "==";
				} else {
					op = o;
				}
				break;
			}
		}

		if (op.equals("") || operands.length != 2) {
			// error
			return "";
		}

		String[] left = ParseExpression(operands[0].trim(), blockVars);
		String[] right = ParseExpression(operands[1].trim(), blockVars);

		if (left[0].equals("") || right[0].equals("")) {
			// error parsing one side
			return "";
		}

		// if we get here we're good
		return left[1] + op + right[1];
	}

	public static String[] ParseMathExpr(String input, Map<String, String> blockVars) {
		StringBuilder sb = new StringBuilder();
		Pattern validate = Pattern.compile("^[()+\\-*/%A-Za-z0-9\s]+$");
		Matcher validateM = validate.matcher(input);
		if (!validateM.matches()) {
			// error non-mathematic characters present
			return new String[]{"", ""};
		}

		Pattern p = Pattern.compile("\\(|\\)|\\+|-|\\*|\\/|%|[0-9]+|[A-Za-z0-9]+|[0-9]+\\.[0-9]+");
		Matcher m = p.matcher(input);
		Pattern atomPattern = Pattern.compile("^[0-9]+$|^[A-Za-z0-9]+$|^[0-9]\\.[0-9]$");
		Stack<Boolean> parens = new Stack<>();
		String type = "int";
		String prev = "";
		boolean prevWasOp = false;
		String[] var;
		String[] val;

		while(m.find()) {
			var = CheckVariable(m.group(), blockVars);
			val = ParseValue(m.group());
			if (prevWasOp) {
				Matcher atom = atomPattern.matcher(m.group());
				if (var[0].equals("") && val[0].equals("") && !m.group().equals("(")) {
					// error illegal operator placement
					return new String[]{"", ""};
				}
				prevWasOp = false;
			}

			if (m.group().equals("(")) {
				parens.push(true);
			} else if (m.group().equals(")")) {
				if (parens.empty()) {
					// error unbalanced parens
					return new String[]{"", ""};
				}
				parens.pop();
			} else if (var[0].equals("int") || val[0].equals("int")) {
				// nop
			} else if (var[0].equals("double") || val[0].equals("double")) {
				type = "double";
			} else if (!var[0].equals("") && !val[0].equals("")) {
				// error non-numeric value
				return new String[]{"", ""};
			} else {
				// operator -- make sure prev is an atom or close paren and next is an atom or open paren
				Matcher atom = atomPattern.matcher(prev);
				if (!atom.matches() && !prev.equals(")")) {
					// error illegal operator placement
					return new String[]{"", ""};
				}
				prevWasOp = true;
			}

			sb.append(m.group());
			prev = m.group();
		}

		if (parens.empty()) {
			return new String[]{type, sb.toString()};
		} else {
			// error unbalanced parens
			return new String[]{"", ""};
		}
	}

	public static String ParseReturnStmt(String input, Map<String, String> blockVars,
		Function fn, String file, int start, int end, int curLine) {

		String[] parsed = ParseExpression(input, blockVars);
		
		if (parsed[0].equals("")) {
			// error
			System.out.println("SYNTAX ERROR: invalid expression in return statement");
			Utils.PrintParseError(file, start, end, curLine, curLine + 1);
			return "";
		}

		if (fn.returnType.equals("void") || fn.returnType.equals(parsed[0])) {
			fn.returnType = parsed[0];
			return parsed[1];
		} else if (fn.returnType.equals("double") && parsed[0].equals("int")) {
			return parsed[1];
		} else {
			// error return type mismatch
			System.out.println("SYNTAX ERROR: function return type has already been defined as " 
				+ fn.returnType + ", but return statement received an incompatible value");
			Utils.PrintParseError(file, start, end, curLine, curLine + 1);
			return "";
		}
	}

	public static String ParseConsoleWrite(String input, Map<String, String> blockVars, 
		String file, int start, int end, int curLine) {
		
		String[] parsed = ParseExpression(input, blockVars);
		
		if (parsed[0].equals("")) {
			// error
			System.out.println("SYNTAX ERROR: invalid expression in print statement");
			Utils.PrintParseError(file, start, end, curLine, curLine + 1);
			return "";
		} else {
			return parsed[1];
		}
	}

	public static String[] ParseExpression(String input, Map<String, String> blockVars) {
		input = input.trim();
		String[] ret = new String[]{"", ""};

		String[] var = CheckVariable(input, blockVars);
		String[] math = ParseMathExpr(input, blockVars);
		String eval = ParseEvalExpr(input, blockVars);
		String[] val = ParseValue(input);
		
		if (!var[0].equals("")) {
			ret = var;
		} else if (!math[0].equals("")) {
			ret = math;
		} else if (!eval.equals("")) {
			ret[0] = "boolean";
			ret[1] = eval;
		} else if (!val[0].equals("")) {
			ret = val;
		}

		return ret;
	}

	public static String[] CheckVariable(String input, Map<String, String> blockVars) {
		input = input.trim();
		String[] ret = new String[]{"", ""};
		if (globalVars.containsKey(input)) {
			ret[0] = globalVars.get(input);
			ret[1] = input;
		} else if (blockVars.containsKey(input)) {
			ret[0] = blockVars.get(input);
			ret[1] = input;
		}
		return ret;
	}

	public static String[] ParseValue(String input) {
		input = input.trim();
		String[] ret = new String[]{"", input};
		if (Pattern.compile("[0-9]+").matcher(input).matches()) {
			ret[0] = "int";
		} else if (Pattern.compile("[0-9]+\\.[0-9]+").matcher(input).matches()) {
			ret[0] = "double";
		} else if (Pattern.compile("^\".*\"$").matcher(input).matches()) {
			ret[0] = "String";
		} else if (input.equals("true") || input.equals("false")) {
			ret[0] = "boolean";
		} else {
			ret[1] = "";
		}
		return ret;
	}
	
	/* -------------------------------------------------------------------------- */
	/*                               Data Structures                              */
	/* -------------------------------------------------------------------------- */

	static class Function{

		public String name;				// The name of the function
		public List<Arg> parameters;		// The parameters of the function
		public String body;				// The body of the function
		public int lineNumber;			// The line number of the function call 
		public String returnType;

		public Function(String name, List<Arg> parameters, String body, int lineNumber){
			this.name = name;
			this.parameters = parameters;
			this.body = body;
			this.lineNumber = lineNumber;
			this.returnType = "void";
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