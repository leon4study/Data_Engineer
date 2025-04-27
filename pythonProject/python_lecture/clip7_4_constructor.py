# 어떤 조건에 부합하지 않으면 이 객체는 생성되면 안 돼. 이렇계 예외를 던지거나
# 로직을 넣기도 하는데, 가능하면 가벼운게 좋긴 하다.
# __init__() 메서드에서 조건을 검사하고, 조건에 부합하지 않으면 예외를 발생시킨다.

class Person:

    def __init__(self, name, age) :
        self.name = name
        self.age = age


    def __str__(self):
        return f"이름: {self.name}, 나이: {self.age}"
    def __repr__(self):
        return f"Person(name={self.name}, age={self.age})"
    
    def print_info(self):
        print(f"이름: {self.name}, 나이: {self.age}")

    @staticmethod
    def print_age_by_arg(age):
        print(f"나이: {age}")


paul = Person("Paul", 30)
paul.print_info()
paul.print_age_by_arg(14)
Person.print_age_by_arg(20)
