package net.tyler.sopwith.ingame

import com.badlogic.gdx.math.MathUtils

import net.tyler.math.CartesianVector2f

case class BombState(val position: CartesianVector2f, 
                     val velocity: CartesianVector2f, 
                     //val releasePosition: CartesianVector2f,
                     val releaseTime: Long)

case class PlaneState(val position: CartesianVector2f,
                      val velocity: CartesianVector2f,
                      val acceleration: CartesianVector2f,
                      val flipped: Boolean) {
  /**
   * The angle is between 180 (inc) and -180 (not inc) and is in degrees
   * since that's what libgdx rotation functions use.
   */
  def angle = MathUtils.radiansToDegrees * velocity.angle
}
                      
case class BuildingState(val building: Building, val isLive: Boolean) {
  def position = building.position
}