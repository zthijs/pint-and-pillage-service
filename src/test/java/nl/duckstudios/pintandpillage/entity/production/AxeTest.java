package nl.duckstudios.pintandpillage.entity.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AxeTest {

    private Axe axe;

    @BeforeEach
    void setUp() {
        axe = new Axe();
    }

    @Test
    void when_AxeCreated_then_BaseTimeToProduceIsSet() {
        LocalTime expectedBaseTimeToProduce = LocalTime.of(0, 0, 10);
        LocalTime actualBaseTimeToProduce = axe.getBaseTimeToProduce();

        assertEquals(expectedBaseTimeToProduce, actualBaseTimeToProduce);
    }

    @Test
    void when_AxeCreated_then_ResourcesRequiredToProduceAreSet() {
        Map<String, Integer> expectedResources = Map.of(
            "Wood", 15,
            "Beer", 15
        );
        Map<String, Integer> actualResources = axe.getResourcesRequiredToProduce();

        assertEquals(expectedResources, actualResources);
    }

    @Test
    void when_AxeCreated_then_AttributesAreSetCorrectly() {
        assertEquals(15, axe.getAttack());
        assertEquals(15, axe.getDefence());
        assertEquals(20, axe.getHealth());
        assertEquals(10, axe.getSpeed());
        assertEquals(0, axe.getPlunderAmount());
        assertEquals("War hardened vikings wielding a axe", axe.getDescription());
        assertEquals(1, axe.getPopulationRequiredPerUnit());
    }
}