package net.tyler.math

import scala.math._

object CartesianVectorConstants {
  val zero = new CartesianVector2f(0f, 0f)
}

/**
 * Custom written immutable floating point vector since the Vector2f included
 * with Slick2d is mutable when using functions like scale.
 *
 * @param x - Floating point coordinates
 * @param y - Floating point coordinates
 */
case class CartesianVector2f(val x: Float, val y: Float) {
  def +(v: CartesianVector2f): CartesianVector2f = new CartesianVector2f(x + v.x, y + v.y)

  def -(v: CartesianVector2f): CartesianVector2f = new CartesianVector2f(x - v.x, y - v.y)

  def scale(factor: Float): CartesianVector2f = new CartesianVector2f(x * factor, y * factor)

  def length: Float = math.sqrt(this dot this).toFloat
  
  def dot(v: CartesianVector2f): Float = x * v.x + y * v.y
  
  def angle: Float = atan2(y, x).toFloat
  
  def rotate(angle: Float): CartesianVector2f = 
    new CartesianVector2f((x * cos(angle) - y * sin(angle)).asInstanceOf[Float], 
                          (x * sin(angle) + y * cos(angle)).asInstanceOf[Float])
  
  def toPolar: PolarVector2f = new PolarVector2f(length, angle) 

  override def toString = "(" + x.toString + "," + y.toString + ")"
}