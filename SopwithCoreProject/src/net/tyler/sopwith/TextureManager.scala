package net.tyler.sopwith

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

object TextureManager {

  case object LevelBackground extends InternalTexture("images/level_background.png")
  case object BombTexture extends InternalTexture("images/bomb.png")
  case object PlaneTexture extends InternalTexture("images/biplane.png")
  case object BuildingLiveTexture extends InternalTexture("images/building_live.png")
  case object BuildingDestroyedTexture extends InternalTexture("images/building_destroyed.png")
  
  protected abstract class InternalTexture(filename: String) {
    
    val texture = loadTextureFromFile(filename)
    
    def dispose { texture.dispose }
  }
  
  private def loadTextureFromFile(filename: String): Texture = {
    val texture = new Texture(Gdx.files.internal(filename))
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
        
    texture
  }
  
  /**
   * Each individual texture that is loaded creates some opengl resources
   * which java does not manage. We need to dispose of those properly by
   * calling this dispose method when the texture manager is no longer
   * required.
   */
  def dispose {
    LevelBackground.dispose
    BombTexture.dispose
    PlaneTexture.dispose
    BuildingLiveTexture.dispose
    BuildingDestroyedTexture.dispose
  }
}