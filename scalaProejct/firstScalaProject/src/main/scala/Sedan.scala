class Sedan extends Car {

  override def engineStart(): Unit = println("Engine starts in Sedan")
  override def engineStop(): Unit = println("Engine stops in Sedan")

}
