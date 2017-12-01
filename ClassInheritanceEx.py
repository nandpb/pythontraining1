class CompanyMember:
    def __init__(self, name, designation, age):
        self.name = name
        self.designation = designation
        self.age = age

    def tell(self):
        print('Name: ', self.name, '\nDesignation : ',self.designation, '\nAge : ', self.age)


class FactoryMember(CompanyMember):
    def __init__(self, name, designation, age, overtime_allow):
        CompanyMember.__init__(self, name, designation, age)
        self.overtime_allow = overtime_allow
        CompanyMember.tell(self)
        print("overtime Allowane : ", self.overtime_allow)


class OfficeMember(CompanyMember):
    def __init__(self, name, designation, age, travelling_allow):
        CompanyMember.__init__(self, name, designation, age)
        self.travelling_allow = travelling_allow
        CompanyMember.tell(self)
        print("overtime Allowane : ", self.travelling_allow)

FactoryMember('Nanda', 'Tech', 32,500)
OfficeMember('Kumar', 'Engg', 32,500)


