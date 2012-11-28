package net.tyler.sopwith

import com.badlogic.gdx.utils.TimeUtils
import net.tyler.messaging.MessagePassing
import net.tyler.sopwith.levels.Level
import net.tyler.math.CollisionDetection

class InGameObjectChecker(private val querier: InGameStateQuerier,
                          private val messagePassing: MessagePassing,
                          private val level: Level) {

  /**
   * Checks collisions between various objects and ensures that the game state
   * is updated to reflect these collisions.
   */
  def checkObjectLiveness {
    implicit val t = TimeUtils.millis
    
    checkAllBombFloorCollisions
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
      messagePassing.send(new BombDestroyed(bomb.releasePosition, t))
    } 
  }
}