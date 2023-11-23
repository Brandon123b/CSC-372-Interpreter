
# ---------------------------------------------------------------------------- #
#                                   Overview                                   #
# ---------------------------------------------------------------------------- #

This language is a GUI based language. Every time a program is ran, it will automatically open a canvas. This is how the language has
been structured and I would implore you to make use of it properly. There is a list of commands in "Commands.txt". I reccomend ending
the file with ".mpl" (my programming language) so the makefile will work with it. (more below on make) All the possible commands are
listed in Commands.txt. This language was designed to more closely resemble regular english. This is why the commands are so long and
are case sensitive. The language is also (sometimes) whitespace sensitive. This means that you cannot have any whitespace in the middle 
of a command other than a single space.

# ---------------------------------------------------------------------------- #
#                                   MakeFile                                   #
# ---------------------------------------------------------------------------- #

The makefile is simple, but has 3 main commands. All these commands assume that you have a file ending with ".mpl" in the same directory.
You may also interpret your program without using make, but is somewhat more effort to do so. Unfortunaltey, the makefile does not directly 
support command line arguements withing the program. You can however add the args under the makefile variable "ARGS" to allow them to work 
for "make run". {filename} is the name of the file you want to run without the ".mpi" extension. If a file is in a subdirectory, you must
include the subdirectory in the filename. ("make ExamplePrograms/Test1")

"make" -- This will compile the interpreter only into the "bin" directory. This will not run the program.

"make {filename}.comp" -- This will make the interpreter and interpret the given file into {filename}.java. This will not run the program.

"make {filename}.class" -- This will make the interpreter, interpret the given file into {filename}.java, and compile the java file into the
						   bin directory. This will not run the program. The program should be ran with "java -cp bin {filename}". (Assuming 
						   you are in the root directory bin is the correct path)

(The most important one)
"make {filename}" -- This will make the interpreter, interpret the given file into {filename}.java, compile the java file into the bin 
					 directory, and run the program. This immediately runs the program and is the most useful command. I reccomend running 
					 the program often when writing it to make sure it is working as intended.

(Assuming you are in the root directory)
Example:
	"make World" -- This will run the world program in the ExamplePrograms directory. This is a cool-ish program. I reccomend
					looking at it to see how the language works. It is a good example of how to use the language and what can be made.
					"make World" is the same as "make ExamplePrograms/World" except that make World has command line args and cannot
					be used without adding them to the makefile (random world seed).

# ---------------------------------------------------------------------------- #
#                             Other Important Info                             #
# ---------------------------------------------------------------------------- #

Main:
	The first function is automatically called at program start. Any parameters passed to the program will be passed to the main function.
	If the main function takes 2 integer parameter, the interpreted program must be ran with 2 integer parameters.

Gameloop: 
	The Gameloop is an optional function. Any function called Gameloop will be automatically called every frame. This is useful for animations 
	and games. The gameloop function is the only function that will be called after the initial program is ran. This means that it must be Used
	to allow any code to run after the first frame. Basically, not using Gameloop means a boring program that only runs once. 

Functions:
	Functions can be declaired and called in any order. This means that you can call a function before it is defined. All code must be inside a
	function. This means that you cannot have any code outside of a function. (Comments are allowed outside of functions)

Global vars:
	Global vars are parsed from top to bottom. This means that you cannot use a global var before it is defined. If you need to access a global
	var in a function higher up in the file, you must define the global var before the function. It might be a good idea to define all global vars
	in one of the earlier functions.

Errors:
	The errors for GUI commands are very through. They will tell you exactly what is wrong with the command, what it was expecting and on what 
	line it failed. This is very useful for debugging. The errors for general commands may not be as great. I have noticed that the typeing of Variables
	can be quite strict and may not explain what is wrong with the command. If you encounter an error that you do not understand, I reccomend looking
	at the types of the variables and making sure they are correct. Ints can be coverted to a double by adding 0.0 to the end of the int. (ex: 5+0.0).

# ---------------------------------------------------------------------------- #
#                                Recomendations                                #
# ---------------------------------------------------------------------------- #

Test often:
	It is very easy to make a simple mistake in this language. I recomend testing your program often to make sure it is working as intended.

Look at the top error first:
	The program might skip a line if an error occurs, potentially causing subsequent errors. I recommend addressing the first error first to ensure 
	it's fixed before examining other errors. Often, resolving the initial error can automatically resolve the others.

Negatives are not supported:
	There is no support for negative numbers. This is because some reason I assume? Use 0-{number} instead of -{number}. This is a simple fix that works well.
