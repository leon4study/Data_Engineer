val anything : Int = 2

def checkValue(anything: Int): String = anything match {
  case 0 => "Matched 0"
  case 1 => "Matched 1"
  case _ => "No match!"
}

val case0: String = checkValue(0)
println(case0)

val case1: String = checkValue(1)
println(case1)

val case3: String = checkValue(3)
println(case3)

