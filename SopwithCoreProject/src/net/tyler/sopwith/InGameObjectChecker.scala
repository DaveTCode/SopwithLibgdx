package net.tyler.sopwith

import com.badlogic.gdx.utils.TimeUtils

import net.tyler.messaging.MessagePassing

class InGameObjectChecker(private val querier: InGameStateQuerier,
                          private val messagePassing: MessagePassing) {

  /**
   * Checks collisions between various objects and ensures that the game state
   * is updated to reflect these collisions.
   */
  def checkObjectLiveness {
    implicit val t = TimeUtils.millis
    
    checkBombFloorCollisions
  }
  
  /**
   * Bombs expire when they move below 0 on the y-axis (i.e. the floor 
   * baseline).
   */
  private def checkBombFloorCollisions(implicit t: Long) {
    querier.liveBombs(t).foreach((bomb: BombState) => {
      if (bomb.position.y < 0) {
        messagePassing.send(new BombDestroyed(bomb.releasePosition, t))
      }
    })
  }
}