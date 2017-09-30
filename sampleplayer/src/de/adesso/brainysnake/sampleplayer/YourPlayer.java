package de.adesso.brainysnake.sampleplayer;

import de.adesso.brainysnake.playercommon.BrainySnakePlayer;
import de.adesso.brainysnake.playercommon.PlayerState;
import de.adesso.brainysnake.playercommon.PlayerUpdate;


/**
 * Implementiere hier deine Schlangensteuerung.
 */
public class YourPlayer implements BrainySnakePlayer {
    private static int num = 0;
    private String name;

    private PlayerState playerState;
    public YourPlayer(){
        this.name= String.format("YourPlayer%d", ++num);
    }
    @Override
    public String getPlayerName() {
        return this.name;
    }

    @Override
    public boolean handlePlayerStatusUpdate(PlayerState playerState) {
        this.playerState = playerState;
        return true;
    }

    // .....___5____
    // ....|5.....25|
    // ....|4.9...24|_
    // ..5.|3.8...23|_<Schlange
    // ....|2.7.....|
    // ....|1_6_____|

    @Override
    public PlayerUpdate tellPlayerUpdate() {
        return new PlayerUpdate(null);
    }
}
