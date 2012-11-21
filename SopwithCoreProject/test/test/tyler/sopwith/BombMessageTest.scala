package test.tyler.sopwith

import org.junit.Assert._
import org.junit.Test
import net.tyler.sopwith.PlaneVelocityChange
import net.tyler.sopwith.PlaneAngularVelocityChange
import net.tyler.sopwith.BombDestroyed
import net.tyler.sopwith.BombReleased
import net.tyler.sopwith.BuildingDestroyed
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.StateQuerier
import net.tyler.math.ImmutableVector2f
import net.tyler.sopwith.InGameStateQuerier
import net.tyler.sopwith.PlaneState
import net.tyler.sopwith.BombState
import net.tyler.sopwith.Configuration

/**
 * Test that queries regarding the bombs return the correct set of information.
 */
class BombMessageTest {
  
  private val FP_DELTA = 0.01
  
  val initialPlaneState = new PlaneState(new ImmutableVector2f(10f, 10f), new ImmutableVector2f(1f, 2f), 0f, 0f, false)
  
  trait StateTester {
    private val inGameMessageTypes = List(classOf[PlaneVelocityChange],
                                          classOf[PlaneAngularVelocityChange],
                                          classOf[BombDestroyed],
                                          classOf[BombReleased],
                                          classOf[BuildingDestroyed])
    val messagePassing = new MessagePassing
    val messagingComponent = new MessagingComponent(messagePassing, inGameMessageTypes)
    
    val querier = new InGameStateQuerier(initialPlaneState, List(), 5, 0, messagingComponent)
  }
  
  @Test def oneLiveBomb {
    new ApplicationTester with StateTester {
      messagePassing.send(new BombReleased(new ImmutableVector2f(10f, 100f), 10))
      
      assertEquals(querier.liveBombs(9).size, 0)
      assertEquals(querier.liveBombs(11).size, 1)
      
      val msg: BombState = querier.liveBombs(1010).head
      
      assertEquals(msg.velocity.x, 0f, FP_DELTA)
      assertEquals(msg.velocity.y, Configuration.BOMB_ACCELERATION, FP_DELTA)
      assertEquals(msg.position.x, 10f, FP_DELTA)
      assertEquals(msg.position.y, 1.5f * Configuration.BOMB_ACCELERATION, FP_DELTA)
    }
  }
  
  @Test def bombsRemaining {
    new ApplicationTester with StateTester {
      messagePassing.send(new BombReleased(new ImmutableVector2f(10f, 100f), 10))
      messagePassing.send(new BombReleased(new ImmutableVector2f(10f, 100f), 15))
      messagePassing.send(new BombReleased(new ImmutableVector2f(10f, 100f), 20))
      
      assertEquals(querier.bombsRemaining(5), 5)
      assertEquals(querier.bombsRemaining(11), 4)
      assertEquals(querier.bombsRemaining(16), 3)
      assertEquals(querier.bombsRemaining(21), 2)
    }
  }
}