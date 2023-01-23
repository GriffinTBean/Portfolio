'''
Greater-than-Average-GriffinBean.py
'''

# Loops through validating input until a list of < 100 values
# and the proper number of values is input
while True:     # Runs infinitely until the program is interrupted or killed
    while True:
        tooLong = False
        notEnough = False
        listlen = input("Enter number of integers followed " + 
                        "by the integer values:\n")
        list = []
        for string in listlen.split():
            # Adds and parses each integer input to the list
            list.append(int(string))
        if list[0] >= 100 or list[0] == 0:
            print("List must be greater than 0 and less than 100\n")
            tooLong = True
        if len(list) < list[0]+1:
            print("You did not input the number of " + 
                  "values you specified earlier\n")
            notEnough = True
        if list[0] < 100 and tooLong != True and notEnough != True:
            break
    # Takes first element of the list (the length), sets listlen to this value,
    # and then deletes it from the list
    listlen = list[0]
    avg = 0
    higherCount = 0
    del list[0]
    # Output and Average calculation follows
    print("The list (of " + str(listlen) + " element(s)): ")
    for x in range (listlen):
        avg = avg + list[x]
        # Referenced Python documentation for
        #these parameters to print to one line
        print(str(list[x]) + " ", end="", flush=True)
    avg = avg/listlen
    print("\nThe average:")
    # Referenced Python documentation for decimal format/rounding function
    print(round(avg, 2))
    # Processes through the list one last time for the number of integers
    # higher than the previously calculated average.
    for x in range (listlen):
        if (list[x] > avg):
            higherCount = higherCount + 1
    print("Number of values greater than average:")
    print(higherCount)
    print("\n")




