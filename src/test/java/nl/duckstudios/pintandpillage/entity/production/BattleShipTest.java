package nl.duckstudios.pintandpillage.entity.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BattleShipTest {

    private BattleShip battleShip;

    @BeforeEach
    void setUp() {
        battleShip = new BattleShip();
    }

    @Test
    void when_BattleShipCreated_then_BaseTimeToProduceIsSet() {
        LocalTime expectedBaseTimeToProduce = LocalTime.of(0, 0, 10);
        LocalTime actualBaseTimeToProduce = battleShip.getBaseTimeToProduce();

        assertEquals(expectedBaseTimeToProduce, actualBaseTimeToProduce);
    }

    @Test
    void when_BattleShipCreated_then_ResourcesRequiredToProduceAreSet() {
        Map<String, Integer> expectedResources = Map.of(
            "Wood", 500,
            "Stone", 200,
            "Beer", 100
        );
        Map<String, Integer> actualResources = battleShip.getResourcesRequiredToProduce();

        assertEquals(expectedResources, actualResources);
    }

    @Test
    void when_BattleShipCreated_then_AttributesAreSetCorrectly() {
        assertEquals(50, battleShip.getAttack());
        assertEquals(0, battleShip.getDefence());
        assertEquals(10, battleShip.getHealth());
        assertEquals(50, battleShip.getSpeed());
        assertEquals(500, battleShip.getPlunderAmount());
        assertEquals("A viking longship that can carry many raiders", battleShip.getDescription());
        assertEquals(3, battleShip.getPopulationRequiredPerUnit());
        assertEquals(50, battleShip.getShipCapacity());
    }
}