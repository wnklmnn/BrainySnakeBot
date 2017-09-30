package de.adesso.brainysnake.sampleplayer;

import java.util.List;
import java.io.*;

import de.adesso.brainysnake.playercommon.*;


/**
 * Implementiere hier deine Schlangensteuerung.
 */
public class YourPlayer implements BrainySnakePlayer {

    private PlayerState ps;

    @Override
    public String getPlayerName() {
        return "YourName";
    }

    @Override
    public boolean handlePlayerStatusUpdate(PlayerState playerState) {
        this.ps = playerState;
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
        if(isPoint()>-1){
            System.out.println("Yay");
        }
        for(int i = 0; i<25; i++){
            if(i%5==0)
                System.out.println("");
            System.out.print(printField(i));
        }
        System.out.println("");

        return new PlayerUpdate(null);
    }

    private int isPoint(){
        int num = 0;
        for(Field f : this.ps.getPlayerView().getVisibleFields()) {
            if(f.getFieldType() == FieldType.POINT)
                return num;
        }
        return -1;
    }

    private Orientation right(){
        switch (ps.getPlayerView().getCurrentOrientation()){
            case UP:
                return Orientation.RIGHT;
            case RIGHT:
                return Orientation.DOWN;
            case DOWN:
                return Orientation.LEFT;
            case LEFT:
                return Orientation.UP;
        }
    }

    private Orientation left(){
        switch (ps.getPlayerView().getCurrentOrientation()){
            case UP:
                return Orientation.LEFT;
            case RIGHT:
                return Orientation.UP;
            case DOWN:
                return Orientation.RIGHT;
            case LEFT:
                return Orientation.DOWN;
        }
    }

    private String printField(int id){
        FieldType f = this.ps.getPlayerView().getVisibleFields().get(id).getFieldType();
        switch (f) {
            case LEVEL:
                return "L";
            case EMPTY:
                return "E";
            case PLAYER:
                return "S";
            case POINT:
                return "P";
            default:
                return "N";
        }
    }
}
