package net.tyler.sopwith

import net.tyler.messaging.Message
import net.tyler.math.ImmutableVector2f

case class PlaneAccelerationChange(val acceleration: ImmutableVector2f, t: Long) extends Message(t)
case class PlaneVelocityChange(val velocity: ImmutableVector2f, t: Long) extends Message(t)
case class PlanePositionChange(val position: ImmutableVector2f, t: Long) extends Message(t)
case class PlaneOrientationFlip(t: Long) extends Message(t)

case class BuildingDestroyed(val building: Building, t: Long) extends Message(t)

case class BombReleased(val releasePosition: ImmutableVector2f, t: Long) extends Message(t)
case class BombDestroyed(val releasePosition: ImmutableVector2f, t: Long) extends Message(t)
