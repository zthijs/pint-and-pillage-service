package nl.duckstudios.pintandpillage.controller;

import nl.duckstudios.pintandpillage.Exceptions.SettleConditionsNotMetException;
import nl.duckstudios.pintandpillage.entity.Coord;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.entity.Village;
import nl.duckstudios.pintandpillage.entity.production.Jarl;
import nl.duckstudios.pintandpillage.helper.VillageFactory;
import nl.duckstudios.pintandpillage.model.NewVillageData;
import nl.duckstudios.pintandpillage.model.UnitType;
import nl.duckstudios.pintandpillage.model.VillageNameChangeData;
import nl.duckstudios.pintandpillage.service.AccountService;
import nl.duckstudios.pintandpillage.service.AuthenticationService;
import nl.duckstudios.pintandpillage.service.VillageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VillageControllerTest {

    @Mock private VillageFactory villageFactory;
    @Mock private AuthenticationService authenticationService;
    @Mock private AccountService accountService;
    @Mock private VillageService villageService;

    @InjectMocks
    @Spy
    private VillageController villageController;

    private User testUser;
    private Village testVillage;

    @BeforeEach
    void setUp() {
        this.testUser = new User();
        this.testUser.setId(42L);
        this.testVillage = new Village();
        this.testVillage.setVillageId(100L);
        this.testVillage.setName("Testburg");
        this.testVillage.setUser(testUser);
    }

    @Test
    void when_getVillage_then_returnVillage() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getVillage(testVillage.getVillageId())).thenReturn(testVillage);

        Village result = villageController.getVillage(testVillage.getVillageId());

        verify(accountService).checkIsCorrectUser(testUser.getId(), testVillage);
        assertSame(testVillage, result);
    }

    @Test
    void when_getVillagesFromUser_withExistingVillages_then_returnList() {
        List<Village> villages = List.of(testVillage);
        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getListOfVillagesFromUser(testUser.getId())).thenReturn(villages);

        List<Village> result = villageController.getVillagesFromUser();

        verify(villageController, never()).createVillage();
        assertEquals(1, result.size());
        assertEquals(testVillage, result.get(0));
    }

    @Test
    void when_getVillagesFromUser_withNoVillages_then_createVillageAndReturnNewList() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getListOfVillagesFromUser(testUser.getId()))
                .thenReturn(Collections.emptyList())
                .thenReturn(List.of(testVillage));

        doReturn(testVillage).when(villageController).createVillage();

        List<Village> result = villageController.getVillagesFromUser();

        verify(villageController).createVillage();
        assertEquals(1, result.size());
        assertEquals(testVillage, result.get(0));
    }

    @Test
    void when_changeVillageName_then_updateAndReturnVillage() {
        String newName = "Newburg";
        VillageNameChangeData data = new VillageNameChangeData();
        data.newName = newName;

        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getVillage(testVillage.getVillageId())).thenReturn(testVillage);

        Village result = villageController.changeVillageName(testVillage.getVillageId(), data);

        verify(accountService).checkIsCorrectUser(testUser.getId(), testVillage);
        verify(villageService).update(testVillage);
        assertEquals(newName, result.getName());
    }

    @Test
    void when_startNewVillage_withValidJarls_then_createAndReturn() {
        NewVillageData data = new NewVillageData();
        data.villageId = testVillage.getVillageId();
        data.newPosition = new Coord(8, 9);

        testVillage.addUnit(new Jarl(), 2);

        Village newVillage = new Village();
        newVillage.setVillageId(200L);
        newVillage.setUser(testUser);

        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getVillage(testVillage.getVillageId())).thenReturn(testVillage);

        doNothing().when(villageService).checkIsValidCreatingSpot(testVillage, data.newPosition);
        when(villageService.getListOfVillagesFromUser(testUser.getId()))
                .thenReturn(List.of(testVillage));
        when(villageFactory.createBasicVillage(testUser, data.newPosition))
                .thenReturn(newVillage);

        Village result = villageController.startNewVillage(data);

        assertEquals(1, testVillage.getUnitInVillage(UnitType.Jarl).getAmount());
        verify(villageService).update(newVillage);
        assertSame(newVillage, result);
    }

    @Test
    void when_startNewVillage_withoutJarls_then_throw() {
        NewVillageData data = new NewVillageData();
        data.villageId = testVillage.getVillageId();
        data.newPosition = new Coord(8, 9);

        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getVillage(testVillage.getVillageId())).thenReturn(testVillage);

        assertThrows(SettleConditionsNotMetException.class,
                () -> villageController.startNewVillage(data));
    }

    @Test
    void when_startNewVillage_invalidSpot_then_bubbleUp() {
        NewVillageData data = new NewVillageData();
        data.villageId = testVillage.getVillageId();
        data.newPosition = new Coord(8, 9);

        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
        when(villageService.getVillage(testVillage.getVillageId())).thenReturn(testVillage);
        doThrow(new SettleConditionsNotMetException("bad spot"))
                .when(villageService).checkIsValidCreatingSpot(testVillage, data.newPosition);

        assertThrows(SettleConditionsNotMetException.class,
                () -> villageController.startNewVillage(data));
    }
}
