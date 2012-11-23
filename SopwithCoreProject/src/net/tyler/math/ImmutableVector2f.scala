package net.tyler.math

import scala.math._

object Vector2fConstants {
  val zero = ImmutableVector2f(0f, 0f)
} 

/**
 * Custom written immutable floating point vector since the Vector2f included
 * with Slick2d is mutable when using functions like scale.
 *
 * @param x - Floating point coordinates
 * @param y - Floating point coordinates
 */
case class ImmutableVector2f(val x: Float, val y: Float) {
  def +(v: ImmutableVector2f): ImmutableVector2f = new ImmutableVector2f(x + v.x, y + v.y)

  def -(v: ImmutableVector2f): ImmutableVector2f = new ImmutableVector2f(x - v.x, y - v.y)

  def scale(factor: Float): ImmutableVector2f = new ImmutableVector2f(x * factor, y * factor)

  def length: Float = math.sqrt(this dot this).toFloat
  
  def dot(v: ImmutableVector2f): Float = x * v.x + y * v.y
  
  def rotate(angle: Float): ImmutableVector2f = 
    new ImmutableVector2f((x * cos(angle) - y * sin(angle)).asInstanceOf[Float], 
                          (x * sin(angle) + y * cos(angle)).asInstanceOf[Float])

  override def toString = "(" + x.toString + "," + y.toString + ")"
}