package net.tyler.sopwith

import net.tyler.math.CartesianVector2f
import net.tyler.math.PolarVector2f

case class BombState(val position: CartesianVector2f, 
                     val velocity: CartesianVector2f, 
                     val releasePosition: CartesianVector2f)

case class PlaneState(val acceleration: PolarVector2f,
                      val velocity: PolarVector2f,
                      val polarCoordinates: PolarVector2f,
                      val flipped: Boolean) {
  def position: CartesianVector2f = polarCoordinates.toCartesian
}
                      
case class BuildingState(val building: Building, val isLive: Boolean) {
  def position = building.position
}