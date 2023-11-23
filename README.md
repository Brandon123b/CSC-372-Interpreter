
# ---------------------------------------------------------------------------- #
#                                   Overview                                   #
# ---------------------------------------------------------------------------- #

This language is a GUI based language. Every time a program is ran, it will automatically open a canvas. This is how the language has
been structured and I would implore you to make use of it properly.. There is a list of commands in "Commands.txt". I reccomend ending
the file with ".mpl" (my programming language) so the makefile will work with it. (more below on make) All the possible commands are
listed in Commands.txt. This language was designed to more closely resemble regular english. This is why the commands are so long and
are case sensitive. The language is also (sometimes) whitespace sensitive. This means that you cannot have any whitespace in the middle 
of a command other than a single space.

# ---------------------------------------------------------------------------- #
#                                   MakeFile                                   #
# ---------------------------------------------------------------------------- #

The makefile is simple, but has 3 main commands. All these commands assume that you have a file ending with ".mpl" in the same directory.
You may also interpret your program without using make, but is somewhat more effort to do so. Unfortunaltey, the makefile does not directly 
support command line arguements withing the program. You can however add the args under the makefile variable "ARGS" to allow them to work.
arguements are only for "make run". {filename} is the name of the file you want to run without the ".mpi" extension.

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
	"make ExamplePrograms/World" -- This will run the world program in the ExamplePrograms directory. This is a cool-ish program.

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