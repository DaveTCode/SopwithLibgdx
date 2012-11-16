package net.tyler.messaging

abstract class Message(val ticks: Long) extends Ordered[Message] {
  def compare(that: Message) = this.ticks compare that.ticks
}