try:
    mobile = int(input("enter a integer : "))
except ValueError:
    print("Enter only Integer number")

val2 = float(input("enter a float : "))
print(type(val2))
try:
    print(type(mobile))
    print(val2, mobile)
except NameError:
    print("something wrong")