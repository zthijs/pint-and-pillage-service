package nl.duckstudios.pintandpillage.entity.buildings;

import nl.duckstudios.pintandpillage.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefenceTowerTest {

    private DefenceTower defenceTower;

    @BeforeEach
    void setUp() {
        this.defenceTower = new DefenceTower();
    }

    private void setTowerLevel(int level) {
        defenceTower.setLevel(level);
        defenceTower.updateBuilding();
    }

    @Test
    public void when_levelIsZero_expect_woodCostToBe301() {
        int woodCost = defenceTower.getResourcesRequiredLevelUp().get(ResourceType.Wood.name());
        assertEquals(301, woodCost);
    }

    @Test
    public void when_levelIsFive_expect_woodCostToBe350() {
        setTowerLevel(5);
        int woodCost = defenceTower.getResourcesRequiredLevelUp().get(ResourceType.Wood.name());
        assertEquals(350, woodCost);
    }

    @Test
    public void when_levelIsZero_expect_stoneCostToBe301() {
        int stoneCost = defenceTower.getResourcesRequiredLevelUp().get(ResourceType.Stone.name());
        assertEquals(301, stoneCost);
    }

    @Test
    public void when_levelIsFive_expect_stoneCostToBe425() {
        setTowerLevel(5);
        int stoneCost = defenceTower.getResourcesRequiredLevelUp().get(ResourceType.Stone.name());
        assertEquals(425, stoneCost);
    }

    @Test
    public void when_levelIsZero_expect_beerCostToBe101() {
        int beerCost = defenceTower.getResourcesRequiredLevelUp().get(ResourceType.Beer.name());
        assertEquals(101, beerCost);
    }

    @Test
    public void when_levelIsFive_expect_beerCostToBe125() {
        setTowerLevel(5);
        int beerCost = defenceTower.getResourcesRequiredLevelUp().get(ResourceType.Beer.name());
        assertEquals(125, beerCost);
    }
}