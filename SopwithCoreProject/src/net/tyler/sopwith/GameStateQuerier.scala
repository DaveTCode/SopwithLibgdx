package net.tyler.sopwith

import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.StateQuerier

class GameStateQuerier(messagingComponent: MessagingComponent) extends StateQuerier(messagingComponent) {

  private def allEventsBackwards(t: Long) = eventsPreTickVal(t).sortBy(_.ticks * -1)
  
  def getState(t: Long): GameState = {
    val appStartEvents = messageEvents[ApplicationStart](t)
    if (appStartEvents.size > 0) {
      allEventsBackwards(t) foreach { 
        case _: SplashScreenStart => StateSplashScreen()
        case _: 
      }
    }
    
    StateStarting()
  }
}