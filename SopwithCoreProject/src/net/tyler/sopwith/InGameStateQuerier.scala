package net.tyler.sopwith

import scala.annotation.tailrec
import scala.collection.immutable.List
import net.tyler.math.CartesianVector2f
import net.tyler.messaging.StateQuerier
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.Message
import com.badlogic.gdx.Gdx

class InGameStateQuerier(val initPlaneState: PlaneState, 
                         val initBuildings: Iterable[Building],
                         val initBombs: Int,
                         val createTime: Long,
                         messagingComponent: MessagingComponent) extends StateQuerier(messagingComponent) {
  
  private def planeAccelerationEvents(tMin: Long, tMax: Long) = messageEvents[PlaneAccelerationChange](tMin, tMax)
  private def planeVelocityEvents(tMin: Long, tMax: Long) = messageEvents[PlaneVelocityChange](tMin, tMax)
  private def planePositionAffectingEvents(tMin: Long, tMax: Long) =
    planeAccelerationEvents(tMin, tMax) ++ planeVelocityEvents(tMin, tMax)
    
  private def planeAccelerationEvents(t: Long) = messageEvents[PlaneAccelerationChange](t)
  private def planeVelocityEvents(t: Long) = messageEvents[PlaneVelocityChange](t)
  private def planePositionEvents(t: Long) = messageEvents[PlanePositionChange](t)
  private def planeOrientationEvents(t: Long) = messageEvents[PlaneOrientationFlip](t)
  
  private def bombReleaseEvents(t:Long) = messageEvents[BombReleased](t)
  private def bombDestroyedEvents(t: Long) = messageEvents[BombDestroyed](t)
  
  private def buildingDestroyedEvents(t: Long) = messageEvents[BuildingDestroyed](t)

  /**
   * The planes acceleration at time t.
   */
  private def planeAcceleration(t: Long): CartesianVector2f = {
    val events = planeAccelerationEvents(t)
    
    if (events.isEmpty) initPlaneState.acceleration else events.last.acceleration
  }
  
  /**
   * The planes velocity at time t.
   */
  private def planeVelocity(t: Long): CartesianVector2f = {
    @tailrec def recurCalcVelocity(accelerationChanges: List[PlaneAccelerationChange],
                                   acc: CartesianVector2f,
                                   vel: CartesianVector2f,
                                   currentTime: Long): CartesianVector2f = accelerationChanges match {
      case Nil => {
        vel + acc.scale((t - currentTime) / 1000f)
      }
      case head :: tail => {
        recurCalcVelocity(tail, head.acceleration, vel + acc.scale((head.t - currentTime) / 1000f), head.t)
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
  private def planePosition(t: Long): CartesianVector2f = {    
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
  private def planeOrientation(t: Long): Boolean =
    planeOrientationEvents(t).size % 2 == 1
    
  /**
   * A list of all the bombs which are still alive at time t. 
   */
  def liveBombs(t: Long): Iterable[BombState] = {
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
    val vel = new CartesianVector2f(0f, acc * deltaT)
    val pos = new CartesianVector2f(releasedEvent.releasePosition.x, 
                                    releasedEvent.releasePosition.y + vel.y * deltaT + 0.5f * acc * deltaT * deltaT)
    
    new BombState(pos, vel, releasedEvent.releasePosition)
  }
  
  /**
   * Returns the number of bombs that the plane has left at any given point.
   */
  def bombsRemaining(t: Long): Int = initBombs - bombReleaseEvents(t).size
  
  /**
   * The full plane state at any given time t. This is used to draw the plane
   * at the correct location and to determine whether any collisions have 
   * occurred.
   */
  def planeState(t: Long): PlaneState =
    new PlaneState(planePosition(t), 
                   planeVelocity(t), 
                   planeAcceleration(t), 
                   planeOrientation(t))
  
  /**
   * A list of all the buildings with their current state at a give time t.
   * 
   * Used to determine where to draw buildings and whether anything has hit 
   * them on an update loop.
   */
  def buildings(t: Long): Iterable[BuildingState] =
    initBuildings.map((building: Building) => new BuildingState(building, isBuildingLive(building, t)))
  
  /**
   * Check whether the given building was destroyed at time t.
   */
  private def isBuildingLive(building: Building, t: Long): Boolean = 
    buildingDestroyedEvents(t).count(_.building == building) == 0
}