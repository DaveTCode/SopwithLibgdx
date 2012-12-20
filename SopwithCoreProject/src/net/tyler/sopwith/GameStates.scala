package net.tyler.sopwith

abstract class GameState {}

case object StateSplashScreen extends GameState
case object StateInGamePaused extends GameState
case object StateInGameRunning extends GameState
  