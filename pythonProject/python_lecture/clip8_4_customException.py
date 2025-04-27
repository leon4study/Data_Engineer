# 이미 있던 exception 클래스 말고 내가 exception 클래스를 정의하는 것.
# 파이썬 내장 클래스인 Exception을 상속받아서 나만의 예외 클래스를 만들 수 있다.
# 기본적으로 exception이 가지는 모든 속성을 가지고 내가 추가적인 정의를 하는 클래스를 만들 수 있다.

class AgeError(Exception):
    def __str__(self):
        return "나이는 0보다 커야 합니다."

class Person:
    def __init__(self, age):
        if age <=0:
            raise AgeError
        self.age = AgeError

Person(1) #잘 수행됨
Person(0) #AgeError
