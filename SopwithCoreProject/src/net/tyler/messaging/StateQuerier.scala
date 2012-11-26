package net.tyler.messaging

import scala.reflect.ClassTag

abstract class StateQuerier(val messagingComponent: MessagingComponent) {
  
  /**
   * Returns a list of all events which occurred before a given time t.
   */
  def eventsPreTickVal(t: Long) = messagingComponent.Buffer filter {_.ticks < t}
  
  /**
   * Returns a list of all events which occurred after a given time t.
   */
  def eventsPostTickVal(t: Long) = messagingComponent.Buffer filter {_.ticks > t}
  
  def eventsBetween(tMin: Long, tMax: Long) = 
    messagingComponent.Buffer filter {message => (message.ticks > tMin) && (message.ticks < tMax)}
  
  /**
   * Returns a list of all events which occurred before a given time t and that
   * match a particular message type.
   */
  def messageEvents[T <: Message](t: Long)(implicit m: Manifest[T]): Seq[T] = messageEvents[T](0L, t)
    
  /**
   * Returns a list of all events which occurred before a given time t and that
   * match a particular message type.
   */
  def messageEvents[T <: Message](tMin: Long, tMax: Long)(implicit m: ClassTag[T]): Seq[T] =
    eventsBetween(tMin, tMax).collect({
      case message if m.runtimeClass.isInstance(message) => message
    }).asInstanceOf[Seq[T]]
  
  /**
   * Return a list of all events which occurred between two tick values and 
   * match any of the given message types.
   */
  def multiMessageEvents(types: Seq[Class[_ <: Message]], t: Long): Seq[Message] =
    multiMessageEvents(types, 0L, t)
  
  def multiMessageEvents(types: Seq[Class[_ <: Message]], tMin: Long, tMax: Long) = {
    eventsBetween(tMin, tMax) collect {
      case message if types.contains(message.getClass) => message
    }
  }
}