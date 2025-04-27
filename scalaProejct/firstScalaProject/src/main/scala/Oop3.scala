object Oop3 {
  def square(num:Int) : Int = num*num

  def double(i: Int) : Int = i * 2

  def main(args: Array[String]): Unit = {
    val nums = List(1,2,3,4,5)
    val doubles = nums.map(double)
    println(doubles)

    case class Car2(brand:String, model:String, firstManufacturedYear : Int)
    val porche911 = Car2("Porsche", "911", 1963)// case 클래스는 "new"키워드가피룡하지 않다
    println(porche911.brand)
    println(porche911.model)
    println(porche911.firstManufacturedYear)

    val porche992 = porche911.copy(model = "992",firstManufacturedYear = 2019)
    println(porche992.brand)
    println(porche992.model)
    println(porche992.firstManufacturedYear)

    val avante = Car2("Hyundai","Avante", 1990)
    println(porche911 == avante)
  }
}
