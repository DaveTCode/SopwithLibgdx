package net.tyler.sopwith

import net.tyler.math.CartesianVector2f
import com.badlogic.gdx.math.MathUtils

case class BombState(val position: CartesianVector2f, 
                     val velocity: CartesianVector2f, 
                     val releasePosition: CartesianVector2f)

case class PlaneState(val position: CartesianVector2f,
                      val velocity: CartesianVector2f,
                      val acceleration: CartesianVector2f,
                      val flipped: Boolean) {
  def angle = MathUtils.radiansToDegrees * velocity.angle
}
                      
case class BuildingState(val building: Building, val isLive: Boolean) {
  def position = building.position
}