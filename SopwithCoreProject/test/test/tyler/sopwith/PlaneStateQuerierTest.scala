package test.tyler.sopwith

import org.junit.Assert._
import org.junit.Test
import net.tyler.math.ImmutableVector2f
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.sopwith.BombDestroyed
import net.tyler.sopwith.BombReleased
import net.tyler.sopwith.BuildingDestroyed
import net.tyler.sopwith.InGameStateQuerier
import net.tyler.sopwith.PlaneAngularVelocityChange
import net.tyler.sopwith.PlaneState
import net.tyler.sopwith.PlaneVelocityChange
import net.tyler.sopwith.PlaneOrientationFlip
import net.tyler.sopwith.PlaneOrientationFlip
import net.tyler.math.Vector2fConstants

/**
 * Test class for checking that the plane updates it's state correctly under
 * various message circumstances.
 */
class PlaneStateQuerierTest {

  private val FP_DELTA = 0.01
  
  val initialPlaneState = new PlaneState(Vector2fConstants.zero, 
                                         new ImmutableVector2f(1f, 2f), 
                                         new ImmutableVector2f(10f, 10f), 
                                         0f, 0f, 0f, false)
  
  trait StateTester {
    private val inGameMessageTypes = List(classOf[PlaneVelocityChange],
                                          classOf[PlaneAngularVelocityChange],
                                          classOf[PlaneOrientationFlip],
                                          classOf[BombDestroyed],
                                          classOf[BombReleased],
                                          classOf[BuildingDestroyed])
    val messagePassing = new MessagePassing
    val messagingComponent = new MessagingComponent(messagePassing, inGameMessageTypes)
    
    val querier = new InGameStateQuerier(initialPlaneState, List(), 5, 0, messagingComponent)
  }
  
  @Test def initialState() {
    
    new ApplicationTester with StateTester {
      assertEquals(initialPlaneState, querier.planeState(0))
      assertEquals(initialPlaneState.velocity, querier.planeState(100).velocity)
      assertEquals(initialPlaneState.angularVelocity, querier.planeState(100).angularVelocity, FP_DELTA)
    }
  }
  
  @Test def singleVelocityChange() {
    new ApplicationTester with StateTester {
      val newVelocity = new ImmutableVector2f(-1f, -0.5f)
      messagePassing.send(new PlaneVelocityChange(newVelocity, 10))
      
      assertEquals(initialPlaneState.velocity, querier.planeState(9).velocity)
      assertEquals(newVelocity, querier.planeState(11).velocity)
    }
  }
  
  @Test def positionChangeProgression() {
    new ApplicationTester with StateTester {
      val newVelocity = new ImmutableVector2f(-10f, 7f)
      messagePassing.send(new PlaneVelocityChange(newVelocity, 10))
      
      assertEquals(initialPlaneState.position.x + initialPlaneState.velocity.x * 0.009f, querier.planeState(9).position.x, FP_DELTA)
      assertEquals(initialPlaneState.position.y + initialPlaneState.velocity.y * 0.009f, querier.planeState(9).position.y, FP_DELTA)
      
      val positionAfter1000ms = querier.planeState(1010).position
      assertEquals((initialPlaneState.position.x + initialPlaneState.velocity.x * 0.01f) + newVelocity.x, positionAfter1000ms.x, FP_DELTA)
      assertEquals((initialPlaneState.position.y + initialPlaneState.velocity.y * 0.01f) + newVelocity.y, positionAfter1000ms.y, FP_DELTA)
    }
  }
  
  @Test def planeOrientation() {
    new ApplicationTester with StateTester {
      messagePassing.send(new PlaneOrientationFlip(10))
      
      assertEquals(false, querier.planeState(9).flipped)
      assertEquals(true, querier.planeState(11).flipped)
    }
  }
}