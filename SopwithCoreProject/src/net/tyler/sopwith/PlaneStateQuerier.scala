package net.tyler.sopwith

import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.StateQuerier
import net.tyler.math.CartesianVector2f
import scala.annotation.tailrec
import net.tyler.messaging.Message
import com.badlogic.gdx.Gdx

/**
 * This class provides all querying functions required against the plane data.
 * 
 * This includes the ability to calculate position, velocity etc at any
 * given time t.
 */
class PlaneStateQuerier(initPlaneState: PlaneState, 
                        createTime: Long, 
                        messagingComponent: MessagingComponent) extends StateQuerier(messagingComponent) {

  private def planeAccelerationEvents(tMin: Long, tMax: Long) = messageEvents[PlaneAccelerationChange](tMin, tMax)
  private def planeVelocityEvents(tMin: Long, tMax: Long) = messageEvents[PlaneVelocityChange](tMin, tMax)
  private def planePositionAffectingEvents(tMin: Long, tMax: Long) =
    planeAccelerationEvents(tMin, tMax) ++ planeVelocityEvents(tMin, tMax)
    
  private def planeAccelerationEvents(t: Long) = messageEvents[PlaneAccelerationChange](t)
  private def planeVelocityEvents(t: Long) = messageEvents[PlaneVelocityChange](t)
  private def planePositionEvents(t: Long) = messageEvents[PlanePositionChange](t)
  private def planeOrientationEvents(t: Long) = messageEvents[PlaneOrientationFlip](t)
  
  /**
   * The planes acceleration at time t.
   */
  def planeAcceleration(t: Long): CartesianVector2f = {
    val events = planeAccelerationEvents(t)
    
    if (events.isEmpty) initPlaneState.acceleration else events.last.acceleration
  }
  
  /**
   * The planes velocity at time t.
   */
  def planeVelocity(t: Long): CartesianVector2f = {
    @tailrec def recurCalcVelocity(accelerationChanges: List[PlaneAccelerationChange],
                                   acc: CartesianVector2f,
                                   vel: CartesianVector2f,
                                   currentTime: Long): CartesianVector2f = accelerationChanges match {
      case Nil => {
        (vel + acc.scale((t - currentTime) / 1000f)).cap(Configuration.MAX_PLANE_VELOCITY)
      }
      case head :: tail => {
        recurCalcVelocity(tail, 
                          head.acceleration, 
                          vel + acc.scale((head.t - currentTime) / 1000f), 
                          head.t)
      }
    }
    
    val events = planeVelocityEvents(t)
    
    val initVel = if (events.isEmpty) initPlaneState.velocity else events.last.velocity
    val initAcc = if (events.isEmpty) initPlaneState.acceleration else planeAcceleration(events.last.t)
    val initT = if (events.isEmpty) createTime else events.last.t
    
    recurCalcVelocity(planeAccelerationEvents(initT, t).sortBy(_.ticks).toList, 
                      initAcc, 
                      initVel, 
                      initT)
  } 
    
  /**
   * The planes position at time t.
   */
  def planePosition(t: Long): CartesianVector2f = {    
    @tailrec def recurCalcPosition(relevantChanges: List[Message],
                                   pos: CartesianVector2f,
                                   vel: CartesianVector2f,
                                   acc: CartesianVector2f,
                                   currentTime: Long): CartesianVector2f = relevantChanges match {
      case Nil => {
        val deltaT = (t - currentTime) / 1000f
        pos + vel.scale(deltaT) + acc.scale(0.5f * deltaT * deltaT)
      }
      case (head: PlaneVelocityChange) :: tail => {
        val deltaT = (head.t - currentTime) / 1000f
        recurCalcPosition(tail, 
                          pos + vel.scale(deltaT) + acc.scale(0.5f * deltaT * deltaT), 
                          head.velocity, 
                          acc, 
                          head.t)
      }
      case (head: PlaneAccelerationChange) :: tail => {
        val deltaT = (head.t - currentTime) / 1000f
        recurCalcPosition(tail, 
                          pos + vel.scale(deltaT) + acc.scale(0.5f * deltaT * deltaT), 
                          vel + acc.scale(deltaT), 
                          head.acceleration, 
                          head.t)
      }
      case (head: Message) :: tail => {
        Gdx.app.error(Configuration.LOG, "Wrong message type in plane position case match. Ignored")
        recurCalcPosition(tail, pos, vel, acc, currentTime)
      }
    }
    
    val events = planePositionEvents(t)
    val initPos = if (events.isEmpty) initPlaneState.position else events.last.position
    val initVel = if (events.isEmpty) initPlaneState.velocity else planeVelocity(events.last.t)
    val initAcc = if (events.isEmpty) initPlaneState.acceleration else planeAcceleration(events.last.t)
    val initT = if (events.isEmpty) createTime else events.last.t
    
    recurCalcPosition(planePositionAffectingEvents(initT, t).sortBy(_.ticks).toList,
                      initPos,
                      initVel,
                      initAcc,
                      createTime)
  }
  
  /**
   * The orientation of the plane at time t.
   */
  def planeOrientation(t: Long): Boolean =
    planeOrientationEvents(t).size % 2 == 1
}