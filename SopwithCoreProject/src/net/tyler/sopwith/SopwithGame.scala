package net.tyler.sopwith

import com.badlogic.gdx.Game
import net.tyler.sopwith.splash.SplashScreen

/**
 * The entrance point for the application. This is what is loaded by the 
 * android and desktop projects.
 */
class SopwithGame extends Game {

  def create() {
    setScreen(new SplashScreen(this))
  }
}