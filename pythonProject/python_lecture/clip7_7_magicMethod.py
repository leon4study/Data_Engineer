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
        print(f"안녕하세요 {self.get_job()} {self.get_full_name()} 입니다.")
    
    #실제로 뭘 구현할지는 모르겠지만 추상메소드로 정의만 해놓고 자식클래스에서 알아서 구현하라는 뜻
    @abstractmethod
    def get_job(self):
        pass

    def __add__(self, other):
        return self.age + other.age
    
    def __ge__(self, other): # greater than or equal
        return self.age >= other.age
    
    def __del__(self):
        self.introduce()
        print("I'm dead")



class Developer(Person): # 괄호 안에 클래스 자체를 넣어버리면 상속받아짐
    #Person 구현체가 그대로 물려받아서 왔다
        
    def develop(self):
        print("Wroking hard! I'm a keyboard warrior!")

    def get_job(self):
        return "개발자"

# Person 클래스의 인스턴스를 생성
developer1 = Developer("철수","김", 27)
developer1.introduce()
developer1.print_age()
developer1.develop()


developer2 = Developer("영희","김", 14)
developer2.introduce()
developer2.print_age()

print(developer1 + developer2)
print(developer1 <= developer2)

del developer1

# 이렇게 하면 developer1이 죽을 때 introduce()가 호출된다.
# developer2는 아직 살아있고
# 프로그램이 종료되면 모든 객체가 죽으면서 __del__이 호출된다. 그래서 둘 다 호출되는 것으로 보이는 것!
# time.sleep(1) 으로 확인하면 될 듯
import time
time.sleep(1)

