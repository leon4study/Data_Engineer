try{
  1/0
}catch {
  case e:ArithmeticException => println( "Are you idiot?")
  case _: Throwable => println("Unknown Exception")
}finally {
  println("print anyway")
}

def upperString(value: String): Option[String]={
  if (value.isEmpty) None
  else Some(value.toUpperCase())
}

val result1 = upperString("lowercase")
val result2 = upperString("")

if (result1.isDefined) {
  println(result1.get )
}

if(result2.isEmpty){
  println("empty")
}

def upperStringOption(value: String): Either[String, String] = { //보통 에러를 left에 넣음
  if (value.isEmpty) Left("Value cannot be empty")
  else Right(value.toUpperCase())
}

val result3 = upperStringOption("lowercase")
val result4 = upperStringOption("")

if (result3.isLeft){
  println(result3.left)
  println(result3.left.get)
}else{
  println(result3.right)
  println(result3.right.get)
}