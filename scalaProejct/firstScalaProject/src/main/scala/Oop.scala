object Oop {
  def main(args: Array[String]): Unit = {
    val myBook1 = new Book("My awesome book 1", "Paul")
    val myBook2 = new Book("My awesome book 2")

    println(myBook1.author)
    println(myBook2.title)
    //myBook1.author = "Nancy" error

    println(myBook2.author)
  }
}
