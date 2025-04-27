class SUV extends Car {

  override def engineStart(): Unit = println("Engine starts in SUV")
  override def engineStop(): Unit = println("Engine stops in SUV")

}
