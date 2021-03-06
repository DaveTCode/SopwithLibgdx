package net.tyler.sopwith.ingame

import com.badlogic.gdx.utils.TimeUtils

import net.tyler.math.CollisionDetection
import net.tyler.math.CartesianVectorConstants
import net.tyler.messaging.MessagingComponent
import net.tyler.sopwith.levels.Level
import net.tyler.sopwith.Configuration

class InGameObjectChecker(private val querier: InGameStateQuerier,
                          private val messagingComponent: MessagingComponent,
                          private val level: Level) {

  /**
   * Checks collisions between various objects and ensures that the game state
   * is updated to reflect these collisions.
   */
  def checkObjectLiveness {
    implicit val t = TimeUtils.millis
    
    checkAllBombFloorCollisions
  }
  
  def capVelocities {
    implicit val t = TimeUtils.millis
    
    capPlaneVelocity
  }
  
  /**
   * The plane has a maximum velocity. Once it hits that we want to prevent
   * it accelerating any faster.
   */
  private def capPlaneVelocity(implicit t: Long) {
    val plane = querier.planeState(t)
    
    if (plane.velocity.length > Configuration.MAX_PLANE_VELOCITY &&
        plane.acceleration != CartesianVectorConstants.zero) {
      messagingComponent.send(new PlaneAccelerationChange(CartesianVectorConstants.zero, t))
    }
  }
  
  /**
   * Bombs expire when they move below 0 on the y-axis (i.e. the floor 
   * baseline).
   */
  private def checkAllBombFloorCollisions(implicit t: Long) {
    querier.liveBombs(t).foreach((bomb: BombState) => {
      checkBombFloorCollisions(bomb)
    })
  }
  
  /**
   * Check a single bomb collision against the ground of the level or the
   * edges of the map in general (so that bombs don't disappear off into 
   * nowhere).
   */
  private def checkBombFloorCollisions(bomb: BombState)(implicit t: Long) {
    val lines = level.groundSegments ++ level.edges
    
    if (lines.exists({ case (p1, p2) => {
          CollisionDetection.circleLineOverlapping(bomb.position, Configuration.BOMB_RADIUS, p1, p2)
        }
      })) {
      messagingComponent.send(new BombDestroyed(bomb.releaseTime, t))
    } 
  }
}
