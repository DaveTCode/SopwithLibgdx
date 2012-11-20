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

class PlaneStateQuerierTest {

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
    
    val querier = new InGameStateQuerier(initialPlaneState, List(), 0, messagingComponent)
  }
  
  @Test def initialState() {
    
    new ApplicationTester with StateTester {
      assertEquals(querier.planeState(0), initialPlaneState)
      assertEquals(querier.planeState(100).velocity, initialPlaneState.velocity)
      assertEquals(querier.planeState(100).angularVelocity, initialPlaneState.angularVelocity, FP_DELTA)
    }
  }
  
  @Test def singleVelocityChange() {
    new ApplicationTester with StateTester {
      val newVelocity = new ImmutableVector2f(-1f, -0.5f)
      messagePassing.send(new PlaneVelocityChange(newVelocity, 10))
      
      assertEquals(querier.planeState(9).velocity, initialPlaneState.velocity)
      assertEquals(querier.planeState(11).velocity, newVelocity)
    }
  }
  
  @Test def positionChangeProgression() {
    new ApplicationTester with StateTester {
      val newVelocity = new ImmutableVector2f(-10f, 7f)
      messagePassing.send(new PlaneVelocityChange(newVelocity, 10))
      
      assertEquals(querier.planeState(9).position, initialPlaneState.position)
      
      val positionAfter1000ms = querier.planeState(1010).position
      assertEquals(positionAfter1000ms.x, -10f, FP_DELTA)
      assertEquals(positionAfter1000ms.y, 7f, FP_DELTA)
    }
  }
}