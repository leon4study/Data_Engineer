#public
class Person:
    def __init__(self, name, age) -> None:
        self.name = name
        self.age = age


# protected
# protected는 자식 클래스에서만 접근 가능하다.
# protected는 언더바(_)로 시작한다.
class Person2:
    def __init__(self, name, age) -> None:
        self._name = name
        self._age = age

# private
# private은 클래스 내부에서만 접근 가능하다.
# private은 언더바 두개(__)로 시작한다.

class Person3:
    def __init__(self, name, age) -> None:
        self.__name = name
        self.__age = age