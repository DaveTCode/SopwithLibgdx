package net.tyler.sopwith.levels

import net.tyler.sopwith.ingame.Building
import net.tyler.math.CartesianVector2f
import net.tyler.sopwith.ingame.PlaneState
import net.tyler.math.CartesianVectorConstants

object Level1 extends Level {
  override def width = 960
  override def height = 320
  
  override def ground = List((0, 320), (20, 10), (940, 10), (960, 320))
  
  override def buildings = List(new Building(new CartesianVector2f(30, 10)),
                                new Building(new CartesianVector2f(120, 10)),
                                new Building(new CartesianVector2f(420, 10)),
                                new Building(new CartesianVector2f(609, 10)))
                       
  override def plane = new PlaneState(new CartesianVector2f(width / 2, height / 2), 
                                      CartesianVectorConstants.zero, 
                                      CartesianVectorConstants.zero, false)
}