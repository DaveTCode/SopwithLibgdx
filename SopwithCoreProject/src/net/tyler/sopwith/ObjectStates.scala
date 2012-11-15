package net.tyler.sopwith

import net.tyler.math.ImmutableVector2f

case class BombState(val position: ImmutableVector2f, val velocity: ImmutableVector2f)

case class PlaneState(val position: ImmutableVector2f,
                      val velocity: ImmutableVector2f,
                      val angle: Float,
                      val angularVelocity: Float,
                      val flipped: Boolean)
                      
case class BuildingState(val building: Building, val isLive: Boolean) {
  def position = building.position
}