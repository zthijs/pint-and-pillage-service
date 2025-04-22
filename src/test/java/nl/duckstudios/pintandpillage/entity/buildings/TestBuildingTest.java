package nl.duckstudios.pintandpillage.entity.buildings;

import nl.duckstudios.pintandpillage.Exceptions.BuildingConditionsNotMetException;
import nl.duckstudios.pintandpillage.entity.Village;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestBuildingTest {

    private TestBuilding building;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private Village village;

    @BeforeEach
    void setUp() {
        building = new TestBuilding();
        building.setResourceManager(resourceManager);
        building.setVillage(village);
    }

    @Test
    public void when_LevelUpCalled_Expect_SubtractResourcesOnce() {
        when(resourceManager.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);
        when(village.hasEnoughPopulation(building.getPopulationRequiredNextLevel())).thenReturn(true);
        this.building.levelUp();

        verify(resourceManager, times(1)).subtractResources(any(), any());
    }

    @Test
    public void when_LevelUpCalled_Expect_BuildingUnderConstructionTrue() {
        when(resourceManager.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);
        when(village.hasEnoughPopulation(building.getPopulationRequiredNextLevel())).thenReturn(true);
        boolean expectedIsUnderConstruction = true;

        building.setConstructionTimeSeconds(200);
        this.building.levelUp();
        boolean actualIsUnderConstruction = this.building.isUnderConstruction();

        assertEquals(expectedIsUnderConstruction, actualIsUnderConstruction);
    }

    @Test
    public void when_NotEnoughPopulation_Expect_ThrowBuildingConditionsNotMetException() {
        when(resourceManager.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);
        when(village.hasEnoughPopulation(building.getPopulationRequiredNextLevel())).thenReturn(false);
        String expectedExceptionMessage = "Not enough population available";

        Exception actualThrownException = assertThrows(BuildingConditionsNotMetException.class, () -> building.levelUp());
        String actualThrownExceptionMessage = actualThrownException.getMessage();

        assertEquals(expectedExceptionMessage, actualThrownExceptionMessage);
    }

    @Test
    public void when_NotEnoughResources_Expect_ThrowBuildingConditionsNotMetException() {
        when(resourceManager.hasEnoughResourcesAvailable(any(), any())).thenReturn(false);
        String expectedExceptionMessage = "Not enough resources available";

        Exception actualThrownException = assertThrows(BuildingConditionsNotMetException.class, () -> building.levelUp());
        String actualThrownExceptionMessage = actualThrownException.getMessage();

        assertEquals(expectedExceptionMessage, actualThrownExceptionMessage);
    }
}