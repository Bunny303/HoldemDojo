package com.nedogeek.holdem.gamingStuff;

/*-
 * #%L
 * Holdem dojo project is a server-side java application for playing holdem pocker in DOJO style.
 * %%
 * Copyright (C) 2016 Holdemdojo
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.nedogeek.holdem.GameSettings;
import com.nedogeek.holdem.PlayerStatus;
import com.nedogeek.holdem.combinations.PlayerCardCombination;
import com.nedogeek.holdem.dealer.EventManager;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: Konstantin Demishev
 * Date: 22.11.12
 * Time: 1:55
 */
public class PlayersListTest {
    private final String FIRST_PLAYER = "First player";
    private final String SECOND_PLAYER = "Second player";

    private final String FIRST_PLAYER_CARD_COMBINATION_STRING = "First player card combination";
    private final String SECOND_PLAYER_CARD_COMBINATION_STRING = "Second player card combination";

    private final String FIRST_PLAYER_JSON = "First player JSON";
    private final String SECOND_PLAYER_JSON = "Second player JSON";

    private final String FIRST_PLAYER_JSON_WITH_CARDS = "First player JSON with cards";
    private final String SECOND_PLAYER_JSON_WITH_CARDS = "Second player JSON with cards";
    private final String NOT_PRESENT_PLAYER = "Not presented player";

    private PlayersList playersList;

    private Player firstPlayer = mock(Player.class);
    private Player secondPlayer = mock(Player.class);
    private final EventManager eventManagerMock = mock(EventManager.class);

    @Before
    public void setUp() throws Exception {

        resetPlayers();

        resetPlayerList();
        setDefaultTwoPlayersGame();
    }

    private void resetPlayers() {
        firstPlayer = mock(Player.class);
        secondPlayer = mock(Player.class);

        when(firstPlayer.getName()).thenReturn(FIRST_PLAYER);
        when(secondPlayer.getName()).thenReturn(SECOND_PLAYER);

        when(firstPlayer.getStatus()).thenReturn(PlayerStatus.NotMoved);
        when(secondPlayer.getStatus()).thenReturn(PlayerStatus.NotMoved);

        setPlayersCardCombinations();
    }

    private void setPlayersCardCombinations() {
        PlayerCardCombination firstPlayerCardCombinationMock = mock(PlayerCardCombination.class);
        PlayerCardCombination secondPlayerCardCombinationMock = mock(PlayerCardCombination.class);

        when(firstPlayer.getCardCombination()).thenReturn(firstPlayerCardCombinationMock);
        when(secondPlayer.getCardCombination()).thenReturn(secondPlayerCardCombinationMock);

        when(firstPlayerCardCombinationMock.toString()).thenReturn(FIRST_PLAYER_CARD_COMBINATION_STRING);
        when(secondPlayerCardCombinationMock.toString()).thenReturn(SECOND_PLAYER_CARD_COMBINATION_STRING);

        when(firstPlayer.toJSON()).thenReturn(FIRST_PLAYER_JSON);
        when(secondPlayer.toJSON()).thenReturn(SECOND_PLAYER_JSON);

        when(firstPlayer.toJSONWithCards()).thenReturn(FIRST_PLAYER_JSON_WITH_CARDS);
        when(secondPlayer.toJSONWithCards()).thenReturn(SECOND_PLAYER_JSON_WITH_CARDS);
    }

    private void setDefaultTwoPlayersGame() {
        playersList.add(firstPlayer);
        playersList.add(secondPlayer);
        playersList.setNewGame();
    }

    private void setPlayerBet(Player player, int bet) {
        when(player.getBet()).thenReturn(bet);
    }

    @Test
    public void shouldFalseWhenDefaultDeskSecondPlayerFold() throws Exception {
        when(secondPlayer.getStatus()).thenReturn(PlayerStatus.Fold);

        assertFalse(playersList.hasAvailableMovers());
    }

    @Test
    public void shouldFalseWhenDefaultDeskSecondPlayerLost() throws Exception {
        playersList.playerMoved(secondPlayer);
        when(secondPlayer.getStatus()).thenReturn(PlayerStatus.Fold);

        assertFalse(playersList.hasAvailableMovers());
    }

