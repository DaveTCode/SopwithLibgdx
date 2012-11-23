package net.tyler.sopwith

import net.tyler.math.ImmutableVector2f

case class BombState(val position: ImmutableVector2f, 
                     val velocity: ImmutableVector2f, 
                     val releasePosition: ImmutableVector2f)

case class PlaneState(val acceleration: ImmutableVector2f,
                      val velocity: ImmutableVector2f,
                      val position: ImmutableVector2f,
                      val angularAcceleration: Float,
                      val angularVelocity: Float,
                      val angle: Float,
                      val flipped: Boolean)
                      
case class BuildingState(val building: Building, val isLive: Boolean) {
  def position = building.position
}