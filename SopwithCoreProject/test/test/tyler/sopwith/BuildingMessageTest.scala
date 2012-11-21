package test.tyler.sopwith

import org.junit.Test
import org.junit.Assert._
import net.tyler.math.ImmutableVector2f
import net.tyler.messaging.MessagePassing
import net.tyler.messaging.MessagingComponent
import net.tyler.sopwith.BombDestroyed
import net.tyler.sopwith.BombReleased
import net.tyler.sopwith.BuildingDestroyed
import net.tyler.sopwith.InGameStateQuerier
import net.tyler.sopwith.PlaneAngularVelocityChange
import net.tyler.sopwith.PlaneState
import net.tyler.sopwith.PlaneVelocityChange
import net.tyler.sopwith.Building
import net.tyler.sopwith.BuildingDestroyed

class BuildingMessageTest {
  
  trait StateTester {
    private val inGameMessageTypes = List(classOf[PlaneVelocityChange],
                                          classOf[PlaneAngularVelocityChange],
                                          classOf[BombDestroyed],
                                          classOf[BombReleased],
                                          classOf[BuildingDestroyed])
    val messagePassing = new MessagePassing
    val messagingComponent = new MessagingComponent(messagePassing, inGameMessageTypes)
    
    val building1 = new Building(new ImmutableVector2f(100,0))
    val building2 = new Building(new ImmutableVector2f(50,-100))
    val building3 = new Building(new ImmutableVector2f(1,-23))
    
    val querier = new InGameStateQuerier(null, List(building1, building2, building3), 5, 0, messagingComponent)
  }
  
  @Test def testBuildingCreation {
    new ApplicationTester with StateTester {
      assertEquals(querier.buildings(100).size, 3)
      assertEquals(querier.buildings(100).head.building, building1)
      assertEquals(querier.buildings(100).tail.head.building, building2)
      assertEquals(querier.buildings(100).tail.tail.head.building, building3)
    }
  }
  
  @Test def testBuildingDestruction {
    new ApplicationTester with StateTester {
      messagePassing.send(new BuildingDestroyed(building1, 110))
      messagePassing.send(new BuildingDestroyed(building2, 210))
      messagePassing.send(new BuildingDestroyed(building3, 310))
      
      val buildingsAt100 = querier.buildings(100)
      assertEquals(buildingsAt100.size, 3)
      
      val buildingsAt200 = querier.buildings(200)
      assertFalse(buildingsAt200.head.isLive)
      assertTrue(buildingsAt200.tail.head.isLive)
      assertTrue(buildingsAt200.tail.tail.head.isLive)
      
      val buildingsAt300 = querier.buildings(300)
      assertFalse(buildingsAt300.head.isLive)
      assertFalse(buildingsAt300.tail.head.isLive)
      assertTrue(buildingsAt300.tail.tail.head.isLive)
      
      val buildingsAt400 = querier.buildings(400)
      assertFalse(buildingsAt400.head.isLive)
      assertFalse(buildingsAt400.tail.head.isLive)
      assertFalse(buildingsAt400.tail.tail.head.isLive)
    }
  }
}