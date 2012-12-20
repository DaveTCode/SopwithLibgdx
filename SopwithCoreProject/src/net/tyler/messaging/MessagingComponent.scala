package net.tyler.messaging

import scala.collection.mutable.ArrayBuffer

class MessagingComponent(messageTypes: List[Class[_ <: Message]]) {
  
  private val messagePasser = new MessagePassing
  
  /*
   * Register with all of the valid message types for this component.
   */
  messageTypes.foreach(messagePasser.register(this, _))
  
  /**
   * A collection of all messages stored in the order that they arrived (not
   * necessarily time order).
   */
  val Buffer = new ArrayBuffer[Message]()
  
  /**
   * Receives any message for this component. Default behaviour is to store 
   * them in the buffer but that can be overriden if required.
   */
  def receive(message: Message) { Buffer += message }
  
  /**
   * Sends a message to all registered recipients of the message type.
   */
  def send(message: Message) { messagePasser.send(message) }
}