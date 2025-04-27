class Book(val title:String, val author: String ) {
  def this(title:String) = {
    this(title,"anonymous")
  }
}