    @Test
    public void should0PlayersWhenNewPlayersManager() throws Exception {
        assertEquals(0, new PlayersList(eventManagerMock).size());
    }

    @Test
    public void should1PlayerWhenNewPlayerAddedToNewPlayersManager() throws Exception {
        resetPlayerList();
        playersList.add(firstPlayer);
        playersList.setNewGame();

        assertEquals(1, playersList.size());
    }

    @Test
    public void should2PlayersWhen2NewPlayerAddedToNewPlayersManager() throws Exception {
        resetPlayerList();
        playersList.add(firstPlayer);
        playersList.add(secondPlayer);
        playersList.setNewGame();

        assertEquals(2, playersList.size());
    }

    private void resetPlayerList() {
        playersList = new PlayersList(eventManagerMock);
    }

    @Test
    public void should1PlayerWhenSameNewPlayerAddedToNewPlayersManagerTwice() throws Exception {
        resetPlayerList();
        playersList.add(firstPlayer);
        playersList.add(firstPlayer);
        playersList.setNewGame();

        assertEquals(1, playersList.size());
    }

    @Test
    public void should2PlayersListWhenPlayersManagerGetPlayers() throws Exception {
        assertEquals(2, playersList.size());
    }

    @Test
    public void shouldFirstPlayerWhenSmallBlindPlayer() throws Exception {
        assertEquals(firstPlayer, playersList.smallBlindPlayer());
    }

    @Test
    public void shouldSecondPlayerWhenBigBlindPlayer() throws Exception {
        assertEquals(secondPlayer, playersList.bigBlindPlayer());
    }

    @Test
    public void shouldThirdPlayerWhenAddThirdPlayerAndGetBigBlindPlayer() throws Exception {
        Player thirdPlayer = mock(Player.class);
        playersList.add(thirdPlayer);
        playersList.setNewGame();

        assertEquals(thirdPlayer, playersList.bigBlindPlayer());
    }

    @Test
    public void shouldNotHasAvailableMoversWhenBothPlayersAreNotActiveNotRisingPlayers() throws Exception {
        when(firstPlayer.isActiveNotRisePlayer()).thenReturn(false);
        when(secondPlayer.isActiveNotRisePlayer()).thenReturn(false);

        assertFalse(playersList.hasAvailableMovers());
    }

    @Test
    public void shouldChangeDealerWhenSetNewGame() throws Exception {
        playersList.setNewGame();

        assertEquals(0, playersList.getDealerNumber());
    }

    @Test
    public void shouldAllPlayersSetNotMovedStatusesWhenPlayersAdded() throws Exception {
        verify(firstPlayer).setStatus(PlayerStatus.NotMoved);
        verify(secondPlayer).setStatus(PlayerStatus.NotMoved);
    }

    @Test
    public void shouldHasAvailableMoversWhenFirstPlayerIsAllInAndSecondIsActive() throws Exception {
        when(firstPlayer.getStatus()).thenReturn(PlayerStatus.AllIn);

        when(secondPlayer.isActiveNotRisePlayer()).thenReturn(true);

        assertTrue(playersList.hasAvailableMovers());
    }

    @Test
    public void shouldNotHasAvailableMoversWhenFirstPlayerIsFoldAndSecondIsActive() throws Exception {
        when(firstPlayer.getStatus()).thenReturn(PlayerStatus.Fold);

        when(secondPlayer.isActiveNotRisePlayer()).thenReturn(true);

        assertFalse(playersList.hasAvailableMovers());
    }

    @Test
    public void should2PlayersWhenFirstAddedManagedAndAddedAgainSecondTime() throws Exception {
        resetPlayerList();

        playersList.add(firstPlayer);
        playersList.setNewGame();
        playersList.add(firstPlayer);
        playersList.setNewGame();

        assertEquals(1, playersList.size());
    }

    @Test
    public void shouldPlayerAddedWhenAddPlayerAndSetNewGame() throws Exception {
        resetPlayerList();

        playersList.add(firstPlayer);
        playersList.setNewGame();

        assertEquals(1, playersList.size());
    }

