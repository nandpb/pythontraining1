class BankAccount:

    min_balance = 1500;


    def withdraw(self, amount, balance):
        self.balance = balance
        return (balance-amount)

    def deposit(self, amount, balance):
        self.balance = balance
        return (balance+amount)

print("choose your choice \n1.withdraw \n2.deposti ")
choise = input("enter your choice :")

if choise == '1':
    amount = int(input("enter an amount to Withdraw : "))
    if amount>0:
        ba = BankAccount()
        if ba.min_balance>=1000:
            print(ba.withdraw(amount,ba.min_balance))
        else:
            print("you can\'t withdraw this amount your balance exceed to min balance ")
    else:
        print("the amount should grater than Zero")

if choise == '2':
    amount = int(input("enter an amount to Withdraw : "))
    if amount > 0:
        ba = BankAccount()
        print(ba.deposit(amount, ba.min_balance))
    else:
        print("the amount should grater than Zero")
