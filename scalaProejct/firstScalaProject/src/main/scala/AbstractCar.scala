abstract class AbstractCar(name: String) {
  def engineStart(): Unit = println(s"Engine starts in $name")
  def engineStop(): Unit = println(s"Engine Stops in $name")
  def accelerate(): Unit
  def brake(): Unit = println(s"$name is braking")
}

