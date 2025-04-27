val cond : Boolean = true
if (cond){
  "Condition is True"
}

val a =1
val b =2
val smallerN = if(a<b) a else b

println(smallerN)

if(a < b) println("a is smaller")

if(a==b){
  println("a equals b")
  if (a >0){
    println("a is larger than 0")
  }
}else if (a > b){
  println("a doesn't equal b")
  println("a is larger than b")
  if (a >0){
    println("a is larger than 0")
  }
}else{
  println("a is smaller than b")
}

