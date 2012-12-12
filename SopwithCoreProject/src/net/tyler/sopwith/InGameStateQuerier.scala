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
  
  private val planeStateQuerier = new PlaneStateQuerier(initPlaneState, createTime, messagingComponent)
  
  private def bombReleaseEvents(t:Long) = messageEvents[BombReleased](t)
  private def bombDestroyedEvents(t: Long) = messageEvents[BombDestroyed](t)
  
  private def buildingDestroyedEvents(t: Long) = messageEvents[BuildingDestroyed](t)
    
  /**
   * A list of all the bombs which are still alive at time t. 
   */
  def liveBombs(t: Long): Iterable[BombState] = {
    val validBombs = bombReleaseEvents(t).filter((event: BombReleased) => {
      bombDestroyedEvents(t).count(_.releaseTime == event.t) == 0
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

    new BombState(pos, vel, releasedEvent.t)
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
    new PlaneState(planeStateQuerier.planePosition(t), 
                   planeStateQuerier.planeVelocity(t), 
                   planeStateQuerier.planeAcceleration(t), 
                   planeStateQuerier.planeOrientation(t))
  
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