    @Test
    public void should2PlayersWhenAddThirdPlayerWithoutSetNewGame() throws Exception {
        playersList.add(mock(Player.class));

        assertEquals(2, playersList.size());
    }

    @Test
    public void shouldFirstPlayerAddedImmediatelyWhenPlayersListSize0() throws Exception {
        resetPlayerList();

        playersList.add(firstPlayer);

        assertEquals(1, playersList.size());
    }

    @Test
    public void shouldSecondPlayerAddedImmediatelyWhenPlayersListSize1() throws Exception {
        resetPlayerList();

        playersList.add(firstPlayer);
        playersList.add(secondPlayer);

        assertEquals(2, playersList.size());
    }

    @Test
    public void shouldPot0WhenDefault2PlayersGame() throws Exception {
        assertEquals(0, playersList.getPot());
    }

    @Test
    public void shouldPot100WhenFirstPlayerBet100() throws Exception {
        setPlayerBet(firstPlayer, 100);

        assertEquals(100, playersList.getPot());
    }

    @Test
    public void shouldPot200WhenFirstPlayerBet100AndSecondPlayerBet100() throws Exception {
        setPlayerBet(firstPlayer, 100);
        setPlayerBet(secondPlayer, 100);

        assertEquals(200, playersList.getPot());
    }

    @Test
    public void shouldSecondPlayerIsDealerWhenDefaultGame() throws Exception {

        assertEquals(SECOND_PLAYER, playersList.getDealerName());
    }

    @Test
    public void shouldFirstPlayerIsDealerWhenDefaultGameSetNewGame() throws Exception {
        playersList.setNewGame();

        assertEquals(FIRST_PLAYER, playersList.getDealerName());
    }

    @Test
    public void shouldFirstPlayerIsMoverWhenFirstPlayerIsActive() throws Exception {
        when(firstPlayer.isActiveNotRisePlayer()).thenReturn(true);

        assertEquals(FIRST_PLAYER, playersList.getMoverName());
    }

    @Test
    public void shouldSecondPlayerIsMoverWhenSecondPlayerIsActiveAndFirstPlayerMoved() throws Exception {
        when(secondPlayer.isActiveNotRisePlayer()).thenReturn(true);
        playersList.playerMoved(firstPlayer);

        assertEquals(SECOND_PLAYER, playersList.getMoverName());
    }

    @Test
    public void shouldEmptyStringWhenDefault() throws Exception {
        assertEquals("", playersList.getMoverName());
    }

    @Test
    public void shouldFirstPlayerCardCombinationWhenGetFirstPlayerCardCombination() throws Exception {
        assertEquals(FIRST_PLAYER_CARD_COMBINATION_STRING, playersList.getPlayerCardCombination(FIRST_PLAYER));
    }

    @Test
    public void shouldSecondPlayerCardCombinationWhenGetSecondPlayerCardCombination() throws Exception {
        assertEquals(SECOND_PLAYER_CARD_COMBINATION_STRING, playersList.getPlayerCardCombination(SECOND_PLAYER));
    }

    @Test
    public void shouldEmptyStringWhenGetCardCombinationOgNotPresentedPlayer() throws Exception {
        assertEquals("", playersList.getPlayerCardCombination(NOT_PRESENT_PLAYER));
    }

    @Test
    public void shouldTwoPlayersJSONWhenGeneratePlayersJSON() throws Exception {
        assertEquals("[" + FIRST_PLAYER_JSON + ", " + SECOND_PLAYER_JSON + "]",
                playersList.generatePlayersJSON());
    }

    @Test
    public void shouldFirstPlayerJSONSecondPlayerJSONWithCardsWhenGeneratePlayersJSONSecondPlayer() throws Exception {
        assertEquals("[" + FIRST_PLAYER_JSON + ", " + SECOND_PLAYER_JSON_WITH_CARDS + "]",
                playersList.generatePlayersJSON(SECOND_PLAYER));
    }

    @Test
    public void shouldTwoPlayersJSONWhenGeneratePlayersJSONWithNotPresentPlayer() throws Exception {
        assertEquals("[" + FIRST_PLAYER_JSON + ", " + SECOND_PLAYER_JSON + "]",
                playersList.generatePlayersJSON(NOT_PRESENT_PLAYER));
    }

