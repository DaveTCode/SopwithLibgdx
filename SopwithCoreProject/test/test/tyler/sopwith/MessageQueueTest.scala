package test.tyler.sopwith

import scala.collection.mutable.ArrayBuffer

import org.junit.Assert._
import org.junit.Test

import net.tyler.math.CartesianVector2f
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.StateQuerier
import net.tyler.sopwith.ingame.BombDestroyed
import net.tyler.sopwith.ingame.BombReleased
import net.tyler.sopwith.ingame.BuildingDestroyed
import net.tyler.sopwith.ingame.PlaneVelocityChange

/**
 * Test class for checking that the right set of messages are returned from the
 * queue and that they appear in the right order.
 */
class MessageQueueTest {
  trait StateTester {
    private val inGameMessageTypes = List(classOf[BombDestroyed],
                                          classOf[BombReleased])
    val messagingComponent = new MessagingComponent(inGameMessageTypes)
    val stateQuerier = new StateQuerier(messagingComponent) {}
  }
  
  @Test def singleMessage() {
    new ApplicationTester with StateTester {
      val pos = new CartesianVector2f(-100f, -100f)
      
      messagingComponent.send(new BombReleased(pos, 1959203))
      assertEquals(stateQuerier.eventsPostTickVal(0), messagingComponent.Buffer)
      assertEquals(stateQuerier.eventsPostTickVal(9999999).size, 0)
      assertEquals(stateQuerier.eventsPostTickVal(1959203).size, 0)
      assertEquals(stateQuerier.eventsPreTickVal(1959203).size, 0)
      assertEquals(stateQuerier.eventsPreTickVal(9999999), messagingComponent.Buffer)
      assertEquals(stateQuerier.eventsPreTickVal(0).size, 0)
    }
  }
  
  @Test def messageTypes() {
    new ApplicationTester with StateTester {
      val pos = new CartesianVector2f(-100f, -100f)
      
      messagingComponent.send(new BombReleased(pos, 1029478))
      messagingComponent.send(new BombDestroyed(1029478, 2029478))
      
      assertEquals(stateQuerier.messageEvents[BombReleased](99999999).size, 1)
      assertEquals(stateQuerier.messageEvents[BombDestroyed](99999999).size, 1)
    }
  }
  
  @Test def messageOrdering() {
    new ApplicationTester with StateTester {
      val pos = new CartesianVector2f(-100f, -100f)
      val msg1 = new BombReleased(pos, 1)
      val msg2 = new BombReleased(pos, 10923)
      val msg3 = new BombReleased(pos, 123)
      
      messagingComponent.send(msg1)
      messagingComponent.send(msg2)
      messagingComponent.send(msg3)
      
      assertEquals(ArrayBuffer(msg1, msg2, msg3), stateQuerier.messageEvents[BombReleased](11111))
    }
  }
}