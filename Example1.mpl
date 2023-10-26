

# A simple fib example (First function so cmd arg 1 will be N)
Begin a function called Fib with type int called N.
	if N = 0 is true, then
		Return 0 to the caller.
	Leave the if statement.
	if N = 1 is true, then
		Return 1 to the caller.
	Leave the if statement.

	# Doing it recursively
	Set an int called RetVal to call the function Fib with N-2 +
								call the function Fib with N-1.
	
	Print "Fib(" + n + ") is" + RetVal.

	Return RetVal to the caller.

Leave the function.