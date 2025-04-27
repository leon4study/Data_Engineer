class Bus extends AbstractCar(name = "Bus") with PaymentModule {

  override def engineStart(): Unit = println("Engine starts in Bus")
  override def engineStop(): Unit = println("Engine Stops in Bus")

  override def collectPayment(amount: Int): Boolean = {
    println(s"[bus] amount : $amount was paid")
    true
  }

  override def accelerate(): Unit = println(s"Bus is accelerating")
}
