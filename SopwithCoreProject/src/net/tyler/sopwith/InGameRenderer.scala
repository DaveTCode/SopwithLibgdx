package net.tyler.sopwith

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.tyler.sopwith.TextureManager._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.TimeUtils

class InGameRenderer(val querier: InGameStateQuerier) {

  private val camera = new OrthographicCamera(Configuration.GAME_WIDTH, Configuration.GAME_HEIGHT)
  private val spriteBatch = new SpriteBatch(100)
  
  def renderLevel {
    val renderTime = TimeUtils.millis
    
    renderBackground
    
    renderBuildings(renderTime)
    
    renderPlane(renderTime)
    
    renderBombs(renderTime)
  }
  
  private def renderBackground {
    spriteBatch.disableBlending
    spriteBatch.begin
    
    spriteBatch.draw(LevelBackground.texture, 0f, 0f)
    
    spriteBatch.end
  }
  
  private def renderBuildings(t: Long) {
    val buildings = querier.buildings(t)
    
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
  
  private def renderPlane(t: Long) {
    val plane = querier.planeState(t)
    
    spriteBatch.enableBlending
    spriteBatch.begin
    
    spriteBatch.draw(PlaneTexture.texture, plane.position.x, plane.position.y)
    
    spriteBatch.end
  }
  
  private def renderBombs(t: Long) {
    val bombs = querier.liveBombs(t)
    
    spriteBatch.enableBlending
    spriteBatch.begin
    bombs.foreach((bomb: BombState) => {
      spriteBatch.draw(BombTexture.texture, bomb.position.x, bomb.position.y)
    })
    spriteBatch.end
  }
}