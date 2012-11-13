package net.tyler.sopwith

import net.tyler.messaging.StateQuerier

class InGameStateQuerier(planeState: PlaneState, buildings: List[Building]) extends StateQuerier {

  def liveBombs(t: Long): List[BombState] = {
    List()
  }
  
  def bombsRemaining(t: Long): Int = {
    5
  }
  
  def planeState(t: Long): PlaneState = {
    planeState
  }
  
  def buildings(t: Long): List[BuildingState] = {
    buildings.map((building: Building) => new BuildingState(building, true))
  }
}