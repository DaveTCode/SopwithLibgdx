package net.tyler.sopwith

import net.tyler.messaging.Message
import net.tyler.math.CartesianVector2f
import net.tyler.math.CartesianVector2f
import net.tyler.math.PolarVector2f

case class PlaneAccelerationChange(val acceleration: PolarVector2f, t: Long) extends Message(t)
case class PlaneVelocityChange(val velocity: PolarVector2f, t: Long) extends Message(t)
case class PlanePositionChange(val position: PolarVector2f, t: Long) extends Message(t)

case class PlaneOrientationFlip(t: Long) extends Message(t)

case class BuildingDestroyed(val building: Building, t: Long) extends Message(t)

case class BombReleased(val releasePosition: CartesianVector2f, t: Long) extends Message(t)
case class BombDestroyed(val releasePosition: CartesianVector2f, t: Long) extends Message(t)
