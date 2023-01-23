'''
Palindrome-GriffinBean.py
'''

# Runs this program until cancelled or killed, based on the sample output in
# the instructions
while True:
    reverse = ""
    print("Enter a string: ", end="", flush=True)
    userString = input()
    # Decriments through the input String, adding each character to the
    # new reversed String
    x = len(userString) - 1
    while x >= 0:
        reverse += userString[x]
        x = x - 1
    # Checks if the string is exactly a palindrome, not counting spaces or
    # capitalization
    print("Your String: " + userString)
    print("The Reverse: " + reverse)
    if (reverse == userString):
        print("\"" + userString + "\" is a palindrome\n")
    else:
        print("\"" + userString + "\" is not a palindrome\n")
