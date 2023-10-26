# Define a function to create a grid of boxes
Begin a function called CreateBoxGrid with parameters called numRows, numCols, boxWidth, boxHeight:
    # Calculate the horizontal and vertical spacing between boxes
    Set an int called spacingX to 10.
    Set an int called spacingY to 10.

    # Loop through rows and columns to create the grid
    Set an int called row to 0.
    While row < numRows is true:
        Set an int called col to 0.
        While col < numCols is true:
            # Calculate the position for the current box
            Set an int called x to col * (boxWidth + spacingX).
            Set an int called y to row * (boxHeight + spacingY).

            # Create a new Box and set its properties
            Create a Box called gridBox.
            Move gridBox to x and y.
            Set the size of Box gridBox to boxWidth and boxHeight.
            Set the color of gridBox to Red. # Or use hex?

            # Increment the column counter
            Set col to col + 1.
        Exit the while.
        
        # Increment the row counter
        Set row to row + 1.
    Exit the while.

Leave the function.

# Call the CreateBoxGrid like this
# This would work with command args by default (Maybe?)
# Call the function CreateBoxGrid with 5, 5, 50, and 50.  # Create a 5x5 grid of boxes with a size of 50x50 pixels.
