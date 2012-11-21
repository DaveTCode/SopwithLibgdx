package net.tyler.sopwith

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Peripheral
import net.tyler.messaging.MessagePassing
import com.badlogic.gdx.utils.TimeUtils

/**
 * Class is responsible for handling input device polling and converting the
 * results into state change messages to pass back to the game model.
 */
class InGameInputProcessor(private val querier: InGameStateQuerier, 
                           private val messagePassing: MessagePassing) {

  /**
   * Called once per render loop to process any new input and convert it into
   * game state messages.
   */
  def processInput {
    implicit val t = TimeUtils.millis
    
    /*
     * Control of the plane is either done via the accelerometer (when 
     * available) or via the keyboard when the accelerometer is not available.
     */
    if (Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)) {
      processAccelerometerInput
    } else {
      processKeyInput
    }
    
    /*
     * Touch/Mouse pointer use is used to release bombs
     */
    processTouchInput
  }

  /**
   * Touch based input (includes mouse pointer input) is used to interact with
   * the planes weapons.
   */
  private def processTouchInput(implicit t: Long) {
    if (Gdx.input.justTouched) {
      /*
       * @TODO - DAT - Should add in check to make sure that the touch is near
       * the plane. Blocked on collision code probably.
       */
      if (querier.bombsRemaining(t) > 0) {
        messagePassing.send(new BombReleased(querier.planeState(t).position, t))
      }
    }
  }
  
  private def processAccelerometerInput(implicit t: Long) {
    
  }
  
  private def processKeyInput(implicit t: Long) {
    
  }
}