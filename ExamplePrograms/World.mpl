
# ---------------------------------------------------------------------------- #
#                                     Init                                     #
# ---------------------------------------------------------------------------- #

Begin a function called Init with an int called _seed.

	Print _seed to the console.
	Set global seed to _seed.

	# Create a background (static color)
	Create a global Box called backGround.
	Move backGround to 0 and 0.
	Set the size of backGround to 1920 and 1080.
	Set the color of backGround to (135, 206, 250, 255).

	# Create a sun
	Call the function CreateSun.

	Call the function InitTerrain.

	Call the function InitPlayer.
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

Begin a function called InitTerrain.

	# Globals
	Set global BlockSize to 50.

	# The next block to load when visible
	Set global NextRightBlock to 0.
	Set global NextRightBlockHeight to 1080/BlockSize/2.
	Set global NextLeftBlock to 0-1.
	Set global NextLeftBlockHeight to 1080/BlockSize/2.

	# Return is broken in this language, so we use a global
	Set global intRet to 0.
Leave the function.

Begin a function called InitPlayer.

	# Create camera vars
	Set global camX to 0.0.
	Set global camY to 0.0.
	Set global camSpeed to 10.0.
	Set global lastCamX to 0.0.
	Set global lastCamY to 0.0.

	# Create player vars
	Set global pX to 0.0.
	Set global pY to 300.0.
	Set global pSpeed to 15.0.

	Create a global Box called player.
	Set the size of player to 50 and 50.
	Set the color of player to (255, 0, 0, 255).
Leave the function.

# ---------------------------------------------------------------------------- #
#                                  Animations                                  #
# ---------------------------------------------------------------------------- #

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

# ---------------------------------------------------------------------------- #
#                                  Generation                                  #
# ---------------------------------------------------------------------------- #

# If a new part is onscreen, then load a new colmn of blocks
Begin a function called LoadTerrain.

	# Load a Load a new right chunk if it is visible
	If NextRightBlock * BlockSize + 0.0 < camX + 1920/2 + BlockSize * 4, then:
		Call the function GenerateCol with NextRightBlock and NextRightBlockHeight.
		Set NextRightBlock to NextRightBlock + 1.
		Call the function Random with 3.
		Set intRet to intRet - 1.
		Set NextRightBlockHeight to NextRightBlockHeight + intRet.
	Leave the if statement.

	# Load a Load a new left chunk if it is visible
	If NextLeftBlock * BlockSize + 0.0 > camX - 1920/2 - BlockSize * 4, then:
		Call the function GenerateCol with NextLeftBlock and NextLeftBlockHeight.
		Set NextLeftBlock to NextLeftBlock - 1.
		Call the function Random with 3.
		Set intRet to intRet - 1.
		Set NextLeftBlockHeight to NextLeftBlockHeight + intRet.
	Leave the if statement.
Leave the function.

# Create a colmn of blocks at the given (x,y) choord in the grid 
Begin a function called GenerateCol with an int called XCol and an int called height.

	# Spawn blocks until it reaches the bottom of the screen
	Set depth to 0.
	While height < 1080:
		Call the function SpawnBlock with XCol, height, depth.
		Set height to height + 1.
		Set depth to depth + 1.
	Exit the while.
Leave the function.

# Spawns a single block	at the given x and y position (Depth is the amount of blocks above it)
Begin a function called SpawnBlock with an int called XPos, an int called YPos, and an int called depth.

	# Create a block
	Create a Box called block.
	Set the size of block to BlockSize + 1 and BlockSize + 1.	# Add 1 to prevent gaps
	Move block to XPos * BlockSize and YPos * BlockSize.

	# Top block is Green, then move from light brown to dark brown as it goes deeper
	If depth = 0, then:
		Set the color of block to (0, 200, 0, 255).
	Leave the if statement.
	If depth = 1 or depth = 2, then:
		Set the color of block to (166, 139, 113, 255).
	Leave the if statement.
	If depth = 3 or depth = 4, then:
		Set the color of block to (136, 103, 78, 255).
	Leave the if statement.
	If depth = 5 or depth = 6, then:
		Set the color of block to (102, 65, 33, 255).
	Leave the if statement.
	If depth >= 7, then:
		Set the color of block to (84, 45, 28, 255).
	Leave the if statement.

	# OnClick
	When block is clicked call ClickBlock.

	# Add the block to the list
	Add block to global blocksList.
	Add XPos * BlockSize to global blocksXList.
	Add YPos * BlockSize to global blocksYList.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                     Utils                                    #
# ---------------------------------------------------------------------------- #

# Returns a random number below the given max
Begin a function called Random with an int called max.

	# Simple, but effective random number generator
	Set seed to seed * 1103515245 + 12345.
	If seed < 0, then:
		Set seed to seed - seed * 2.
	Leave the if statement.
	Set global intRet to seed % max.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                   Gameloop                                   #
# ---------------------------------------------------------------------------- #

Begin a function called Gameloop. 

	Call the function AnimateSun.
	
	# Load terrain
	Call the function LoadTerrain.

	If Get the key F, then:
		Print "F" to the console.
	Leave the if statement.

	Call the function MoveCamera.
Leave the function.

Begin a function called MoveCamera.

	# Move the player
	If Get the key A, then:
		Set pX to pX - pSpeed.
	Leave the if statement.
	If Get the key D, then:
		Set pX to pX + pSpeed.
	Leave the if statement.

	# Move the camera towards the player
	Set camX to camX + (pX - camX) / camSpeed.

	# Set the new position of all blocks
	Set i to 0.
	While i < length of blocksList:
		Set block to index i of blocksList.
		Set newX to index i of blocksXList - camX + 1920/2.
		Set newY to index i of blocksYList.
		Move block to newX and newY.

		Set i to i + 1.
	Exit the while.

	# Move the player (with respect to the camera)
	Move player to pX - camX + 1920/2 and pY.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                Events (clicks)                               #
# ---------------------------------------------------------------------------- #

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

Begin a function called ClickBlock with a Box called tempBlock.

	# If the block is clicked, kill it
	Remove tempBlock from the canvas.
Leave the function.