package net.tyler.messaging

import scala.collection.mutable.ArrayBuffer

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
  def messageEvents[T <: Message](t: Long) = {
    eventsPreTickVal(t) collect { case message: T => message }
  }
}