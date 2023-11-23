
/* hi1 */
/* hi2 */
/* hi3 */

Begin a function called Init.

	# Create a background (static color)
	Create a global Box called backGround.
	Move backGround to 0 and 0.
	Set the size of backGround to 1920 and 1080.
	Set the color of backGround to (135, 206, 250, 255).

	# Create a sun
	Call the function CreateSun.
Leave the function.

Begin a function called CreateSun.
	
	# Sun vars
	Set global sunRadius to 115.
	Set global sunLinesCount to 24.
	Set global sunLinesGap to sunRadius + 25.
	Set global sunLineSize to 5.
	Set global sunLinesLength to 75.
	Set global sunPosx to 1600.
	Set global sunPosy to 200.

	# Create a sun at the top right of the screen
	Create a global Circle called sun.
	Set the radius of sun to sunRadius.
	Set the color of sun to (255, 255, 102, 255).
	Move sun to sunPosx and sunPosy.

	# OnClick
	When sun is clicked call ClickSun.

	# Create some sun lines
	Set index to sunLinesCount.
	While index > 0:
		Create a Line called sunLine.
		Set the size of sunLine to sunLineSize.
		Set the color of sunLine to (255, 255, 102, 255).
		Add sunLine to global sunLinesList.

		Set index to index - 1.
	Exit the while.
	
	# Used when spacing the sun lines
	Set global cos15 to 0.96592582628.
	Set global sin15 to 0.2588190451.

	# Used for animation
	Set global cos1 to 0.99984769515639123915701155881391.
	Set global sin1 to 0.01745240643728351281941897851632.
	
	# For animation
	Set global sunLineX to 0.0.
	Set global sunLineY to 1.0.
Leave the function.

Begin a function called AnimateSun.

	# Animate the sun lines
	Set global sunLineX to sunLineX * cos1 - sunLineY * sin1.
	Set global sunLineY to sunLineX * sin1 + sunLineY * cos1.

	# Local vars for rotation
	Set x to sunLineX.
	Set y to sunLineY.

	# Set the position of the sun lines
	Set index to 0.
	While index < sunLinesCount:
		Set sunLine to index index of sunLinesList.
		Set newLineLength to sunLinesLength.

		If index % 2 = 0, then:
			Set newLineLength to newLineLength * 2.
		Leave the if statement.

		Set tempx to x.
		Set x to x * cos15 - y * sin15.
		Set y to tempx * sin15 + y * cos15.
		Set startX to sunPosx + x * sunLinesGap.
		Set startY to sunPosy + y * sunLinesGap.
		Set the chords of sunLine to (startX, startY) and (startX + x * newLineLength, startY + y * newLineLength).

		Set index to index + 1.
	Exit the while.
Leave the function.

Begin a function called Gameloop. 

	Call the function AnimateSun.
Leave the function.

Begin a function called ClickSun with a Circle called tempSun.

	# If the sun is clicked, change the color of the sun
	Set the color of sun to (0, 0, 0, 255).

	# Set all sunlines to Red
	Set index to 0.
	While index < sunLinesCount:
		Set sunLine to index index of sunLinesList.
		Set the color of sunLine to (255, 0, 0, 255).

		Set index to index + 1.
	Exit the while.
Leave the function.