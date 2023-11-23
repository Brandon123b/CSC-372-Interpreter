Begin a function called TestLists. 
	
	# Create an int list

	Add 1 to global list1.
	Add 2 to list1.
	Add 3 to list1.

	Remove 3 from list1.
	Remove index 0 from list1.

	# Create a string list
	
	Add "1" to list2.
	Add "2" to list2.
	Add "3" to list2.

	Remove "3" from list2.
	Remove index 0 from list2.

	# Create a float list

	Add 1.0 to list3.
	Add 2.0 to list3.
	Add 3.0 to list3.

	Remove 3.0 from list3.
	Remove index 0 from list3.

	# Print all lists

	Print "Lists:" to the console.
	Print list1 to the console.
	Print list2 to the console.
	Print list3 to the console.
	Print "" to the console.

	# Test gets

	Set t1 to index 0 of list1.
	Set t2 to index 0 of list2.
	Set t3 to index 0 of list3.

	# Print all gets

	Print "List Gets:" to the console.
	Print t1 to the console.
	Print t2 to the console.
	Print t3 to the console.
	Print "" to the console.

	# GUI Test

	Create a Box called box1.
	Create a Box called box2.
	Create a Box called box3.
	Add box1 to list4.
	Add box2 to list4.
	Add box3 to list4.

	# Remove box3 from list4.
	Remove index 0 from list4.

	# Print a GUI object
	Set out to index 0 of list4.
	Print "GUI Box:" to the console.
	Print out to the console.
	Print "" to the console.

	# Test expressions with list gets

	Set t1 to index 0 of list1 + index 0 of list1.
	Set t2 to index 0 of list2 + index 0 of list2.
	Set t3 to index 0 of list3 + index 0 of list3.

	# Print all expressions

	Print "Expressions:" to the console.
	Print t1 to the console.
	Print t2 to the console.
	Print t3 to the console.

	# Test list length
	Add 1 to list5.
	Print "Len: " + length of list5 + " (Exp: 1)" to the console.
	Add 2 to list5.
	Print "Len: " + length of list5 + " (Exp: 2)" to the console.
	Add 3 to list5.
	Print "Len: " + length of list5 + " (Exp: 3)" to the console.
	Remove 3 from list5.
	Print "Len: " + length of list5 + " (Exp: 2)" to the console.
	Remove index 0 from list5.
	Print "Len: " + length of list5 + " (Exp: 1)" to the console.
	Remove index 0 from list5.
	Print "Len: " + length of list5 + " (Exp: 0)" to the console.

Leave the function.