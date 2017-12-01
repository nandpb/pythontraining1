class Parent:
    def sum(self, num1, num2):
        num3 = num1 + num2
        print("Addition ", num3)

class Child(Parent):
    def mul(self, num1, num2):
        num3 = num1 * num2
        print("mul ", num3)

ch = Child()
ch.sum(55,55)
ch.mul(55,55)

