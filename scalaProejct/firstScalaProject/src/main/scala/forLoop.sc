val nums: List[Int] = List(1,2,3,4,5)

for (num <- nums)println(num)

nums.foreach(it => print(it +", " ))

val numberMap = Map(
  "one" -> 1,
  "two" -> 2,
  "three" -> 3
)

for ((key, value) <- numberMap) println(s"Number $key : $value x 10 = ${value * 10}")