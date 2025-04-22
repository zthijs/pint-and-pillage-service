package nl.duckstudios.pintandpillage.service;

import nl.duckstudios.pintandpillage.dao.UserDAO;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.model.UserHighscore;
import nl.duckstudios.pintandpillage.model.WorldVillage;
import nl.duckstudios.pintandpillage.entity.Coord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighscoreServiceTest {

    @Mock
    private UserDAO mockUserDAO;

    @Mock
    private VillageService mockVillageService;

    private HighscoreService highscoreService;

    private WorldVillage vw1;
    private WorldVillage vw2;
    private WorldVillage vw3;
    private User userAlice;
    private User userBob;

    @BeforeEach
    void setUp() {
        highscoreService = new HighscoreService(mockUserDAO, mockVillageService);

        vw1 = new WorldVillage(10L, "AliceVilleA", new Coord(1, 1), "Alpha", 1L, 100);
        vw2 = new WorldVillage(20L, "BobVille",    new Coord(2, 2), "Beta",  2L, 150);
        vw3 = new WorldVillage(30L, "AliceVilleB", new Coord(3, 3), "Gamma", 1L,  50);

        userAlice = new User();
        userAlice.setId(1L);
        userAlice.setUsername("Alice");

        userBob = new User();
        userBob.setId(2L);
        userBob.setUsername("Bob");
    }

    @Test
    void when_noVillages_expect_emptyList() {
        when(mockVillageService.getWorldVillages()).thenReturn(List.of());

        List<UserHighscore> result = highscoreService.getHighscore();

        assertTrue(result.isEmpty(), "Expected empty highscore list when there are no villages");
    }

    @Test
    void when_oneVillageAndKnownUser_expect_singleHighscoreEntry() {
        when(mockVillageService.getWorldVillages()).thenReturn(List.of(vw1));
        when(mockUserDAO.findUsernameById(1L)).thenReturn(Optional.of(userAlice));

        List<UserHighscore> result = highscoreService.getHighscore();

        assertEquals(1, result.size());
        UserHighscore hs = result.get(0);
        assertEquals("Alice", hs.username);
        assertEquals(100, hs.totalPoints);
    }

    @Test
    void when_multipleVillagesPerUser_expect_pointsAggregated() {
        when(mockVillageService.getWorldVillages()).thenReturn(List.of(vw1, vw2, vw3));
        when(mockUserDAO.findUsernameById(1L)).thenReturn(Optional.of(userAlice));
        when(mockUserDAO.findUsernameById(2L)).thenReturn(Optional.of(userBob));

        List<UserHighscore> result = highscoreService.getHighscore();

        assertEquals(2, result.size());

        UserHighscore aliceScore = result.get(0);
        assertEquals("Alice", aliceScore.username);
        assertEquals(150, aliceScore.totalPoints);

        UserHighscore bobScore = result.get(1);
        assertEquals("Bob", bobScore.username);
        assertEquals(150, bobScore.totalPoints);
    }

    @Test
    void when_unknownUser_expect_skipVillage() {
        when(mockVillageService.getWorldVillages()).thenReturn(List.of(vw1, vw2));
        when(mockUserDAO.findUsernameById(1L)).thenReturn(Optional.empty());
        when(mockUserDAO.findUsernameById(2L)).thenReturn(Optional.of(userBob));

        List<UserHighscore> result = highscoreService.getHighscore();

        assertEquals(1, result.size());
        UserHighscore only = result.get(0);
        assertEquals("Bob", only.username);
        assertEquals(150, only.totalPoints);
    }
}
