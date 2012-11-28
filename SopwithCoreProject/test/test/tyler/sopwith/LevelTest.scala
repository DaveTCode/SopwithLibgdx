package test.tyler.sopwith

import org.junit.Assert._
import org.junit.Test
import net.tyler.sopwith.levels.Level
import net.tyler.math.CartesianVectorConstants
import net.tyler.sopwith.PlaneState
import net.tyler.math.CartesianVector2f

/**
 * Test functions attached to the level object.
 */
class LevelTest {

  private val levelTest = new Level {
    def buildings = Nil
    def plane = new PlaneState(CartesianVectorConstants.zero, CartesianVectorConstants.zero, CartesianVectorConstants.zero, false)
    def width = 100
    def height = 100
    def ground = List((0, 10),(10,10),(20, 0))
  }
  
  private val emptyGroundPointTest = new Level {
    def buildings = Nil
    def plane = new PlaneState(CartesianVectorConstants.zero, CartesianVectorConstants.zero, CartesianVectorConstants.zero, false)
    def width = 100
    def height = 100
    def ground = Nil
  }
  
  private val oneGroundPointTest = new Level {
    def buildings = Nil
    def plane = new PlaneState(CartesianVectorConstants.zero, CartesianVectorConstants.zero, CartesianVectorConstants.zero, false)
    def width = 100
    def height = 100
    def ground = Nil
  }
  
  @Test def checkZeroGroundPoints() {
    assertEquals(Nil, emptyGroundPointTest.groundSegments)
  }
  
  @Test def checkOneGroundPoint() {
    assertEquals(Nil, oneGroundPointTest.groundSegments)
  }
  
  @Test def checkGroundPoints() {
    assertEquals(List((new CartesianVector2f(0, 10), new CartesianVector2f(10, 10)), 
                      (new CartesianVector2f(10,10), new CartesianVector2f(20,0))), 
                 levelTest.groundSegments)
  }
}