    @Test
    public void shouldBothPlayersJSONWithCardsWhenGeneratePlayersJSONFirstPlayerSecondPlayer() throws Exception {
        assertEquals("[" + FIRST_PLAYER_JSON_WITH_CARDS + ", " + SECOND_PLAYER_JSON_WITH_CARDS + "]",
                playersList.generatePlayersJSON(FIRST_PLAYER, SECOND_PLAYER));
    }

    @Test
    public void shouldMoverIsSecondPlayerChangeDealerWhenSetNewGameSetPlayersNotMovedGetMoverNumber() throws Exception {
        playersList.setNewGame();
        playersList.setPlayersNotMoved();

        when(secondPlayer.isActiveNotRisePlayer()).thenReturn(true);
        when(firstPlayer.isActiveNotRisePlayer()).thenReturn(true);

        assertEquals(secondPlayer, playersList.getMover());
    }

    @Test
    public void should2PlayerNamesWhenGetPlayerNamesDefaultGame() throws Exception {
        assertEquals(2, playersList.getPlayerNames().size());
    }

    @Test
    public void shouldFirstPlayerNameWhenGetPlayerNamesDefaultGameGetFirstPlayer() throws Exception {
        assertEquals(FIRST_PLAYER, playersList.getPlayerNames().get(0));
    }

    @Test
    public void shouldSecondPlayerNameWhenGetPlayerNamesDefaultGameGetSecondPlayer() throws Exception {
        assertEquals(SECOND_PLAYER, playersList.getPlayerNames().get(1));
    }

    @Test
    public void shouldPlayerFirstPlayerKickedWhenKickFirstPlayerSetNewGame() throws Exception {
        playersList.kickPlayer(FIRST_PLAYER);

        playersList.setNewGame();

        assertEquals(SECOND_PLAYER, playersList.getPlayerNames().get(0));
    }

    @Test
    public void shouldFirstPlayerNotRemovedWhenKickFirstPlayer() throws Exception {
        playersList.kickPlayer(FIRST_PLAYER);

        assertEquals(FIRST_PLAYER, playersList.getPlayerNames().get(0));
    }

    @Test
    public void shouldNoExceptionsWhenNewPlayerListGetBigBlindPlayer() throws Exception {
        new PlayersList(eventManagerMock).bigBlindPlayer();
    }

    @Test
    public void shouldNoExceptionsWhenNewPlayerListGetSmallBlindPlayer() throws Exception {
        new PlayersList(eventManagerMock).smallBlindPlayer();
    }

    @Test
    public void shouldNoExceptionsWhenNewPlayerListGetMover() throws Exception {
        new PlayersList(eventManagerMock).getMover();
    }

    @Test
    public void shouldNoExceptionsWhenNewPlayerListGetDealerName() throws Exception {
        new PlayersList(eventManagerMock).getDealerName();
    }

    @Test
    public void shouldGameSettingsMaximumPlayersPlayersWhen20PlayersAddedAndSetNewGame() throws Exception {
        for (int i = 0; i < 20; i++) {
            playersList.add(mock(Player.class));
        }

        playersList.setNewGame();

        assertEquals(GameSettings.getMaximumPlayers(), playersList.size());
    }

    @Test
    public void shouldFirstPlayerSetMoveWhenPlayersListSetFirstPlayerMove() throws Exception {
        final PlayerAction playerAction = mock(PlayerAction.class);

        playersList.setPlayerMove(FIRST_PLAYER, playerAction);

        verify(firstPlayer).setMove(playerAction);
    }

    @Test
    public void shouldSecondPlayerSetMoveWhenPlayersListSetSecondPlayerMove() throws Exception {
        final PlayerAction playerAction = mock(PlayerAction.class);

        playersList.setPlayerMove(SECOND_PLAYER, playerAction);

        verify(secondPlayer).setMove(playerAction);
    }

    @Test
    public void shouldNoFirstPlayerSetMoveWhenPlayersListSetIllegalPlayerMove() throws Exception {
        final PlayerAction playerAction = mock(PlayerAction.class);

        playersList.setPlayerMove("Illegal player", playerAction);

        verify(firstPlayer, never()).setMove(playerAction);
    }
}
