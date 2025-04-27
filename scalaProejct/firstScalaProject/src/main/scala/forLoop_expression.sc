val nums : List[Int] = List(1,2,3,4,5)

val doubleNumbers = for (num <- nums) num * 2
println(doubleNumbers)

val doubleNumbersWithYield = for (num <- nums) yield num * 2
println(doubleNumbersWithYield)

val someNumbers = for(num <- nums) yield {
  val numPlusOne = num + 1
  val numMinusOne = num -1
  numPlusOne * numMinusOne
}
println(someNumbers)