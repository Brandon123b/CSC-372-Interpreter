
# ---------------------------------------------------------------------------- #
#                                  Start Menu                                  #
# ---------------------------------------------------------------------------- #

Begin a function called Main with an int called _seed.

	Print _seed to the console.
	Set global seed to _seed.
	Set global intRet to 0.

	Set global attackCooldown to 0.
	Set global bulletAttackCount to 0.
	Set global bulletAttackCooldown to 0.

	# Create a start menu
	Call the function LoadMenu.
Leave the function.

# Creates a start menu for the game.
Begin a function called LoadMenu.

	Call the function KillAll.

	# Create a background (static color)
	Create a global Box called backGround.
	Move backGround to 0 and 0.
	Set the size of backGround to 1920 and 1080.
	Set the color of backGround to (10, 10, 25, 255).

	Call the function InitHPBar.

	# Create a title
	Create a global Text called titleText.
	Move titleText to 500 and 340.
	Set the size of titleText to 100.
	Set the color of titleText to (255, 255, 255, 255).
	Set the text of titleText to "A Simple Bullet Hell".

	# Create a start button
	Create a global Box called startButtonBox.
	Move startButtonBox to 600 and 500.
	Set the size of startButtonBox to 500 and 100.
	Set the color of startButtonBox to (255, 255, 255, 255).
	When startButtonBox is clicked call OnStart.

	# Create a start button text
	Create a global Text called startButtonText.
	Move startButtonText to 600 and 580.
	Set the size of startButtonText to 100.
	Set the color of startButtonText to (0, 0, 0, 255).
	Set the text of startButtonText to "Start".

	# Create some text to explain the game
	Create a global Text called gameText.
	Move gameText to 10 and 1060.
	Set the size of gameText to 30.
	Set the color of gameText to (255, 255, 255, 255).
	Set the text of gameText to "Use WASD for movement and jumping, Avoid the bullets! There is no bullet collision in midair!".
Leave the function.

# Removes the start menu and starts the game.
Begin a function called OnStart.

	# Delete the title
	Remove titleText from the canvas.
	Remove startButtonBox from the canvas.
	Remove startButtonText from the canvas.

	Call the function StartGame.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                  Death Menu                                  #
# ---------------------------------------------------------------------------- #

# Creates a death menu for the game.
Begin a function called LoadDeathMenu.

	Set global isAlive to false.

	# Create a title
	Create a global Text called titleText.
	Move titleText to 500 and 340.
	Set the size of titleText to 100.
	Set the color of titleText to (255, 0, 0, 255).
	Set the text of titleText to "You Died".

	# Create a start button
	Create a global Box called startButtonBox.
	Move startButtonBox to 600 and 500.
	Set the size of startButtonBox to 500 and 100.
	Set the color of startButtonBox to (255, 255, 255, 255).
	When startButtonBox is clicked call LoadMenu.

	# Create a start button text
	Create a global Text called startButtonText.
	Move startButtonText to 600 and 580.
	Set the size of startButtonText to 100.
	Set the color of startButtonText to (0, 0, 0, 255).
	Set the text of startButtonText to "Main Menu".
Leave the function.

# ---------------------------------------------------------------------------- #
#                                   Main Game                                  #
# ---------------------------------------------------------------------------- #

# Creates the main game.
Begin a function called StartGame.

	Set global isAlive to true.

	Call the function SpawnPlatforms.

	Call the function InitPlayer.
Leave the function.

# Spawn the platforms
Begin a function called SpawnPlatforms.

	# Create a long base platform
	Create a global Box called basePlatform.
	Set xSize to 1920*2/3.0.
	Set ySize to 150.0.
	Set xPos to 1920/5.0.
	Set yPos to 1080*4/5.0.

	Set the size of basePlatform to xSize and ySize.
	Move basePlatform to xPos and yPos.
	Set the color of basePlatform to (255, 255, 255, 255).

	# Add all details to lists
	Add basePlatform to global platformsList.
	Add xSize to global platformXSizes.
	Add ySize to global platformYSizes.
	Add xPos to global platformXPos.
	Add yPos to global platformYPos.
Leave the function.

# Create a player
Begin a function called InitPlayer.

	# Create player vars
	Set global pX to 1920.0/2.0.
	Set global pY to 300.0.
	Set global pSpeed to 15.0.
	Set global pVelY to 0.0.
	Set global pSize to 50.0.
	Set global isGrounded to false.
	Set global hp to 100.

	Create a global Box called player.
	Set the size of player to pSize and pSize.
	Set the color of player to (0, 255, 0, 255).
	Move player to pX and pY.
