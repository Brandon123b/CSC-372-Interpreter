
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

	Call the function InitClouds.
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
	Set global pVelY to 0.0.
	Set global pSize to 50.0.
	Set global isGrounded to false.

	Create a global Box called player.
	Set the size of player to pSize and pSize.
	Set the color of player to (255, 0, 0, 255).
Leave the function.

# Create some moving clouds
Begin a function called InitClouds.

	# Initialize cloudsList
	Create a Circle called cloud.
	Add cloud to global cloudsList.
	Add 0.0 to global cloudsXList.
	Add 0.0 to global cloudsYList.
	Remove cloud from the canvas.
	Remove index 0 from cloudsList.
	Remove index 0 from cloudsXList.
	Remove index 0 from cloudsYList.

	# Animation time
	Set global globalCloudsTimer to 0.0.
	Set global nextCloud to 0.0.
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

Begin a function called AnimateClouds.

	Set globalCloudsTimer to globalCloudsTimer - 4.0.

	# Move the clouds to the left
	Set i to 0.
	While i < length of cloudsList:
		Set cloud to index i of cloudsList.
		Set newX to index i of cloudsXList.
		Set newY to index i of cloudsYList.

		Move cloud to newX + globalCloudsTimer and newY.

		# If the cloud is offscreen, then remove it
		If newX + globalCloudsTimer < 0.0-400.0, then:
			Remove cloud from the canvas.
			Remove index i from cloudsList.
			Remove index i from cloudsXList.
			Remove index i from cloudsYList.
			Set i to i - 1.
		Leave the if statement.

		Set i to i + 1.
	Exit the while.

	# Spawn new cloud if the last one is offscreen
	If nextCloud >= globalCloudsTimer, then:
		Call the function SpawnCloud.
		Call the function Random with 1000.
		Set nextCloud to globalCloudsTimer - intRet.
	Leave the if statement.
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

	# Random chance to spawn a tree
	Call the function Random with 9.
	If intRet = 1, then:
		Call the function SpawnTree with XCol and height - 1.
	Leave the if statement.

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

	# -1 is tree stump (brown-ish)
	If depth = 0-1, then:
		Set the color of block to (166, 139, 113, 255).
	Leave the if statement.

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

	# Add the block to the list (as doubles)
	Add block to global blocksList.
	Add XPos * BlockSize + 0.0 to global blocksXList.
	Add YPos * BlockSize + 0.0 to global blocksYList.
Leave the function.

# Spawns a tree at the given x and y position
Begin a function called SpawnTree with an int called XPos and an int called YPos.

	# Create the trunk
	Call the function Random with 5.
	Set intRet to intRet + 4.
	Set i to 0.
	While i < intRet:
		Set branchY to YPos - i.
		Call the function SpawnBlock with XPos, branchY, 0-1.
		Set i to i + 1.
	Exit the while.

	# Create the leaves
	Set top to YPos - intRet - 1.

	# Create single top block
	Call the function SpawnBlock with XPos, top, 0.
	Set top to top + 1.
	
	# Create the top layer
	Set leafX to XPos - 1.
	While leafX <= XPos + 1:
		Call the function SpawnBlock with leafX, top, 0.
		Set leafX to leafX + 1.
	Exit the while.

	Set top to top + 1.
	Set leafX to XPos - 2.
	While leafX <= XPos + 2:
		Call the function SpawnBlock with leafX, top, 0.
		Set leafX to leafX + 1.
	Exit the while.
Leave the function.

# Spawns a single cloud at the given x and y position
Begin a function called SpawnCloud.

	Set i to 0.
	While i < 7:

		# Randomize the position of the cloud
		Call the function Random with 400.
		Set cloudY to intRet + 0.0.
		Call the function Random with 500.
		Set cloudX to intRet + 1920 + 500 - globalCloudsTimer.
		
		# Randomize the size of the cloud
		Call the function Random with 80.
		Set cloudSize to intRet + 50.0.

		Create a Circle called cloud.
		Set the radius of cloud to cloudSize.
		Set the color of cloud to (255, 255, 255, 255).
		Move cloud to cloudX and cloudY.

		Add cloud to global cloudsList.
		Add cloudX to global cloudsXList.
		Add cloudY to global cloudsYList.

		Set i to i + 1.
	Exit the while.
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
	Call the function AnimateClouds.
	
	# Load terrain
	Call the function LoadTerrain.

	Call the function MovePlayer.
	Call the function MoveCamera.
