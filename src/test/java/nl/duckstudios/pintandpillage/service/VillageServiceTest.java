package nl.duckstudios.pintandpillage.service;

import nl.duckstudios.pintandpillage.Exceptions.SettleConditionsNotMetException;
import nl.duckstudios.pintandpillage.dao.VillageDataMapper;
import nl.duckstudios.pintandpillage.entity.Coord;
import nl.duckstudios.pintandpillage.entity.Village;
import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.entity.WorldMap;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VillageServiceTest {

    @Mock
    private Village mockVillage;

    @Mock
    private VillageDataMapper mockVillageDataMapper;

    @Mock
    private ResourceManager mockResourceManager;

    @Mock
    private WorldService mockWorldService;

    @Mock
    private DistanceService mockDistanceService;

    private VillageService villageService;
    private VillageUnit villageUnit;

    @BeforeEach
    void setUp() {
        this.villageService = new VillageService(mockVillageDataMapper, mockResourceManager, mockWorldService, mockDistanceService);
        this.villageUnit = new VillageUnit();
    }

    @Test
    void when_noJarlInVillage_expect_throwSettleConditionsNotMetException() {
        villageUnit.setAmount(0);
        when(mockVillage.getUnitInVillage(any())).thenReturn(villageUnit);
        String expectedMessage = "To create a new village you need a jarl";
        Coord testCoords = new Coord(5, 5);

        Exception exception = assertThrows(SettleConditionsNotMetException.class, () -> villageService.checkIsValidCreatingSpot(mockVillage, testCoords));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void when_distanceBetweenVillagesIsTooLarge_expect_throwSettleConditionsNotMetException() {
        villageUnit.setAmount(1);
        when(mockVillage.getUnitInVillage(any())).thenReturn(villageUnit);
        when(mockDistanceService.calculateDistance(any(), any())).thenReturn(6);
        String expectedMessage = "Too much distance between your village and the new village";
        Coord testCoords = new Coord(5, 5);

        Exception exception = assertThrows(SettleConditionsNotMetException.class, () -> villageService.checkIsValidCreatingSpot(mockVillage, testCoords));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void when_chosenSpotIsInvalid_expect_throwSettleConditionsNotMetException() {
        villageUnit.setAmount(1);
        when(mockVillage.getUnitInVillage(any())).thenReturn(villageUnit);
        when(mockDistanceService.calculateDistance(any(), any())).thenReturn(5);
        when(mockWorldService.getWorldMap()).thenReturn(new WorldMap(34843, 50, 50, 25));
        String expectedMessage = "Invalid build spot for a new village";
        Coord testCoords = new Coord(5, 5);

        Exception exception = assertThrows(SettleConditionsNotMetException.class, () -> villageService.checkIsValidCreatingSpot(mockVillage, testCoords));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void when_allConditionsAreMet_expect_noExceptionThrown() {
        villageUnit.setAmount(1);
        Coord testCoords = new Coord(5, 5);

        when(mockVillage.getUnitInVillage(any())).thenReturn(villageUnit);
        when(mockDistanceService.calculateDistance(any(), any())).thenReturn(4);

        WorldMap mockWorldMap = mock(WorldMap.class);
        when(mockWorldService.getWorldMap()).thenReturn(mockWorldMap);
        when(mockWorldMap.isValidToBuildNewVillage(testCoords)).thenReturn(true);

        assertDoesNotThrow(() -> villageService.checkIsValidCreatingSpot(mockVillage, testCoords));
    }
}