Leave the function.

# Init Hp Bar
Begin a function called InitHPBar.

	# Create a green Bar at the top left
	Create a global Box called hpBar.
	Set the size of hpBar to 300 and 50.
	Set the color of hpBar to (0, 255, 0, 255).
	Move hpBar to 30 and 30.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                    Attacks                                   #
# ---------------------------------------------------------------------------- #

# Manages when to Attacks
Begin a function called AttackManager.

	Set global attackCooldown to attackCooldown - 1.		# Cooldown for new attacks

	# If there are more bullets waiting to be fired, then fire them
	If bulletAttackCount > 0, then:
		Set bulletAttackCooldown to bulletAttackCooldown - 1.

		# Don't fire too fast
		If bulletAttackCooldown <= 0, then:
			Call the function CreateBulletToPlayer.
			Set bulletAttackCount to bulletAttackCount - 1.

			Set bulletAttackCooldown to 4.
		Leave the if statement.
	Leave the if statement.

	# Attack if the cooldown is over
	If attackCooldown <= 0, then:

		# Pick a random attack
		Call the function Random with 2.
		Set attackType to intRet.

		# Create a vertical line of bullets
		If attackType = 0, then:
			Call the function CreateVerticalLine.
			Set attackCooldown to 30.
		Leave the if statement.

		# Load up bullets to be fired from the top of the screen
		If attackType = 1, then:
			Call the function Random with 12.
			Set bulletAttackCount to bulletAttackCount + intRet.
			Set attackCooldown to 15.
		Leave the if statement.

	Leave the if statement.
Leave the function.

# Creates a vertical line of bullets from the left or right (randomly)
Begin a function called CreateVerticalLine.

	# Choose a random side
	Call the function Random with 2.
	Set side to intRet.

	# Side 1 is left, side 1 is right
	If side = 0, then:
		Set side to 0-1.
	Leave the if statement.

	Set bulletCount to 20.

	# Create a line of bullets
	Set i to 0.
	While i < bulletCount:
		Set posX to 1920/2 + side * 1920*2/3.
		Set posY to 1080/bulletCount * i.
		Set velX to (0-side) * 10.
		Call the function CreateBullet with posX, posY, velX, 0.
		Set i to i + 1.
	Exit the while.
Leave the function.

# Spawn a bullet from the top of the screen to the player
Begin a function called CreateBulletToPlayer.

	# Get the bullet start position
	Call the function Random with 1920.
	Set bulletX to intRet.
	Set bulletY to 0-10.

	# Get the bullet velocity
	Set bulletVelX to (pX - bulletX) / 100.
	Set bulletVelY to (pY - bulletY) / 100.

	# Create the bullet
	Call the function CreateBullet with bulletX, bulletY, bulletVelX, bulletVelY.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                    Bullets                                   #
# ---------------------------------------------------------------------------- #

# Creates a bullet
Begin a function called CreateBullet with an double called posX, an double called posY, an double called velX, an double called velY.

	# Statics
	Set global bulletRadius to 10.0.

	# Create a bullet
	Create a Circle called bullet.
	Set the radius of bullet to bulletRadius.
	Set the color of bullet to (255, 0, 0, 255).
	Move bullet to posX and posY.

	# Add all details to lists
	Add bullet to global bulletsList.
	Add posX to global bulletXPosList.
	Add posY to global bulletYPosList.
	Add velX to global bulletXVelList.
	Add velY to global bulletYVelList.
Leave the function.

# Kills all bullets and platforms
Begin a function called KillAll.

	# Delete all bullets
	Set i to 0.
	While i < length of bulletsList:
		Set bullet to index i of bulletsList.
		Remove index i from bulletsList.
		Remove index i from bulletXPosList.
		Remove index i from bulletYPosList.
		Remove index i from bulletXVelList.
		Remove index i from bulletYVelList.
		Remove bullet from the canvas.
	Exit the while.

	# Delete all platforms
	Set i to 0.
	While i < length of platformsList:
		Set platform to index i of platformsList.
		Remove index i from platformsList.
		Remove index i from platformXPos.
		Remove index i from platformYPos.
		Remove index i from platformXSizes.
		Remove index i from platformYSizes.
		Remove platform from the canvas.
	Exit the while.
Leave the function.

# ---------------------------------------------------------------------------- #
#                                   Game Loop                                  #
# ---------------------------------------------------------------------------- #

Begin a function called Gameloop. 

	If isAlive, then:

		Call the function MovePlayer.

		Call the function MoveBullets.
		Call the function HandleBulletCollisions.

		Call the function AttackManager.
	Leave the if statement.
