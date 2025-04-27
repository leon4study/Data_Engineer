import traceback

def divide( a,b):
    try:
        result = a/b
        raise Exception("myexception")
    except ZeroDivisionError as e:
        result = 0
        print(f"cannot divide by 0 // a : {a}, b : {b}")
        traceback.print_exc()
    except Exception as ex:
        print(f"unknown Excpeion {ex}")
        traceback.print_exc()
    finally:
        return result



a = int(input("정수를 입력하세요: "))
b = a-1

print(divide(a,b))



### finally 사용방법
def open_file():
    f = open("foo.txt", "r")
    try:
        f.readLines()
    finally:
        f.close()