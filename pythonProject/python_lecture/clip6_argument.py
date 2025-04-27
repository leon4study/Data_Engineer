def first_name(*names):
    for name in names:
        print(name[1:])

#first_name("John", "Doe", "Jane", "Smith")

def name_values(**kwargs):
    for key, value in kwargs.items():
        print("{} : {}".format(key, value))

#name_values(name="John", age=30, city="New York", country="USA")


# 변수 혹은 자료구조 안에 담을 수 있어야 한다
def add(a,b):
    return a + b

add2 = add
print(add2(1,3))

# 매개변수로 전달할 수 있어야 한다

def add_manager(func, a, b):
    print("함수의 이름 : {}".format(func.__name__))
    return func(a, b)

rsult = add_manager(add,2,5)
print(rsult)

# 리턴값으로 사용될 수 있어야 한다. (함수를 함수내에서 선언할 수 있어야 한다.)

def hello(name):
    def printer():
        print(f"Hello, {name}!")
    return printer

hello_func = hello("John")
hello_func()


# 클로저 (closure)
# 클로저란?
# 1. 내부 함수가 외부 함수의 변수를 참조하는 경우
# 2. 외부 함수가 종료된 후에도 내부 함수가 외부 함수의 변수를 기억하는 경우
# 3. 내부 함수가 외부 함수의 변수를 사용하여 동작하는 경우
# 클로저를 사용하면 외부 함수의 변수를 내부 함수에서 사용할 수 있다.
def goodbye(msg):
    message = "Goodbye, " + msg

    def say():
        print(message)
    
    return say

say_goodbye = goodbye("John")
say_goodbye()  # Goodbye, John
# 클로저를 사용하여 상태를 유지하는 예제

def calulate_4(num):
    sum_1 = 4 + num
    print(sum_1)

    def add_5():
        print(add(sum_1, 5))
    
    return add_5

add_5 = calulate_4(10) #14
add_5()  # 19

import time

def logging_duration(orig_func):
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = orig_func(*args, **kwargs)
        end_time = time.time()
        print(f"{orig_func.__name__} duration : {end_time - start_time} sec")
        return result
    return wrapper


@logging_duration
def hello_and_sleep():
    time.sleep(1)
    print('heelo')

@logging_duration
def hello_and_slee2():
    time.sleep(2)
    print('heelo2')

hello_and_sleep()
hello_and_slee2()