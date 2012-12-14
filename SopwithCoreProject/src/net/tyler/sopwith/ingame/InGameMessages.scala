package net.tyler.sopwith.ingame

import net.tyler.messaging.Message
import net.tyler.math.CartesianVector2f

case class PlaneAccelerationChange(val acceleration: CartesianVector2f, t: Long) extends Message(t)
case class PlaneVelocityChange(val velocity: CartesianVector2f, t: Long) extends Message(t)
case class PlanePositionChange(val position: CartesianVector2f, t: Long) extends Message(t)
case class PlaneOrientationFlip(t: Long) extends Message(t)

case class BuildingDestroyed(val building: Building, t: Long) extends Message(t)

case class BombReleased(val releasePosition: CartesianVector2f, t: Long) extends Message(t)
case class BombDestroyed(val releaseTime: Long, t: Long) extends Message(t)
