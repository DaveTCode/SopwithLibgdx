package net.tyler.sopwith.ingame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.TimeUtils
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.sopwith.Configuration
import net.tyler.sopwith.levels.Level1
import net.tyler.messaging.MessagingComponent
import net.tyler.sopwith.GamePaused
import net.tyler.sopwith.GameResumed

class InGameScreen(gameStateMessaging: MessagingComponent) extends Screen {

  private val level = Level1
  
  private val messagingComponent = new MessagingComponent(InGameMessageTypes.types)
  private val querier = new InGameStateQuerier(level.plane,
                                               level.buildings,
                                               Configuration.INIT_BOMBS,
                                               TimeUtils.millis,
                                               messagingComponent)
  private val renderer = new InGameRenderer(querier, level)
  private val inputProcessor = new InGameInputProcessor(querier, messagingComponent)
  private val objectStateUpdater = new InGameObjectChecker(querier, messagingComponent, level) 
  
  override def render(dt: Float) {
    /*
     * Handle changes to the game state based on polling user input. Event based
     * input is handled internally by libgdx (i.e. all mouse/key events).
     */
    inputProcessor.processInput
    
    /*
     * Handle changes to the game state based on collisions.
     */
    objectStateUpdater.checkObjectLiveness
    
    /*
     * Various objects have maximum a velocity. To get around this we check
     * to see whether any object has exceeded it's maximum velocity each frame
     * and if so 0 the acceleration. 
     */
    objectStateUpdater.capVelocities
    
    /*
     * Render the level based on the current game state.
     */
    renderer.renderLevel
  }
  
  override def show() = {
    /*
     * Event based input processing goes through this. Polling is done for other
     * types and is done in the render/update loop.
     */
    Gdx.input.setInputProcessor(inputProcessor)
  }
  
  override def hide() {}
  
  override def resize(width: Int, height: Int) {}
  
  override def pause() {
    gameStateMessaging.send(new GamePaused(TimeUtils.millis))
  }
  
  override def resume() {
    gameStateMessaging.send(new GameResumed(TimeUtils.millis))
  }
  
  override def dispose() = {} 
}