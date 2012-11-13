package net.tyler.sopwith;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class SopwithDesktop {

  public static void main(String[] args) {
    new LwjglApplication(new SopwithGame(), 
                         "Sopwith", 
                         Configuration.GAME_WIDTH(), 
                         Configuration.GAME_HEIGHT(),
                         true);
  }

}
