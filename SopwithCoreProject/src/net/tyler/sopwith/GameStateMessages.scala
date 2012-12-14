package net.tyler.sopwith

import net.tyler.messaging.Message

/**
 * This file contains a list of all case classes that correspond to game 
 * events. 
 * 
 * We use these to determine what the current overall state of the game is.
 * 
 * e.g. What screen should be showing?   
 */
case class ApplicationStart(t: Long) extends Message(t)
case class SplashScreenStart(t: Long) extends Message(t)
case class SplashScreenExpired(t: Long) extends Message(t)
case class GameStarted(t: Long) extends Message(t)
case class GamePaused(t: Long) extends Message(t)
case class GameResumed(t: Long) extends Message(t)
case class PlayerLost(val score: Int, t: Long) extends Message(t)
case class PlayerWon(val score: Int, t: Long) extends Message(t)

object GameStateMessageTypes {
  val types = List(classOf[ApplicationStart],
                   classOf[SplashScreenStart],
                   classOf[SplashScreenExpired],
                   classOf[GameStarted],
                   classOf[GamePaused],
                   classOf[GameResumed],
                   classOf[PlayerLost],
                   classOf[PlayerWon])
}