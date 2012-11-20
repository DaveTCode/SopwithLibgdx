package test.tyler.sopwith

import com.badlogic.gdx.utils.Clipboard
import com.badlogic.gdx.Application
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Audio
import com.badlogic.gdx.Input
import com.badlogic.gdx.Files
import com.badlogic.gdx.Net
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Application._

trait ApplicationTester extends Application {
  
  Gdx.app = this
  
  var logLevel = Application.LOG_INFO

  def getGraphics(): Graphics = { null }

  def getAudio(): Audio = { null }

  def getInput(): Input = { null }

  def getFiles(): Files = { null }

  def getNet(): Net = { null }

  @Override
  def debug(tag: String, message: String) {
    if (logLevel >= LOG_DEBUG) {
      System.out.println(tag + ": " + message)
    }
  }

  @Override
  def debug(tag: String, message: String, exception: Throwable) {
    if (logLevel >= LOG_DEBUG) {
      System.out.println(tag + ": " + message)
      exception.printStackTrace(System.out)
    }
  }

  @Override
  def log(tag: String, message: String) {
    if (logLevel >= LOG_INFO) {
      System.out.println(tag + ": " + message)
    }
  }

  @Override
  def log(tag: String, message: String, exception: Exception) {
    if (logLevel >= LOG_INFO) {
      System.out.println(tag + ": " + message)
      exception.printStackTrace(System.out)
    }
  }

  @Override
  def error(tag: String, message: String) {
    if (logLevel >= LOG_ERROR) {
      System.out.println(tag + ": " + message)
    }
  }

  @Override
  def error(tag: String, message: String, exception: Throwable) {
    if (logLevel >= LOG_ERROR) {
      System.out.println(tag + ": " + message)
      exception.printStackTrace(System.out)
    }
  }

  def setLogLevel(logLevel: Int) { Gdx.app.setLogLevel(logLevel) }

  def getType() = ApplicationType.Desktop

  def getVersion() = 0

  def getJavaHeap()= 0L

  def getNativeHeap() = 0L

  def getPreferences(name: String) = null

  def getClipboard() = null

  def postRunnable(runnable: Runnable) {}

  def exit() {}

}