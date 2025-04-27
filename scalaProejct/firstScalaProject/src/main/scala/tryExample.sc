import scala.io.StdIn
import scala.util.{Failure, Success, Try}

//여기서 실행 안되니까 터미널에서 복붙해서 실행

def divide: Try[Int] = {
  val dividend = Try(StdIn.readLine("Enter an Int that you'd like to divide: \n" ).toInt)
  val divisor = Try(StdIn.readLine("Enter an Int that you'd like to divide by: \n" ).toInt)
  val problem = dividend.flatMap(x => divisor.map(y => x/y))
  problem match {
    case Success(value) =>
      println("Result of "+ dividend.get + "/" + divisor.get+ " is : "+ value)
      Success(value)
    case Failure(e) =>
      println("You must've divided by zero or entered something that's not an Int. Try again!")
      println("Info from the exception : "+ e.getMessage)
      divide
  }
}

// 함수 지정 후 터미널에서 이것도 실행해보기!
println(divide.map(r=>{r*10}))

