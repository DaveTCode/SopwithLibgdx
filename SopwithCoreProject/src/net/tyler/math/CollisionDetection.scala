package net.tyler.math

/**
 * A set of very naive simple collision detection functions for use in games
 * where objects are not moving sufficiently quickly that one frame to the 
 * next may result in a collision with neither frame actually having the objects
 * overlapping.
 */
object CollisionDetection {

  def circleCircleOverlapping(c1: CartesianVector2f, r1: Float,
                              c2: CartesianVector2f, r2: Float) = 
    (c2 - c1).length <= r1 + r2
    
  def circlePointOverlapping(c: CartesianVector2f, r: Float, p: CartesianVector2f) =
    circleCircleOverlapping(c, r, p, 0f)
    
  def circleLineOverlapping(c1: CartesianVector2f, r1: Float, l1: CartesianVector2f, l2: CartesianVector2f) = {
    val dirOfLine = l2 - l1
    val circleToStart = l1 - c1
    
    val a = dirOfLine dot dirOfLine
    val b = circleToStart.scale(2f) dot dirOfLine
    val c = (circleToStart dot circleToStart) - (c1.y * c1.y)
    val discriminant = b * b - 4 * a * c
    
    if (discriminant >= 0) {
      val rootDiscriminant = scala.math.sqrt(discriminant)
      val t1 = (-1.0f * b + rootDiscriminant) / (2.0f * a)
      val t2 = (-1.0f * b - rootDiscriminant) / (2.0f * a)
      
      (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1) 
    } else {
      false
    }
  }
}