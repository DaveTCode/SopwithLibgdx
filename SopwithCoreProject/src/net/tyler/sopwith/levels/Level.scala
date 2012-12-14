package net.tyler.sopwith.levels

import net.tyler.sopwith.ingame.PlaneState
import net.tyler.sopwith.ingame.Building
import net.tyler.math.CartesianVector2f
import net.tyler.math.CartesianVectorConstants
import net.tyler.math.CartesianVector2f

abstract class Level {
  /**
   * Level dimensions
   */
  def width: Int
  def height: Int
  
  /**
   * Access to the initial list of buildings on this level.
   */
  def buildings: List[Building]
  
  /**
   * Access to the planes initial state on the level.
   */
  def plane: PlaneState
  
  /**
   * A series of points that turns into the ground line.
   */
  def ground: List[(Int, Int)]
  
  /**
   * Return a list of line segments starting from the first ground point and
   * ending at the last. There will be ground.size - 1 line segments.
   */
  def groundSegments: List[(CartesianVector2f, CartesianVector2f)] = ground match {
    case Nil => Nil
    case _ :: Nil => Nil
    case starts => {
      (starts.init, ground.tail).zipped.map({
        case ((x1, y1), (x2, y2)) => (new CartesianVector2f(x1, y1), new CartesianVector2f(x2, y2))
      })
    }
  }
  
  /**
   * A list of the edges of the map as lines which can be checked for 
   * collisions.
   */
  def edges: List[(CartesianVector2f, CartesianVector2f)] = {
    List((new CartesianVector2f(0f, 0f), new CartesianVector2f(0f, height)),
         (new CartesianVector2f(0f, 0f), new CartesianVector2f(width, 0f)),
         (new CartesianVector2f(width, height), new CartesianVector2f(0f, height)),
         (new CartesianVector2f(width, height), new CartesianVector2f(width, 0f)))
  }
}