Leave the function.

# Moves the player to a new position (Does not touch player position as its handled by the camera)
Begin a function called MovePlayer.

	# Move the player
	If Get the key A, then:
		Set pX to pX - pSpeed.
	Leave the if statement.
	If Get the key D, then:
		Set pX to pX + pSpeed.
	Leave the if statement.

	# Gravity
	Set pVelY to pVelY + 0.8.
	If pVelY > 20.0, then: Set pVelY to 10.0. Leave the if statement.
	Set pY to pY + pVelY.

	# Jumping
	If Get the key W and isGrounded, then:
		Set pVelY to 0-15.0.
		Set pY to pY - 1.0.
		Set isGrounded to false.
	Leave the if statement.

	# See if the player is falling (not grounded) by vel
	If pVelY > 1.0, then: Set isGrounded to false. Leave the if statement.

	# Handle collisions with blocks (Both player and block positions are top right)
	Set i to 0.
	While i < length of blocksList:
		Set block to index i of blocksList.
		Set blockX to index i of blocksXList.
		Set blockY to index i of blocksYList.

		# If the player is inside the block, then move it out
		If pX + pSize > blockX and pX < blockX + BlockSize and pY + pSize > blockY and pY < blockY + BlockSize, then:
			
			# Get the overlap
			Set overlapX to pX + pSize - blockX.
			Set overlapX2 to blockX + BlockSize - pX.
			If overlapX2 < overlapX, then: Set overlapX to overlapX2. Leave the if statement.
			
			Set overlapY to pY + pSize - blockY.
			Set overlapY2 to blockY + BlockSize - pY.
			If overlapY2 < overlapY, then: Set overlapY to overlapY2. Leave the if statement.

			# If overlapY is smaller than overlapX, then move the player vertically
			If overlapY <= overlapX, then:
				# If the player is above the block, then move it up
				If pY < blockY, then:
					Set pY to pY - overlapY.
					Set isGrounded to true.
					Set pVelY to 0.0.
				Leave the if statement.

				# If the player is below the block, then move it down
				If pY > blockY, then:
					Set pY to pY + overlapY.
				Leave the if statement.
			Leave the if statement.

			# If overlapX is smaller than overlapY, then move the player horizontally
			If overlapX < overlapY, then:
				# If the player is to the left of the block, then move it to the left
				If pX < blockX, then:
					Set pX to pX - overlapX.
				Leave the if statement.

				# If the player is to the right of the block, then move it to the right
				If pX > blockX, then:
					Set pX to pX + overlapX.
				Leave the if statement.
			Leave the if statement.

		Leave the if statement.

		Set i to i + 1.
	Exit the while.
Leave the function.

# Moves the camera (Moves all objects to correct position to draw them)
Begin a function called MoveCamera.

	# Move the camera towards the player
	Set camX to camX + (pX - camX) / camSpeed.
	Set camY to camY + (pY - camY) / camSpeed.

	# Set the new position of all blocks
	Set i to 0.
	While i < length of blocksList:
		Set block to index i of blocksList.
		Set newX to index i of blocksXList - camX + 1920/2.
		Set newY to index i of blocksYList - camY + 1080/2.
		Move block to newX and newY.

		Set i to i + 1.
	Exit the while.

	# Move the player (with respect to the camera)
	Move player to pX - camX + 1920/2 and pY - camY + 1080/2.
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

# When a block is clicked, change its color
Begin a function called ClickBlock with a Box called tempBlock.

	# Set the color of the block to black
	Set the color of tempBlock to (0, 0, 0, 255).
Leave the function.