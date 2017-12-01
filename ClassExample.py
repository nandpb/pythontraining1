class Std:
    pass


s = Std()
print(s)


class Std:
    name = "Nanda"


stds = Std()
print(stds.name)


class Student:
    name = "Nanda"

    def change_name(self, new_name):
        self.name = new_name


obj1 = Student()
print(obj1.name)

obj1.change_name("Nandakumar")
print(obj1.name)


class Student:
    def __init__(self, name):
        self.name = name

    def change_name(self, new_name):
        self.name = new_name


obj2 = Student("Rama")
obj3 = Student("Mani")
obj4 = Student("NandakUmar")
print(obj2.name)
print(obj3.name)


class Employee:
    'Common base class for all employees'
    empCount = 0

    def __init__(self, name, salary):
        self.name = name
        self.salary = salary
        Employee.empCount += 1

    def displayCount(self):
        print
        print("Total Employee %d" % Employee.empCount)

    def displayEmployee(self):
        print
        print("Name : ", self.name, ", Salary: ", self.salary)


emp1 = Employee("Nandakumar", 45000)
#emp2 = Employee("Subha", 35000)
emp1.displayCount()
#emp2.displayCount()


class Student:
    num456 = 40

    def dis(self, name, num456):
        print()
        print(name)
        print(num456)
        print(" ")
        print(self.num456)

st1 = Student()
st1.dis("home", 60)



