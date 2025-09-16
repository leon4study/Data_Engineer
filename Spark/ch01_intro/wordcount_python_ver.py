from collections import defaultdict
import os, sys

if __name__ == "__main__":

    script_path = os.path.abspath(sys.argv[0])
    script_dir = os.path.dirname(script_path)
    os.chdir(script_dir)

    words_count: dict[str, int] = defaultdict(int)
    # defaultdict 사용하면 초기값이 항상 0

    with open("data/words.txt", "r") as file:

        for _, line in enumerate(file):
            words_each_line = line.strip().split(" ")

            for _, word in enumerate(words_each_line):
                words_count[word] += 1

    for word, count in words_count.items():
        print(f"{word} : {count}")


# 굳이 스파크 쓸 필요 없이 파이썬 쓰면 되지만 텍스트 파일이 몇만개가 되고 텍스트 하나가 엄청 커지는 경우엔
# 하나의 스레드로 동작하는 것이기 때문에 한계가 있음 -> spark 사용해야함
