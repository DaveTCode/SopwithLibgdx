package net.tyler.sopwith

abstract class GameState {}

case class StateStarting() extends GameState()
case class StateSplashScreen() extends GameState()
case class StateInGamePaused() extends GameState()
case class StateInGameRunning() extends GameState()
  