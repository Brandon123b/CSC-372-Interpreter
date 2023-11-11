
Begin a function called Init.

	# Create a background (static color)
	Create a global Box called backGround.
	Move backGround to 0 and 0.
	Set the size of backGround to 1920 and 1080.
	Set the color of backGround to (135, 206, 250, 255).

	# Sun vars
	Set global sunRadius to 115.
	Set global sunLinesCount to 12.
	Set global sunLinesGap to sunRadius + 25.
	Set global sunLinesLength to 150.
	Set global sunPosx to 1600.
	Set global sunPosy to 200.

	# Create a sun at the top right of the screen
	Create a global Circle called sun.
	Set the radius of sun to sunRadius.
	Set the color of sun to (255, 255, 102, 255).
	Move sun to sunPosx and sunPosy.

	# Create some sun lines
	Create a global Line called sunLines1.
	Create a global Line called sunLines2.
	Create a global Line called sunLines3.
	Create a global Line called sunLines4.
	Create a global Line called sunLines5.
	Create a global Line called sunLines6.
	Create a global Line called sunLines7.
	Create a global Line called sunLines8.
	Create a global Line called sunLines9.
	Create a global Line called sunLines10.
	Create a global Line called sunLines11.
	Create a global Line called sunLines12.

	Set lineSize to 5.
	Set the size of sunLines1 to lineSize.
	Set the size of sunLines2 to lineSize.
	Set the size of sunLines3 to lineSize.
	Set the size of sunLines4 to lineSize.
	Set the size of sunLines5 to lineSize.
	Set the size of sunLines6 to lineSize.
	Set the size of sunLines7 to lineSize.
	Set the size of sunLines8 to lineSize.
	Set the size of sunLines9 to lineSize.
	Set the size of sunLines10 to lineSize.
	Set the size of sunLines11 to lineSize.
	Set the size of sunLines12 to lineSize.

	# Set the color of the sun lines
	Set the color of sunLines1 to (255, 255, 102, 255).
	Set the color of sunLines2 to (255, 255, 102, 255).
	Set the color of sunLines3 to (255, 255, 102, 255).
	Set the color of sunLines4 to (255, 255, 102, 255).
	Set the color of sunLines5 to (255, 255, 102, 255).
	Set the color of sunLines6 to (255, 255, 102, 255).
	Set the color of sunLines7 to (255, 255, 102, 255).
	Set the color of sunLines8 to (255, 255, 102, 255).
	Set the color of sunLines9 to (255, 255, 102, 255).
	Set the color of sunLines10 to (255, 255, 102, 255).
	Set the color of sunLines11 to (255, 255, 102, 255).
	Set the color of sunLines12 to (255, 255, 102, 255).

	Set global cos30 to 0.866.
	Set global sin30 to 0.5.

	Set global cos1 to 0.99984769515639123915701155881391.
	Set global sin1 to 0.01745240643728351281941897851632.
	
	# For animation
	Set global sunLineX to 0.0.
	Set global sunLineY to 1.0.

Leave the function.




Begin a function called Gameloop. 

	# Animate the sun lines
	Set global sunLineX to sunLineX * cos1 - sunLineY * sin1.
	Set global sunLineY to sunLineX * sin1 + sunLineY * cos1.

	# Local vars for rotation
	Set x to sunLineX.
	Set y to sunLineY.

	# Rotates the sun lines by 30 degrees each time
	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines1 to (startX, startY) and (startX + x * sunLinesLength, startY + y * sunLinesLength).

	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines2 to (startX, startY) and (startX + x * sunLinesLength / 2, startY + y * sunLinesLength / 2).
	
	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines3 to (startX, startY) and (startX + x * sunLinesLength, startY + y * sunLinesLength).

	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines4 to (startX, startY) and (startX + x * sunLinesLength / 2, startY + y * sunLinesLength / 2).
	
	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines5 to (startX, startY) and (startX + x * sunLinesLength, startY + y * sunLinesLength).

	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines6 to (startX, startY) and (startX + x * sunLinesLength / 2, startY + y * sunLinesLength / 2).
	
	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines7 to (startX, startY) and (startX + x * sunLinesLength, startY + y * sunLinesLength).

	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines8 to (startX, startY) and (startX + x * sunLinesLength / 2, startY + y * sunLinesLength / 2).
	
	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines9 to (startX, startY) and (startX + x * sunLinesLength, startY + y * sunLinesLength).

	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines10 to (startX, startY) and (startX + x * sunLinesLength / 2, startY + y * sunLinesLength / 2).
	
	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines11 to (startX, startY) and (startX + x * sunLinesLength, startY + y * sunLinesLength).

	Set tempx to x.
	Set x to x * cos30 - y * sin30.
	Set y to tempx * sin30 + y * cos30.
	Set startX to sunPosx + x * sunLinesGap.
	Set startY to sunPosy + y * sunLinesGap.
	Set the chords of sunLines12 to (startX, startY) and (startX + x * sunLinesLength / 2, startY + y * sunLinesLength / 2).
	
Leave the function.