package test.tyler.sopwith

import org.junit.Assert._
import org.junit.Test
import net.tyler.math.CartesianVector2f
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.StateQuerier
import net.tyler.sopwith.ingame.BombDestroyed
import net.tyler.sopwith.ingame.BombReleased
import net.tyler.sopwith.ingame.BombState
import net.tyler.sopwith.ingame.BuildingDestroyed
import net.tyler.sopwith.Configuration
import net.tyler.sopwith.ingame.InGameStateQuerier
import net.tyler.sopwith.ingame.PlaneAccelerationChange
import net.tyler.sopwith.ingame.PlanePositionChange
import net.tyler.sopwith.ingame.PlaneState
import net.tyler.sopwith.ingame.PlaneVelocityChange
import net.tyler.math.CartesianVector2f

/**
 * Test that queries regarding the bombs return the correct set of information.
 */
class BombMessageTest {
  
  private val FP_DELTA = 0.01
  
  val initialPlaneState = new PlaneState(new CartesianVector2f(10f, 10f), new CartesianVector2f(1f, 2f), new CartesianVector2f(0f, 0f), false)
  
  trait StateTester {
    private val inGameMessageTypes = List(classOf[PlaneAccelerationChange],
                                          classOf[PlaneVelocityChange],
                                          classOf[PlanePositionChange],
                                          classOf[BombDestroyed],
                                          classOf[BombReleased],
                                          classOf[BuildingDestroyed])
    val messagingComponent = new MessagingComponent(inGameMessageTypes)
    
    val querier = new InGameStateQuerier(initialPlaneState, List(), 5, 0, messagingComponent)
  }
  
  @Test def oneLiveBomb {
    new ApplicationTester with StateTester {
      messagingComponent.send(new BombReleased(new CartesianVector2f(10f, 100f), 10))
      
      assertEquals(querier.liveBombs(9).size, 0)
      assertEquals(querier.liveBombs(11).size, 1)
      
      val msg: BombState = querier.liveBombs(1010).head
      
      assertEquals(msg.velocity.x, 0f, FP_DELTA)
      assertEquals(msg.velocity.y, Configuration.BOMB_ACCELERATION, FP_DELTA)
      assertEquals(msg.position.x, 10f, FP_DELTA)
      assertEquals(msg.position.y, 100f + 1.5f * Configuration.BOMB_ACCELERATION, FP_DELTA)
    }
  }
  
  @Test def bombsRemaining {
    new ApplicationTester with StateTester {
      messagingComponent.send(new BombReleased(new CartesianVector2f(10f, 100f), 10))
      messagingComponent.send(new BombReleased(new CartesianVector2f(10f, 100f), 15))
      messagingComponent.send(new BombReleased(new CartesianVector2f(10f, 100f), 20))
      
      assertEquals(querier.bombsRemaining(5), 5)
      assertEquals(querier.bombsRemaining(11), 4)
      assertEquals(querier.bombsRemaining(16), 3)
      assertEquals(querier.bombsRemaining(21), 2)
    }
  }
}