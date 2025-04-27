trait PaymentModule {
  def collectPayment(amount: Int):Boolean = {
    println(s"paid $amount won")
    true
  }
}
