object Oop2 {
  def main(args: Array[String]): Unit = {
    val sedan: Car = new Sedan()
    val suv : Car = new SUV()
    val bus : AbstractCar = new Bus()

    val cars = List(sedan,suv)
    cars.foreach(it => {
      it.engineStart()
      it.engineStop()
    })

    bus.engineStart()
    bus.accelerate()
    bus.brake()
    bus.asInstanceOf[PaymentModule].collectPayment(1600)
    bus.engineStop()
  }
}
