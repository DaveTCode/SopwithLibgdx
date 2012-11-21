package net.tyler.messaging

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

import com.badlogic.gdx.Gdx

import net.tyler.sopwith.Configuration

class MessagePassing {
  private val registeredComponents = new HashMap[Class[_ <: Message], 
                                                 ArrayBuffer[MessagingComponent]]
  
  /**
   * Register a component to receive messages about a given message type.
   *
   * @param component
   * @param messageType
   */
  def register(component : MessagingComponent, messageType : Class[_ <: Message]) {
    Gdx.app.log(Configuration.LOG, "Registering component " + component.toString + " for message type ")
    val componentList = registeredComponents.getOrElseUpdate(messageType, new ArrayBuffer[MessagingComponent])

    componentList += component

    Gdx.app.log(Configuration.LOG, "Registered components map: " + registeredComponents.toString)
  }
  
  /**
   * Send a new message. This will get routed to 0 or more components.
   *
   * @param message
   */
  def send(message: Message) {
    val componentList = registeredComponents.getOrElse(message.getClass,
                                                       new ArrayBuffer[MessagingComponent])
    Gdx.app.log(Configuration.LOG, "Received message -> " + message.toString)

    componentList.foreach((component: MessagingComponent) => {
      Gdx.app.log(Configuration.LOG, component.toString + " -> " + message.toString)
      component.receive(message)
    })
  }
}