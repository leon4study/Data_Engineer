from abc import *

class Person(metaclass=ABCMeta): # ABC Meta를 항상 받아야 한다.
    def __init__(self, first_name, last_name, age):
        self.first_name = first_name
        self.last_name = last_name
        self.age = age
    
    def print_age(self):
        print(self.age)

    def get_full_name(self):
        return self.last_name + self.first_name

    def introduce(self): 
        print(f"안녕하세요 {self.get_job()}{self.get_full_name()} 입니다.")
    
    #실제로 뭘 구현할지는 모르겠지만 추상메소드로 정의만 해놓고 자식클래스에서 알아서 구현하라는 뜻
    @abstractmethod
    def get_job(self):
        pass



class Developer(Person): # 괄호 안에 클래스 자체를 넣어버리면 상속받아짐
    #Person 구현체가 그대로 물려받아서 왔다
        
    def develop(self):
        print("Wroking hard! I'm a keyboard warrior!")

    def get_job(self):
        return "개발자"

# Person 클래스의 인스턴스를 생성
developer1 = Developer("철수","김", 27)
developer1.print_age()
developer1.introduce()
developer1.develop()

p1 = Person("휴먼", "김", 27) # TypeError: Can't instantiate abstract class Person with abstract method get_job
# p1.introduce() # TypeError: Can't instantiate abstract class Person with abstract method get_job
p1.introduce()
# 추상메소드 get_job()을 구현하지 않았기 때문에 Person 클래스의 인스턴스를 생성할 수 없다.
# 이제 메타클래스를 만들고 abstractmethod가 있다면 Person 클래스로는 더이상 객체를 만들 수 없고
# 상속받아서 get_job()을 구현한 자식 클래스만 인스턴스를 만들 수 있다.