package net.tyler.sopwith

import net.tyler.math.ImmutableVector2f
import com.badlogic.gdx.math.MathUtils

case class BombState(val position: ImmutableVector2f, 
                     val velocity: ImmutableVector2f, 
                     val releasePosition: ImmutableVector2f)

case class PlaneState(val position: ImmutableVector2f,
                      val velocity: ImmutableVector2f,
                      val acceleration: ImmutableVector2f,
                      val flipped: Boolean) {
  def angle = MathUtils.radiansToDegrees * velocity.angle
}
                      
case class BuildingState(val building: Building, val isLive: Boolean) {
  def position = building.position
}