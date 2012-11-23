package net.tyler.sopwith

import net.tyler.math.ImmutableVector2f
import scala.annotation.tailrec
import net.tyler.messaging.StateQuerier
import net.tyler.messaging.Message

/**
 * Sub component of the state querier class which controls querying of the 
 * planes state. Essentially just a logical grouping of all plane state 
 * functions.
 */
class InGamePlaneStateQuerier(val initPlaneState: PlaneState, 
                              val createTime: Long,
                              val stateQuerier: StateQuerier) {
  
  private def accelerationEvents(t: Long) = stateQuerier.messageEvents[PlaneAccelerationChange](t)
  private def velocityEvents(t: Long) = stateQuerier.messageEvents[PlaneVelocityChange](t)
  private def positionEvents(t: Long) = stateQuerier.messageEvents[PlanePositionChange](t)
  private def angularAccelerationEvents(t: Long) = stateQuerier.messageEvents[PlaneAngularAccelerationChange](t)
  private def angularVelocityEvents(t: Long) = stateQuerier.messageEvents[PlaneAngularVelocityChange](t)
  private def angleEvents(t: Long) = stateQuerier.messageEvents[PlaneAngleChange](t)
  private def orientationEvents(t: Long) = stateQuerier.messageEvents[PlaneOrientationFlip](t)
  
  private def positionAffectingEvents(t:Long) = stateQuerier.multiMessageEvents(
                                    Seq(classOf[PlaneAccelerationChange],
                                        classOf[PlaneVelocityChange],
                                        classOf[PlaneAngularAccelerationChange],
                                        classOf[PlaneAngularVelocityChange],
                                        classOf[PlaneAngleChange]), t)
  
  /**
   * The planes acceleration at time t.
   */
  def planeAcceleration(t: Long): ImmutableVector2f = {
    val events = accelerationEvents(t)
    
    if (events.isEmpty) {
      initPlaneState.acceleration.rotate(initPlaneState.angle) 
    } else {
      events.last.acceleration.rotate(planeAngle(events.last.t))
    }
  }
  
  /**
   * The planes velocity at time t.
   */
  def planeVelocity(t: Long): ImmutableVector2f = {
    val events = velocityEvents(t)
    
    if (events.isEmpty) {
      initPlaneState.velocity.rotate(initPlaneState.angle) 
    } else {
      events.last.velocity.rotate(planeAngle(events.last.t))
    }
  } 
    
  /**
   * The planes position at time t.
   */
  def planePosition(t: Long): ImmutableVector2f = {
    @tailrec def recurCalcPosition(messages: List[Message],
                                   pos: ImmutableVector2f, vel: ImmutableVector2f, acc: ImmutableVector2f,
                                   angle: Float, angularVel: Float, angularAcc: Float,
                                   currentTime: Long): ImmutableVector2f = messages match {
      case Nil => {
        pos + vel.scale((t - currentTime) / 1000f)
      }
      case (head: PlaneAccelerationChange) :: tail => {
        recurCalcPosition(tail, 
                          pos + vel.scale((currentTime - head.t) / 1000f), 
                          vel + acc.scale((currentTime - head.t) / 1000f), head.acceleration, 
                          angle, angularVel, angularAcc, 
                          head.t)
      }
      case (head: PlaneVelocityChange) :: tail => {
        recurCalcPosition(tail, 
                          pos + vel.scale((currentTime - head.t) / 1000f), head.velocity, acc, 
                          angle, angularVel, angularAcc, 
                          head.t)
      }
      case (head: PlaneAngularAccelerationChange) :: tail => {
        recurCalcPosition(tail, 
                          pos + vel.scale((currentTime - head.t) / 1000f), head.velocity, acc, 
                          angle, angularVel, angularAcc, 
                          head.t)
      }
      case (head: PlaneAngularVelocityChange) :: tail => {
        
      }
      case (head: PlaneAngleChange) :: tail => {
        
      }
    }
    
    val events = positionEvents(t)
    val initPos = if (events.isEmpty) initPlaneState.position else events.last.position
    val initVel = if (events.isEmpty) initPlaneState.velocity else planeVelocity(events.last.t)
    val initAcc = if (events.isEmpty) initPlaneState.acceleration else planeAcceleration(events.last.t)
    val initAngle = if (events.isEmpty) initPlaneState.angle else planeAngle(events.last.t)
    val initAngularVel = if (events.isEmpty) initPlaneState.angularVelocity else planeAngularVelocity(events.last.t)
    val initAngularAcc = if (events.isEmpty) initPlaneState.angularAcceleration else planeAngularAcceleration(events.last.t)
    
    recurCalcPosition(positionAffectingEvents(t).sortBy(_.ticks).toList,
                      initPos, initVel, initAcc,
                      initAngle, initAngularVel, initAngularAcc,
                      createTime)
  }
  
  /**
   * The planes angular acceleration at time t.
   */
  def planeAngularAcceleration(t: Long): Float = {
    val events = angularAccelerationEvents(t)
    
    if (events.isEmpty) initPlaneState.angularAcceleration else events.last.acceleration
  }
  
  /**
   * The planes angular velocity at time t.
   */
  def planeAngularVelocity(t: Long): Float = {
    val events = angularVelocityEvents(t)
    
    if (events.isEmpty) initPlaneState.angularVelocity else events.last.velocity
  }
    
  /**
   * The planes angle at time t.
   */
  def planeAngle(t: Long): Float = {
    @tailrec def recurCalcAngle(velocityChanges: List[Message],
                                currentAngle: Float,
                                currentAngularVelocity: Float,
                                currentAngularAcceleration: Float,
                                currentTime: Long): Float = velocityChanges match {
      case Nil => {
        currentAngle + currentAngularVelocity * (t - currentTime) / 1000f
      }
      case (m: PlaneAngularAccelerationChange) :: tail => {
        recurCalcAngle(tail, 
                       currentAngle + currentAngularVelocity * (currentTime - m.t) / 1000f, 
                       currentAngularVelocity + m.acceleration * (currentTime - m.t) / 1000f, 
                       m.acceleration, 
                       m.t)
      }
      case (m: PlaneAngularVelocityChange) :: tail => {
        recurCalcAngle(tail, 
                       currentAngle + currentAngularVelocity * (currentTime - m.t) / 1000f, 
                       m.velocity, 
                       currentAngularAcceleration, 
                       m.t)
      }
    }
    
    val events = angleEvents(t).sortBy(_.ticks).toList
    val initAngle = if (events.isEmpty) initPlaneState.angle else events.last.angle
    val initAngularVelocity = if (events.isEmpty) initPlaneState.angularVelocity else planeAngularVelocity(events.last.t)
    val initAngularAcceleration = if (events.isEmpty) initPlaneState.angularAcceleration else planeAngularAcceleration(events.last.t)
    
    val affectingEvents = (angularVelocityEvents(t) ++ angularAccelerationEvents(t)).sortBy(_.ticks).toList
    
    val unadjAngle = recurCalcAngle(affectingEvents,
                                    initAngle, 
                                    initAngularVelocity,
                                    initAngularAcceleration,
                                    createTime) % (scala.math.Pi * 2f)
                                    
    if (unadjAngle < 0f) (unadjAngle + scala.math.Pi * 2f).toFloat else unadjAngle.toFloat
  }
  
  /**
   * The orientation of the plane at time t.
   */
  def planeOrientation(t: Long): Boolean =
    orientationEvents(t).size % 2 == 1
}