package net.tyler.sopwith

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.tyler.sopwith.TextureManager._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.graphics.g2d.TextureRegion
import net.tyler.sopwith.levels.Level

class InGameRenderer(private val querier: InGameStateQuerier,
                     private val level: Level) {

  private val camera = new OrthographicCamera(Configuration.GAME_WIDTH, Configuration.GAME_HEIGHT)
  private val spriteBatch = new SpriteBatch(100)
  
  camera.setToOrtho(false, Configuration.GAME_WIDTH, Configuration.GAME_HEIGHT)
  
  def renderLevel {
    implicit val renderTime = TimeUtils.millis
    
    /*
     * Centre the camera onto the plane and then update the renderers.
     */
    centreCamera
    camera.update
    spriteBatch.setProjectionMatrix(camera.combined)
    
    renderBackground
    
    renderBuildings
    
    renderPlane
    
    renderBombs
  }
  
  private def centreCamera(implicit renderTime: Long) {
    val plane = querier.planeState(renderTime)
    camera.translate(plane.position.x - camera.position.x, plane.position.y - camera.position.y)
  }
  
  /**
   * Responsible for drawing the background onto the screen. Note that this should be done
   * before drawing any of the rest of the game.
   */
  private def renderBackground(implicit renderTime: Long) {
    spriteBatch.disableBlending
    spriteBatch.begin
    
    spriteBatch.draw(LevelBackground.texture, 0f, 0f)
    
    spriteBatch.end
  }
  
  /**
   * Responsible for drawing each of the buildings.
   */
  private def renderBuildings(implicit renderTime: Long) {
    val buildings = querier.buildings(renderTime)
    
    spriteBatch.enableBlending
    spriteBatch.begin
    
    buildings.foreach((buildingState: BuildingState) => {
      val texture = if (buildingState.isLive) BuildingLiveTexture.texture else BuildingDestroyedTexture.texture  
      
      spriteBatch.draw(texture, 
                       buildingState.position.x, 
                       buildingState.position.y)
    })
    
    spriteBatch.end
  }
  
  /**
   * Responsible for rendering the image of the plane at the correct location 
   * and with the correct rotation.
   */
  private def renderPlane(implicit renderTime: Long) {
    val plane = querier.planeState(renderTime)
    
    spriteBatch.enableBlending
    spriteBatch.begin
    
    spriteBatch.draw(new TextureRegion(PlaneTexture.texture),                                  // Texture region
                     plane.position.x, plane.position.y,                                       // Position
                     PlaneTexture.texture.getWidth / -2f, PlaneTexture.texture.getHeight / -2f,// Origin
                     PlaneTexture.texture.getWidth, PlaneTexture.texture.getHeight,            // Width, Height
                     1f, 1f,                                                                   // Scaling factor
                     plane.angle)                                                              // Rotation angle
    
    spriteBatch.end
  }
  
  /**
   * Responsible for rendering all of the currently live bombs. This may result 
   * in nothing.
   */
  private def renderBombs(implicit renderTime: Long) {
    val bombs = querier.liveBombs(renderTime)
    
    spriteBatch.enableBlending
    spriteBatch.begin
    bombs.foreach((bomb: BombState) => {
      spriteBatch.draw(BombTexture.texture, bomb.position.x, bomb.position.y)
    })
    spriteBatch.end
  }
}