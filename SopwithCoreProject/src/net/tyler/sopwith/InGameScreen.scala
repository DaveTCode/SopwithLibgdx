package net.tyler.sopwith

import com.badlogic.gdx.Screen
import net.tyler.math.ImmutableVector2f
import com.badlogic.gdx.utils.TimeUtils
import net.tyler.messaging.MessagingComponent
import net.tyler.messaging.Message
import net.tyler.messaging.MessagePassing

class InGameScreen extends Screen {

  private val inGameMessageTypes = List(classOf[PlaneVelocityChange],
                                        classOf[PlaneAngularVelocityChange],
                                        classOf[BombDestroyed],
                                        classOf[BombReleased],
                                        classOf[BuildingDestroyed])
  val messagePassing = new MessagePassing
  val messagingComponent = new MessagingComponent(messagePassing, inGameMessageTypes)
  val querier = new InGameStateQuerier(new PlaneState(new ImmutableVector2f(Configuration.GAME_WIDTH / 2f, Configuration.GAME_HEIGHT / 2f),
                                                      new ImmutableVector2f(0f, 0f),
                                                      0f, 0f, false),
                                       List(new Building(new ImmutableVector2f(Configuration.GAME_WIDTH / 2f, 0f))), 
                                       TimeUtils.millis,
                                       messagingComponent)
  val renderer = new InGameRenderer(querier)
  val inputProcessor = new InGameInputProcessor(querier, messagePassing)
  
  def render(dt: Float) {
    renderer.renderLevel
  }
  
  def show() = {messagePassing.send(new PlaneVelocityChange(new ImmutableVector2f(20f, -10f), TimeUtils.millis))}
  
  def hide() = {}
  
  def resize(width: Int, height: Int) = {}
  
  def pause() = {}
  
  def resume() = {}
  
  def dispose() = {} 
}