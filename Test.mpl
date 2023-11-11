
Begin a function called pie.

	# Boxey
	Create a Box called Boxey.
	Move Boxey to 100.0 and 100.0.
	Set the size of Boxey to 200 and 500.
	Set the color of Boxey to (255, 0, 0, 50).

	# cirk
	Create a global Circle called cirk.
	Set the radius of cirk to 30.
	Set global xPos to 0.
	
	# Line
	Create a global Line called line1.
	Set global line1lx to 0.
	Set global line1ly to 0.
	Set global line1rx to 1920.
	Set global line1ry to 1080.
	Set the size of line1 to 10.

	# Text
	Create a Text called text1.
	Set the text of text1 to "Hello World!" + "  Goodbye!".
	Move text1 to 100 and 100.
	Set the size of text1 to 80.

	# Errors?
	# Move Boxey to 9*4 and 7wasdwasd. 		# ParseExpression(7wasdwasd, blockVars) fails to report the correct type. (it claims its an int or double?)
	# Set xPos to xPos + 3.0.				# ParseExpression(x, blockVars) fails to allow doubles when adding.
	# Set str1 to "Hello".
	# Set str2 to "World".
	# Set str3 to str1 + str2.				# Can't add 2 string vars
	# Print str3 to the console.


	# Remove Pie from the canvas.
Leave the function.




Begin a function called Gameloop. 
	Set global xPos to xPos + 2.
	Move cirk to xPos and 500.

	# Move beginning of line right and end left
	Set line1lx to line1lx + 3.
	Set line1rx to line1rx - 3.

	Set the chords of line1 to (line1lx, line1ly) and (line1rx, line1ry).
Leave the function.