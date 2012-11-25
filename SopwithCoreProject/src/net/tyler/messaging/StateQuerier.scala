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
  
  def eventsBetween(tMin: Long, tMax: Long) = 
    messagingComponent.Buffer filter {message => (message.ticks > tMin) && (message.ticks < tMax)}
  
  /**
   * Returns a list of all events which occurred before a given time t and that
   * match a particular message type.
   */
  def messageEvents[T <: Message](t: Long)(implicit m: Manifest[T]): Seq[T] = messageEvents[T](0, t)
    
  /**
   * Returns a list of all events which occurred before a given time t and that
   * match a particular message type.
   */
  def messageEvents[T <: Message](tMin: Long, tMax: Long)(implicit m: Manifest[T]): Seq[T] =
    eventsBetween(tMin, tMax).collect({
      case message if (ClassManifest.singleType(message) <:< m) => message
    }).asInstanceOf[Seq[T]]
}