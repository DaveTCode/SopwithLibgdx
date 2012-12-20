package net.tyler.sopwith

import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.StateQuerier

class GameStateQuerier(messagingComponent: MessagingComponent) extends StateQuerier(messagingComponent) {

  private def allEventsBackwards(t: Long) = eventsPreTickVal(t).sortBy(_.ticks * -1)
  
  def getState(t: Long): GameState = {
    val appStartEvents = messageEvents[ApplicationStart](t)
    if (appStartEvents.size > 0) {
      val gameStateEvents = allEventsBackwards(t) collect  { 
        case m if (m.getClass == SplashScreenStart || 
                   m.getClass == GameStarted || 
                   m.getClass == GamePaused || 
                   m.getClass == GameResumed) => m
      } 
      
      gameStateEvents.last match {
        case _: SplashScreenStart => StateSplashScreen
        case _: GameStarted => StateInGameRunning
        case _: GamePaused => StateInGamePaused
        case _: GameResumed => StateInGameRunning
      }
    }
    
    StateSplashScreen
  }
}