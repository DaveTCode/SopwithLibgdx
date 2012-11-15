package test.tyler.sopwith

import org.junit.Assert._
import org.junit.Test
import net.tyler.sopwith.InGameStateQuerier
import net.tyler.sopwith.PlaneState
import net.tyler.math.ImmutableVector2f
import net.tyler.sopwith.PlaneVelocityChange

class PlaneStateQuerierTest {

  private val VEL_DELTA = 0.01
  
  val initialPlaneState = new PlaneState(new ImmutableVector2f(10f, 10f), new ImmutableVector2f(1f, 2f), 0f, 0f)
  
  @Test def initialState() {
    val querier = new TestInGameStateQuerier(initialPlaneState, List())
    
    assertEquals(querier.planeState(0), initialPlaneState)
    assertEquals(querier.planeState(100).velocity, initialPlaneState.velocity)
    assertEquals(querier.planeState(100).angularVelocity, initialPlaneState.angularVelocity, VEL_DELTA)
  }
  
  @Test def singleVelocityChange() {
    val querier = new TestInGameStateQuerier(initialPlaneState, List())
    
    val newVelocity = new ImmutableVector2f(-1f, -0.5f)
    querier.addMessage(new PlaneVelocityChange(newVelocity, 10))
    
    assertEquals(querier.planeState(9).velocity, initialPlaneState.velocity)
    assertEquals(querier.planeState(11).velocity, newVelocity)
  }
}