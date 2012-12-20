package net.tyler.sopwith

import com.badlogic.gdx.Game

/**
 * The entrance point for the application. This is what is loaded by the 
 * android and desktop projects.
 */
class SopwithGame extends Game {

  def create() {
    setScreen(new GameScreen)
  }
}