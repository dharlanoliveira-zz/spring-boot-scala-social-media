package dharlanoliveira.mysocialmedia.application.dto

object Order extends Enumeration{
  type OrderType = Value
  val ASC: Value = Value("ASC")
  val DESC: Value = Value("DESC")

  def isOrderType(s: String): Boolean = values.exists(_.toString == s)
}
