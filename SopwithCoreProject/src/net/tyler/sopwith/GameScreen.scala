package net.tyler.sopwith

import com.badlogic.gdx.Screen
import net.tyler.sopwith.ingame.InGameScreen
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import com.badlogic.gdx.utils.TimeUtils

abstract class GameScreen extends Screen {

  private val gameStateMessaging = new MessagingComponent(GameStateMessageTypes.types)
  private val splashScreen = new SplashScreen(gameStateMessaging)
  private val gameScreen = new InGameScreen(gameStateMessaging)
  val gameStateQuerier = new GameStateQuerier(gameStateMessaging)
  
  private def getScreen(t: Long) = gameStateQuerier.getState(TimeUtils.millis) match {
    case StateSplashScreen => splashScreen
    case StateInGameRunning => gameScreen
    case StateInGamePaused => gameScreen
  }
  
  override def render(delta: Float) {
    getScreen(TimeUtils.millis).render(delta)
  }
  
  override def show() = {
    getScreen(TimeUtils.millis).show()
  }
  
  override def hide() = {
    getScreen(TimeUtils.millis).hide()
  }
  
  override def resize(width: Int, height: Int) = {
    getScreen(TimeUtils.millis).resize(width, height)
  }
  
  override def pause() = {
    getScreen(TimeUtils.millis).pause()
  }
  
  override def resume() = {
    getScreen(TimeUtils.millis).resume()
  }
  
  override def dispose() = {
    getScreen(TimeUtils.millis).dispose()
  } 
}