Leave the function.

# Update the HP bar
Begin a function called UpdateHPBar.

	# Find size based on hp
	Set hpSize to hp * 3.

	Set the size of hpBar to hpSize and 50.
Leave the function.

# Moves the player to a new position
Begin a function called MovePlayer.

	Set gravity to 2.0.
	Set terminalVelocity to 40.0.

	# Move the player
	If Get the key A, then:
		Set pX to pX - pSpeed.
	Leave the if statement.
	If Get the key D, then:
		Set pX to pX + pSpeed.
	Leave the if statement.

	# Gravity
	Set pVelY to pVelY + gravity.
	If pVelY > terminalVelocity, then: Set pVelY to terminalVelocity. Leave the if statement.

	# Jumping
	If Get the key W and isGrounded, then:
		Set pVelY to 0-30.0.
		Set pY to pY - 1.0.
		Set isGrounded to false.
	Leave the if statement.

	# See if the player is falling (not grounded) by vel
	If pVelY > gravity * 2, then: Set isGrounded to false. Leave the if statement.

	# Apply velocity and move
	Set pY to pY + pVelY.
	Move player to pX and pY.

	# Handle collisions with blocks (Both player and block positions are top right)
	Set i to 0.
	While i < length of platformsList:
		Set block to index i of platformsList.
		Set blockX to index i of platformXPos.
		Set blockY to index i of platformYPos.
		Set BlockXSize to index i of platformXSizes.
		Set BlockYSize to index i of platformYSizes.

		# If the player is inside the block, then move it out
		If pX + pSize > blockX and pX < blockX + BlockXSize and pY + pSize > blockY and pY < blockY + BlockYSize, then:
			
			# Get the overlap
			Set overlapX to pX + pSize - blockX.
			Set overlapX2 to blockX + BlockXSize - pX.
			If overlapX2 < overlapX, then: Set overlapX to overlapX2. Leave the if statement.
			
			Set overlapY to pY + pSize - blockY.
			Set overlapY2 to blockY + BlockYSize - pY.
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

# Moves all bullets
Begin a function called MoveBullets.

	# Move all bullets
	Set i to 0.
	While i < length of bulletsList:
		Set bullet to index 0 of bulletsList.
		Set bulletX to index 0 of bulletXPosList.
		Set bulletY to index 0 of bulletYPosList.
		Set bulletVelX to index 0 of bulletXVelList.
		Set bulletVelY to index 0 of bulletYVelList.

		# Remove data from lists, then readd it
		Remove index 0 from bulletsList.
		Remove index 0 from bulletXPosList.
		Remove index 0 from bulletYPosList.
		Remove index 0 from bulletXVelList.
		Remove index 0 from bulletYVelList.

		# Modify the data
		Set bulletX to bulletX + bulletVelX.
		Set bulletY to bulletY + bulletVelY.

		# Add the new data
		Add bullet to bulletsList.
		Add bulletX to bulletXPosList.
		Add bulletY to bulletYPosList.
		Add bulletVelX to bulletXVelList.
		Add bulletVelY to bulletYVelList.

		Move bullet to bulletX and bulletY.

		Set i to i + 1.
	Exit the while.
Leave the function.

# Handle Bullet collisions (I treat them like boxes for easy math)
Begin a function called HandleBulletCollisions.

	# Handle collisions with blocks (Both player and block positions are top right)
	Set i to 0.
	While i < length of bulletsList:
		Set block to index i of bulletsList.
		Set blockX to index i of bulletXPosList - bulletRadius.
		Set blockY to index i of bulletYPosList - bulletRadius.
		Set BlockXSize to bulletRadius * 2.
		Set BlockYSize to bulletRadius * 2.

		# Mess with seed for more random numbers
		Set seed to seed + 37.

		# If the player is inside the block, then move it out
		If isGrounded and pX + pSize > blockX and pX < blockX + BlockXSize and pY + pSize > blockY and pY < blockY + BlockYSize, then:
			
			Set hp to hp - 20.
			Call the function UpdateHPBar.
			Print "Hit" to the console.

			If hp <= 0, then:
				Call the function LoadDeathMenu.
				Print "Dead" to the console.
			Leave the if statement.

			# Destroy the bullet
			Remove index i from bulletsList.
			Remove index i from bulletXPosList.
			Remove index i from bulletYPosList.
			Remove index i from bulletXVelList.
			Remove index i from bulletYVelList.
			Remove block from the canvas.
		Leave the if statement.

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
	While seed < 0: Set seed to seed + 2147483647. Exit the while.
	Set global intRet to seed % max.
Leave the function.
