
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

	static Map<String, String> globalVars = new HashMap<>();

	public static void main(String[] args) {

		Test();

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
		// Replace the function declarations
		template = template.replace("/* {Functions} */", functionDecl.toString());

		if (hasGameloop(functions)) 
			template = template.replace("/* {Call_GameLoop} */", "Gameloop();");
			
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

	// Returns true if there is a function called "Gameloop"
	public static boolean hasGameloop(List<Function> functions){

		for (Function function : functions) {
			if (function.name.equals("Gameloop"))
				return true;
		}

		return false;
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

				// Try a GUI statement
				String guiStmt = ParseGUIStatement(line, blockVars, indent, curLine);

				// GUI statement found
				if (guiStmt != null && !guiStmt.equals("")) {
					sb.append(guiStmt);
					continue;
				}

				// error
				System.out.println("SYNTAX ERROR: unrecognized statement \"" + line + "\"");
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
		Pattern validate = Pattern.compile("^[()+\\-*/%A-Za-z0-9\\s]+$");
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
	
	/* ----------------------------- GUI Statements ----------------------------- */
	
	static Pattern patternStmtCreate = Pattern.compile("^Create a (.+) called (.+)$");
	static Pattern patternStmtCreateGlobal = Pattern.compile("^Create a global (.+) called (.+)$");
	static Pattern patternStmtMove = Pattern.compile("^Move (.+) to (.+) and (.+)$");
	static Pattern patternStmtRemove = Pattern.compile("^Remove (.+) from the canvas$");
	static Pattern patternSetColor = Pattern.compile("^Set the color of (.+) to \\((.+), (.+), (.+), (.+)\\)$");
	static Pattern patternStmtSetCircleRadius = Pattern.compile("^Set the radius of (.+) to (.+)$");
	static Pattern patternStmtSetBoxSize = Pattern.compile("^Set the size of (.+) to (.+) and (.+)$");
	static Pattern patternStmtSetLine = Pattern.compile("^Set the chords of (.+) to \\((.+), (.+)\\) and \\((.+), (.+)\\)$"); // (x1, y1) and (x2, y2)
	static Pattern patternStmtSetText = Pattern.compile("^Set the text of (.+) to (.+)$");
	static Pattern patternStmtSetSize = Pattern.compile("^Set the size of (.+) to (.+)$");

	// Takes a single statement and parces some GUI statement (May return "" if no match and " " if error)
	public static String ParseGUIStatement(String input, Map<String, String> blockVars, String indent, int start) {
		String output;

		output = StmtCreateGlobal(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;

		output = StmtCreate(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
			
		output = StmtMove(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
		
		output = StmtRemove(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
		
		output = StmtSetColor(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
		
		// Circle
		output = StmtSetCircleRadius(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;

		// Box
		output = StmtSetBoxSize(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
		
		// Line
		output = StmtSetLine(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
		
		// Text
		output = StmtSetText(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;
		output = StmtSetSize(input, blockVars, indent, start);
		if (!output.equals(""))
			return output;

		return "";
	}

	// Parses a "Create" statement
	public static String StmtCreate(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtCreate.matcher(input.trim());

		// This is not a "Create" statement
		if (!matcher.find())
			return "";

		String type = ParseGuiType(matcher.group(1), start);
		String name = matcher.group(2);

		// Type is not valid
		if (type.equals("")){
			System.out.println("Error in parsing Create statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid GUI type \"%s\"\n", start, matcher.group(1)));
			return " ";
		}

		// Test if the var exisis
		String[] CheckVariable = CheckVariable(name, blockVars);

		String addToGUI = String.format(indent + "drawableObjects_.add(%s);\n", name);	

		// Either declaires a new type, or assigns a value to an existing variable
		if (CheckVariable[0].equals("")) {
			blockVars.put(name, type);
			return indent + type + " " + name + " = new " + type + "();\n" + addToGUI;
		}
		else{
			return indent + name + " = new " + type + "();\n" + addToGUI;
		}
	}
	
	// Parses a "Create" statement
	public static String StmtCreateGlobal(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtCreateGlobal.matcher(input.trim());

		// This is not a "Create Global" statement
		if (!matcher.find())
			return "";

		String type = ParseGuiType(matcher.group(1), start);
		String name = matcher.group(2);

		// Type is not valid
		if (type.equals("")){
			System.out.println("Error in parsing Create Global statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid GUI type \"%s\"\n", start, matcher.group(1)));
			return " ";
		}

		// Add a global var if needed
		if (!globalVars.containsKey(name)){
			globalVars.put(name, type);
		}

		String addToGUI = String.format(indent + "drawableObjects_.add(%s);\n", name);	
		return indent + name + " = new " + type + "();\n" + addToGUI;
	}

	// Parses a "Move" statement
	public static String StmtMove(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtMove.matcher(input.trim());

		// This is not a "Move" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String x = matcher.group(2);
		String y = matcher.group(3);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Move statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a GUI type
		if (ParseGuiType(checkVariable[0], start).equals("")){
			System.out.println("Error in parsing Move statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a GUI type\n", start, name));
			return " ";
		}

		// Validate the position
		String[] xParsed = ParseExpression(x, blockVars);
		String[] yParsed = ParseExpression(y, blockVars);

		// Check if the x position is valid
		if (xParsed[0].equals("")){
			System.out.println("Error in parsing Move statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid position in expression", start));
			System.out.println(String.format("  Given X value: \"%s\"\n", x));
			return " ";
		}

		// Check if the y position is valid
		if (yParsed[0].equals("")){
			System.out.println("Error in parsing Move statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid position in expression", start));
			System.out.println(String.format("  Given Y value: \"%s\"\n", y));
			return " ";
		}

		// Test the x type
		if (!xParsed[0].equals("int") && !xParsed[0].equals("double")){
			System.out.println("Error in parsing Move statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for X position", start));
			System.out.println(String.format("  Given X value: \"%s\"\n", x));
			return " ";
		}

		// Test the y type
		if (!yParsed[0].equals("int") && !yParsed[0].equals("double")){
			System.out.println("Error in parsing Move statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for Y position", start));
			System.out.println(String.format("  Given Y value: \"%s\"\n", y));
			return " ";
		}

		// Move the GUI object
		return indent + name + ".moveTo((int)" + xParsed[1] + ", (int)" + yParsed[1] + ");\n";
	}

	// Parses a "remove" statement
	public static String StmtRemove(String input, Map<String, String> blockVars, String indent, int start){
		
		Matcher matcher = patternStmtRemove.matcher(input.trim());

		// This is not a "Remove" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Remove statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a GUI type
		if (ParseGuiType(checkVariable[0], start).equals("")){
			System.out.println("Error in parsing Remove statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a GUI type\n", start, name));
			return " ";
		}

		// Remove the GUI object
		return indent + "drawableObjects_.remove(" + name + ");\n";
	
	}

	// Parses a "Set color" statement
	public static String StmtSetColor(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternSetColor.matcher(input.trim());

		// This is not a "Set color" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String r = matcher.group(2);
		String g = matcher.group(3);
		String b = matcher.group(4);
		String a = matcher.group(5);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a GUI type
		if (ParseGuiType(checkVariable[0], start).equals("")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a GUI type\n", start, name));
			return " ";
		}

		// Validate the color
		String[] rParsed = ParseExpression(r, blockVars);
		String[] gParsed = ParseExpression(g, blockVars);
		String[] bParsed = ParseExpression(b, blockVars);
		String[] aParsed = ParseExpression(a, blockVars);

		// Check if the red value is valid
		if (rParsed[0].equals("")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid red value in expression", start));
			System.out.println(String.format("  Given red value: \"%s\"\n", r));
			return " ";
		}

		// Check if the green value is valid
		if (gParsed[0].equals("")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid green value in expression", start));
			System.out.println(String.format("  Given green value: \"%s\"\n", g));
			return " ";
		}

		// Check if the blue value is valid
		if (bParsed[0].equals("")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid blue value in expression", start));
			System.out.println(String.format("  Given blue value: \"%s\"\n", b));
			return " ";
		}

		// Check if the alpha value is valid
		if (aParsed[0].equals("")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid alpha value in expression", start));
			System.out.println(String.format("  Given alpha value: \"%s\"\n", a));
			return " ";
		}

		// Test the red type
		if (!rParsed[0].equals("int") && !rParsed[0].equals("double")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for red value", start));
			System.out.println(String.format("  Given red value: \"%s\"\n", r));
			return " ";
		}

		// Test the green type
		if (!gParsed[0].equals("int") && !gParsed[0].equals("double")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for green value", start));
			System.out.println(String.format("  Given green value: \"%s\"\n", g));
			return " ";
		}

		// Test the blue type
		if (!bParsed[0].equals("int") && !bParsed[0].equals("double")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for blue value", start));
			System.out.println(String.format("  Given blue value: \"%s\"\n", b));
			return " ";
		}

		// Test the alpha type
		if (!aParsed[0].equals("int") && !aParsed[0].equals("double")){
			System.out.println("Error in parsing Set color statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for alpha value", start));
			System.out.println(String.format("  Given alpha value: \"%s\"\n", a));
			return " ";
		}

		// Set the color
		return indent + name + ".setColor(new Color((int)" + rParsed[1] + ", (int)" + gParsed[1] + ", (int)" + bParsed[1] + ", (int)" + aParsed[1] + "));\n";
	}

	// Parses a "Set radius" statement
	public static String StmtSetCircleRadius(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtSetCircleRadius.matcher(input.trim());

		// This is not a "Set radius" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String radius = matcher.group(2);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Set radius statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a Circle
		if (!checkVariable[0].equals("Circle")){
			System.out.println("Error in parsing Set radius statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a Circle type\n", start, name));
			return " ";
		}

		// Validate the radius
		String[] radiusParsed = ParseExpression(radius, blockVars);

		// Check if the radius is valid
		if (radiusParsed[0].equals("")){
			System.out.println("Error in parsing Set radius statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid radius in expression", start));
			System.out.println(String.format("  Given radius value: \"%s\"\n", radius));
			return " ";
		}

		// Test the radius type
		if (!radiusParsed[0].equals("int") && !radiusParsed[0].equals("double")){
			System.out.println("Error in parsing Set radius statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for radius \"%s\"", start, radiusParsed[0]));
			System.out.println(String.format("  Given radius value: \"%s\"\n", radius));
			return " ";
		}

		// Set the radius
		return indent + name + ".setRadius((int)" + radiusParsed[1] + ");\n";
	
	}

	// Parses a "Set size" statement
	public static String StmtSetBoxSize(String input, Map<String, String> blockVars, String indent, int start) {

		Matcher matcher = patternStmtSetBoxSize.matcher(input.trim());

		// This is not a "Set size" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String width = matcher.group(2);
		String height = matcher.group(3);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Set size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a Box
		if (!checkVariable[0].equals("Box")){
			System.out.println("Error in parsing Set size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a Box type\n", start, name));
			return " ";
		}

		// Validate the width and height
		String[] widthParsed = ParseExpression(width, blockVars);
		String[] heightParsed = ParseExpression(height, blockVars);

		// Test the width type
		if (!widthParsed[0].equals("int") && !widthParsed[0].equals("double")){
			System.out.println("Error in parsing Set size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for width \"%s\"", start, widthParsed[0]));
			System.out.println(String.format("  Given width value: \"%s\"\n", width));
			return " ";
		}

		// Test the height type
		if (!heightParsed[0].equals("int") && !heightParsed[0].equals("double")){
			System.out.println("Error in parsing Set size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for height \"%s\"", start, heightParsed[0]));
			System.out.println(String.format("  Given height value: \"%s\"\n", height));
			return " ";
		}

		// Set the size
		return indent + name + ".setSize((int)" + widthParsed[1] + ", (int)" + heightParsed[1] + ");\n";
	}

	// Parses a "Set line" statement
	public static String StmtSetLine(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtSetLine.matcher(input.trim());

		// This is not a "Set line" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String x1 = matcher.group(2);
		String y1 = matcher.group(3);
		String x2 = matcher.group(4);
		String y2 = matcher.group(5);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a Line
		if (!checkVariable[0].equals("Line")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a Line type\n", start, name));
			return " ";
		}

		// Validate the positions
		String[] x1Parsed = ParseExpression(x1, blockVars);
		String[] y1Parsed = ParseExpression(y1, blockVars);
		String[] x2Parsed = ParseExpression(x2, blockVars);
		String[] y2Parsed = ParseExpression(y2, blockVars);

		// Check if the x1 position is valid
		if (x1Parsed[0].equals("")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid position in expression", start));
			System.out.println(String.format("  Given X1 value: \"%s\"\n", x1));
			return " ";
		}

		// Check if the y1 position is valid
		if (y1Parsed[0].equals("")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid position in expression", start));
			System.out.println(String.format("  Given Y1 value: \"%s\"\n", y1));
			return " ";
		}

		// Check if the x2 position is valid
		if (x2Parsed[0].equals("")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid position in expression", start));
			System.out.println(String.format("  Given X2 value: \"%s\"\n", x2));
			return " ";
		}	

		// Check if the y2 position is valid
		if (y2Parsed[0].equals("")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid position in expression", start));
			System.out.println(String.format("  Given Y2 value: \"%s\"\n", y2));
			return " ";
		}

		// Test the x1 type
		if (!x1Parsed[0].equals("int") && !x1Parsed[0].equals("double")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for X1 position", start));
			System.out.println(String.format("  Given X1 value: \"%s\"\n", x1));
			return " ";
		}

		// Test the y1 type
		if (!y1Parsed[0].equals("int") && !y1Parsed[0].equals("double")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for Y1 position", start));
			System.out.println(String.format("  Given Y1 value: \"%s\"\n", y1));
			return " ";
		}

		// Test the x2 type
		if (!x2Parsed[0].equals("int") && !x2Parsed[0].equals("double")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for X2 position", start));
			System.out.println(String.format("  Given X2 value: \"%s\"\n", x2));
			return " ";
		}

		// Test the y2 type
		if (!y2Parsed[0].equals("int") && !y2Parsed[0].equals("double")){
			System.out.println("Error in parsing Set line statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for Y2 position", start));
			System.out.println(String.format("  Given Y2 value: \"%s\"\n", y2));
			return " ";
		}

		// Set the chords
		return indent + name + ".setLine((int)" + x1Parsed[1] + ", (int)" + y1Parsed[1] + ", (int)" + x2Parsed[1] + ", (int)" + y2Parsed[1] + ");\n";
	}

	// Parses a "Set text" statement
	public static String StmtSetText(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtSetText.matcher(input.trim());

		// This is not a "Set text" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String text = matcher.group(2);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Set text statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a Text
		if (!checkVariable[0].equals("Text")){
			System.out.println("Error in parsing Set text statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a Text type\n", start, name));
			return " ";
		}

		String[] textParsed = ParseExpression(text, blockVars);

		// Check if the text is valid
		if (textParsed[0].equals("")){
			System.out.println("Error in parsing Set text statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid text in expression", start));
			System.out.println(String.format("  Given text value: \"%s\"\n", text));
			return " ";
		}

		// Test the text type
		if (!textParsed[0].equals("String")){
			System.out.println("Error in parsing Set text statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for text \"%s\"", start, textParsed[0]));
			System.out.println(String.format("  Given text value: \"%s\"\n", text));
			return " ";
		}

		// Set the text
		return indent + name + ".setText(" + textParsed[1] + ");\n";
	}

	// Parses a "Set size" statement (Text size and Line thickness)
	public static String StmtSetSize(String input, Map<String, String> blockVars, String indent, int start) {
		
		Matcher matcher = patternStmtSetSize.matcher(input.trim());

		// This is not a "Set text size" statement
		if (!matcher.find())
			return "";
		
		String name = matcher.group(1);
		String size = matcher.group(2);

		String[] checkVariable = CheckVariable(name, blockVars);

		// Check if the variable exists
		if (checkVariable[0].equals("")) {
			System.out.println("Error in parsing Set text/line size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" does not exist\n", start, name));
			return " ";
		}

		// Check if the variable is a Text
		if (!checkVariable[0].equals("Text") && !checkVariable[0].equals("Line")){
			System.out.println("Error in parsing Set text/line size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Variable \"%s\" is not a Text/Line type\n", start, name));
			return " ";
		}

		// Validate the size
		String[] sizeParsed = ParseExpression(size, blockVars);

		// Check if the size is valid
		if (sizeParsed[0].equals("")){
			System.out.println("Error in parsing Set text/line size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid size in expression", start));
			System.out.println(String.format("  Given size value: \"%s\"\n", size));
			return " ";
		}

		// Test the size type
		if (!sizeParsed[0].equals("int") && !sizeParsed[0].equals("double")){
			System.out.println("Error in parsing Set text/line size statement:");
			System.out.println(String.format("  SYNTAX ERROR: (line %d) Invalid type for size \"%s\"", start, sizeParsed[0]));
			System.out.println(String.format("  Given size value: \"%s\"\n", size));
			return " ";
		}

		// Set the size
		return indent + name + ".setSize((int)" + sizeParsed[1] + ");\n";
	}

	public static void Test(){

		String output;

		// Test ParseExpression
		output = ParseExpression("wasdwasd", globalVars)[0];
		System.out.println("Expr: " + output);	// output: ""

		// Test ParseExpression
		output = ParseExpression("45wasdwasd", globalVars)[0];
		System.out.println("Expr: " + output);	// output: "int"

		// Test adding doubles
		output = ParseExpression("1.0 + 1.0", globalVars)[0];
		System.out.println("Expr: " + output);	// output: ""
	}

	/* ----------------------------- GUI Validation ----------------------------- */

	// Validates a GUI type
	public static String ParseGuiType(String type, int start){
		
		switch (type) {
			case "Box":
				return "Box";
			case "Circle":
				return "Circle";
			case "Line":
				return "Line";
			case "Text":
				return "Text";
		
			default:
				return "";
		}

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