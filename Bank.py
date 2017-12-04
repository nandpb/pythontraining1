class Account:
    cust_name=""
    acc_no=0
    acc_type=""

    def get_accinfo(self):
        cust_name = input("Enter Customer Name : ")
        try:
            acc_no = int(input("Enter Account Number : "))
        except:
            print("enter only Numbers String special char Can't accept here ")
        acc_type = input("enter account type : ")


    def display_info(self,cust_name,acc_no,acc_type):
        print("Customer Name : ", cust_name)
        print("Account number : ", acc_no)
        print("account type : ", acc_type)


class Cur_Acc (Account):
    balance = 0.0
    def display_currbal(self):
        print("Balance : ",self.balance)

    def deposit_currbal(self):
        deposit =  int(input("enter amount to deposit : "))
        self.balance += deposit

    def withdraw_currbal(self):
        print("Balance : ",self.balance)
        withdraw = int(input("Enter amount to be withdraw : "))
        self.balance -= withdraw
        if self.balance<500:
            penalty = (500-self.balance)/10
            self.balance -= penalty
            print("Balance after deducting penalty : ",self.balance)
        elif self.balance<500:
            print("\n you have to take permission for bank overdraft Facility : \n")
            self.balance += withdraw
        else:
            print("After withdraw your balance revels : ",self.balance)


class Sav_Acc (Account):
    savbal = 100.0
    def disp_savbal(self):
        print("balance : ",self.savbal)

    def deposit_savbal(self):
        deposit=int(input("Enter your amount to Deposit : "))
        self.savbal += deposit
        interest = (self.savbal*2)/100
        self.savbal += interest

    def withdraw_savbal(self):
        print("Balance : ",self.savbal)
        withdraw = int(input("Enter amount to be withdraw : "))
        self.savbal -= withdraw
        if(withdraw>self.savbal):
            print(" Your have to take permission for Bank Overdraft Facility \n")
            self.savbal += withdraw
        else:
            print("After withdraw your balance revels : ",self.savbal)


class Bank:
    c1 = Cur_Acc()
    s1 = Sav_Acc()
    char = input("Enter S for saving a/c customer C for current a/c customer : ")

    if (char=="S" or char=="C"):
        s1.get_accinfo()
        while (1):

            choice =input("1. Deposit \n2. Withdraw \n3.Display Balnace \n4.Display with full details \n5.Exit \n6.choose your choice\n")
            print("choose your choise :\n")
            if choice=='1':
                s1.deposit_savbal()
                break
            elif choice=='2':
                s1.withdraw_savbal()
                break
            elif choice=='3':
                s1.disp_savbal()
                break
            elif choice=='4':
                s1.display_info()
                break
            elif choice=='5':
                exit()
            else:
                print("Entered chois is invalid, \"try again\"")
    else:
        c1.get_accinfo()
        while (1):

            choice =input("1. Deposit \n2. Withdraw \n3.Display Balnace \n4.Display with full details \n5.Exit \n6.choose your choice\n")
            print("choose your choise :\n")
            if choice=='1':
                s1.deposit_savbal()
                break
            elif choice=='2':
                s1.withdraw_savbal()
                break
            elif choice=='3':
                s1.disp_savbal()
                break
            elif choice=='4':
                s1.display_info()
                break
            elif choice=='5':
                exit()
            else:
                print("Entered chois is invalid, \"try again\"")

