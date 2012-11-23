package net.tyler.messaging

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassManifest

abstract class StateQuerier(val messagingComponent: MessagingComponent) {
  
  /**
   * Returns a list of all events which occurred before a given time t.
   */
  def eventsPreTickVal(t: Long) = messagingComponent.Buffer filter {_.ticks < t}
  
  /**
   * Returns a list of all events which occurred after a given time t.
   */
  def eventsPostTickVal(t: Long) = messagingComponent.Buffer filter {_.ticks > t}
  
  /**
   * Returns a list of all events which occurred before a given time t and that
   * match a particular message type.
   */
  def messageEvents[T <: Message](t: Long)(implicit m: Manifest[T]) =
    eventsPreTickVal(t).collect({
      case message if (ClassManifest.singleType(message) <:< m) => message
    }).asInstanceOf[Seq[T]]
  
  /**
   * Return a list of all events which occurred before a given time t and match
   * any of the given message types.
   */
  def multiMessageEvents(types: Seq[Class[_ <: Message]], t: Long) = {
    eventsPreTickVal(t) collect {
      case message if types.contains(message.getClass) => message
    }
  }
}