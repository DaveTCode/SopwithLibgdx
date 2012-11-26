package net.tyler.math

import scala.math._

/**
 * Represents a vector in 2-space using polar coordinates. 
 */
case class PolarVector2f(val r: Float, val theta: Float) {

  private def bindAngle(p: Float): Float = {
    val a = p % (scala.math.Pi * 2f)
    (if (a < 0) a + scala.math.Pi * 2f else a).toFloat
  }
  
  def rotate(p: Float): PolarVector2f =
    new PolarVector2f(r, bindAngle(p + theta))
  
  def scale(s: Float): PolarVector2f = new PolarVector2f(s * r, theta)
  
  def toCartesian: CartesianVector2f = 
    new CartesianVector2f(r * cos(theta).toFloat, r * sin(theta).toFloat)
}