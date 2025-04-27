class Person:
	def __init__(self, first_name, last_name, age):
		self.first_name = first_name
		self.last_name = last_name
		self.age = age
	
	def print_age(self):
		print(self.age)

	def full_name(self):
		return self.last_name + self.first_name

	def introduce(self):
		print(f"안녕하세요 {self.full_name()} 입니다.")
		

class Developer(Person): # 괄호 안에 클래스 자체를 넣어버리면 상속받아짐
	#Person 구현체가 그대로 물려받아서 왔다
	
	def introduce(self):
		super().introduce()
		print(f"저는 개발자입니다.")
		
	def develop(self):
		print("Wroking hard! I'm a keyboard warrior!")
# Person 클래스의 인스턴스를 생성

developer1 = Developer("철수","김", 27)
developer1.print_age()
developer1.introduce()
developer1.develop()
