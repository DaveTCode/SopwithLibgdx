package net.tyler.sopwith

import net.tyler.math.CartesianVector2f
import scala.annotation.tailrec
import net.tyler.messaging.StateQuerier
import net.tyler.messaging.Message
import net.tyler.math.PolarVector2f

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
  private def orientationEvents(t: Long) = stateQuerier.messageEvents[PlaneOrientationFlip](t)
  
  private def positionAffectingEvents(t:Long) = stateQuerier.multiMessageEvents(
                                    Seq(classOf[PlaneAccelerationChange],
                                        classOf[PlaneVelocityChange]), t)
  
  /**
   * The planes acceleration at time t.
   */
  def planeAcceleration(t: Long): PolarVector2f = {
    val events = accelerationEvents(t)
    
    if (events.isEmpty) initPlaneState.acceleration else events.last.acceleration
  }
  
  /**
   * The planes velocity at time t.
   */
  def planeVelocity(t: Long): PolarVector2f = {
    @tailrec def recurCalcVelocity(messages: List[PlaneAccelerationChange],
                                   vel: PolarVector2f,
                                   acc: PolarVector2f,
                                   currentTime: Long): PolarVector2f = messages match {
      case Nil => {
        new PolarVector2f()
      } 
      case head :: tail => {
        
      }
    }
    
    val events = velocityEvents(t)
    val initVel = if (events.isEmpty) initPlaneState.velocity else events.last.velocity
    val initAcc = if (events.isEmpty) initPlaneState.acceleration else planeAcceleration(events.last.t)
    
    recurCalcVelocity(accelerationEvents(t).sortBy(_.ticks).toList, 
                      initVel, initAcc, 
                      t)
  } 
    
  /**
   * The planes position at time t.
   */
  def planePosition(t: Long): PolarVector2f = {
    @tailrec def recurCalcPosition(messages: List[Message],
                                   pos: PolarVector2f, vel: PolarVector2f, acc: PolarVector2f,
                                   currentTime: Long): PolarVector2f = messages match {
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
    }
    
    val events = positionEvents(t)
    val initPos = if (events.isEmpty) initPlaneState.polarCoordinates else events.last.position
    val initVel = if (events.isEmpty) initPlaneState.velocity else planeVelocity(events.last.t)
    val initAcc = if (events.isEmpty) initPlaneState.acceleration else planeAcceleration(events.last.t)
    
    recurCalcPosition(positionAffectingEvents(t).sortBy(_.ticks).toList,
                      initPos, initVel, initAcc,
                      createTime)
  }
  
  /**
   * The orientation of the plane at time t.
   */
  def planeOrientation(t: Long): Boolean = orientationEvents(t).size % 2 == 1
}