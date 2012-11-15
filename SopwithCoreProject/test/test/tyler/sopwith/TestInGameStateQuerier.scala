package test.tyler.sopwith

import net.tyler.sopwith.InGameStateQuerier
import net.tyler.sopwith.PlaneState
import net.tyler.sopwith.Building
import net.tyler.messaging.Message

/**
 * Test harness for the in game state querier to allow inserting messages
 * directly into the queue.
 */
class TestInGameStateQuerier(planeState: PlaneState, buildings: List[Building], startTime: Long) 
  extends InGameStateQuerier(planeState, buildings, startTime) {

  def addMessage(message: Message) { Buffer += message }
}