package net.tyler.sopwith

import scala.annotation.tailrec
import scala.collection.immutable.List
import net.tyler.math.ImmutableVector2f
import net.tyler.messaging.StateQuerier
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.Message

class InGameStateQuerier(val planeState: PlaneState, 
                         val buildings: List[Building],
                         val createTime: Long,
                         messagingComponent: MessagingComponent) extends StateQuerier(messagingComponent) {
  
  private def planeVelocityEvents(t: Long) = messageEvents[PlaneVelocityChange](t)
  private def planeAngularVelocityEvents(t: Long) = messageEvents[PlaneAngularVelocityChange](t)
  private def planeOrientationEvents(t: Long) = messageEvents[PlaneOrientationFlip](t)
  
  private def bombReleaseEvents(t:Long) = messageEvents[BombReleased](t)
  private def bombDestroyedEvents(t: Long) = messageEvents[BombDestroyed](t)

  /**
   * The planes velocity at time t.
   */
  private def planeVelocity(t: Long): ImmutableVector2f = {
    val events = planeVelocityEvents(t)
    
    if (events.isEmpty) planeState.velocity else events.last.velocity
  } 
    
  /**
   * The planes position at time t.
   */
  private def planePosition(t: Long): ImmutableVector2f = {
    @tailrec def recurCalcPosition(velocityChanges: Seq[PlaneVelocityChange],
                                   currentPos: ImmutableVector2f,
                                   currentVel: ImmutableVector2f,
                                   currentTime: Long): ImmutableVector2f = velocityChanges match {
      case Nil => {
        currentPos + currentVel.scale((t - currentTime) / 1000f)
      }
      case head :: tail => {
        recurCalcPosition(tail, currentPos + currentVel.scale((head.t - currentTime) / 1000f), head.velocity, head.t)
      }
    }
    
    recurCalcPosition(planeVelocityEvents(t).sortWith((e1: PlaneVelocityChange, e2: PlaneVelocityChange) => e1.t < e2.t),
                      planeState.position,
                      planeState.velocity,
                      createTime)
  }
  
  /**
   * The planes angular velocity at time t.
   */
  private def planeAngularVelocity(t: Long): Float = {
    val events = planeAngularVelocityEvents(t)
    
    if (events.isEmpty) planeState.angularVelocity else events.last.velocity
  }
    
  /**
   * The planes angle at time t.
   */
  private def planeAngle(t: Long): Float = {
    @tailrec def recurCalcAngle(velocityChanges: Seq[PlaneAngularVelocityChange],
                                currentAngle: Float,
                                currentAngularVelocity: Float,
                                currentTime: Long): Float = velocityChanges match {
      case Nil => {
        currentAngle + currentAngularVelocity * ((t - currentTime) / 1000f)
      }
      case head :: tail => {
        recurCalcAngle(tail, currentAngle + currentAngularVelocity * ((t - currentTime) / 1000f), head.velocity, head.t)
      }
    }
    
    val unadjAngle = recurCalcAngle(planeAngularVelocityEvents(t).sortWith((e1: PlaneAngularVelocityChange, e2: PlaneAngularVelocityChange) => e1.t < e2.t), 
                                    planeState.angle, 
                                    planeState.angularVelocity, 
                                    createTime) % scala.math.Pi * 2f
                                    
    if (unadjAngle < 0f) (unadjAngle + scala.math.Pi * 2f).toFloat else unadjAngle.toFloat
  }
  
  /**
   * The orientation of the plane at time t.
   */
  private def planeOrientation(t: Long): Boolean =
    planeOrientationEvents(t).size % 2 == 1
    
  /**
   * A list of all the bombs which are still alive at time t. 
   */
  def liveBombs(t: Long): Seq[BombState] = {
    val validBombs = bombReleaseEvents(t).filter((event: BombReleased) => {
      bombDestroyedEvents(t).count(_.releasePosition == event.releasePosition) == 0
    })
    
    validBombs.map(calculateBombState(_, t))
  }
  
  /**
   * A single bombs state can be derived from its initial release point and the
   * current time (since the only force acting on it is from gravity).
   */
  private def calculateBombState(releasedEvent: BombReleased, t: Long): BombState = {
    val deltaT = (t - releasedEvent.t) / 1000f
    val acc = Configuration.BOMB_ACCELERATION
    val vel = new ImmutableVector2f(0f, scala.math.min(acc * deltaT, Configuration.BOMB_TERMINAL_VEL))
    val pos = new ImmutableVector2f(releasedEvent.releasePosition.x, vel.y * deltaT + 0.5f * acc * deltaT * deltaT)
    
    new BombState(pos, vel)
  }
  
  def bombsRemaining(t: Long): Int = {
    5
  }
  
  /**
   * The full plane state at any given time t. This is used to draw the plane
   * at the correct location and to determine whether any collisions have 
   * occurred.
   */
  def planeState(t: Long): PlaneState =
    new PlaneState(planePosition(t), planeVelocity(t), 
                   planeAngle(t), planeAngularVelocity(t), 
                   planeOrientation(t))
  
  /**
   * A list of all the buildings with their current state at a give time t.
   * 
   * Used to determine where to draw buildings and whether anything has hit 
   * them on an update loop.
   */
  def buildings(t: Long): List[BuildingState] =
    buildings.map((building: Building) => new BuildingState(building, true))
}