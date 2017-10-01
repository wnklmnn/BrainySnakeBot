package de.adesso.brainysnake.sampleplayer;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

import de.adesso.brainysnake.playercommon.*;

/**
 * Implementiere hier deine Schlangensteuerung.
 */
public class YourPlayer implements BrainySnakePlayer {
    private static int num = 0;
    private String name;
    private PlayerState ps;

    private Queue<doit>   stepQ = new LinkedList<>();
    private Queue<doit> zigzagQ = new LinkedList<>();

    private doit lastStep   = doit.RIGHT;
    private doit lastZigZag = doit.RIGHT;

    private FieldType prevField22 = FieldType.EMPTY,
                      prevField24 = FieldType.EMPTY;

    public YourPlayer() {
        this.name = String.format("YourPlayer%d", ++num);
    }

    public static void printPlayerView(List<Field> f) {
        for (int i = 0; i<25; i++) {
            if (i%5==0)
                System.out.println();
            System.out.print(printField(f, i));
        }
        System.out.println();
    }

    public enum doit {
        RIGHT,
        LEFT,
        FORWARD
    }

    public class pos {
        int x;
        int y;
        public pos(int tx, int ty) {
            x=tx;
            y=ty;
        }
    }

    @Override
    public String getPlayerName() {
        return this.name;
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
        if (stepQ.isEmpty() && isPoint() != -1) {
            int index = isPoint();
            pos p = indexToCoordinate(index);
            switch (p.x) {
                case -2:
                    stepQ.add(doit.LEFT);
                    stepQ.add(doit.FORWARD);
                    if (p.y > 0)
                        stepQ.add(doit.RIGHT);
                    break;
                case -1:
                    stepQ.add(doit.LEFT);
                    if (p.y > 0)
                        stepQ.add(doit.RIGHT);
                    break;
                case 0:
                    break;
                case 1:
                    stepQ.add(doit.RIGHT);
                    if (p.y > 0)
                        stepQ.add(doit.LEFT);
                    break;
                case 2:
                    stepQ.add(doit.RIGHT);
                    stepQ.add(doit.FORWARD);
                    if (p.y > 0)
                        stepQ.add(doit.LEFT);
                    break;
            }

            if (p.y > 0)
                for (int i = p.y-1; i >= 0; i--)
                    stepQ.add(doit.FORWARD);
        }

        //TODO: remove debug output before release
        for (int i = 0; i < 25; i++) {
            if (i%5 == 0)
                System.out.println();
            System.out.print(printField(ps.getPlayerView().getVisibleFields(), i));
        }
        System.out.println();

        if (!stepQ.isEmpty()) {
            refresh22and24();
            switch (stepQ.poll()) {
                case LEFT:
                    return new PlayerUpdate(left());
                case RIGHT:
                    return new PlayerUpdate(right());
                case FORWARD:
                default:
                    return new PlayerUpdate(forward());
            }
        }


        if (lastZigZag == doit.LEFT) {
            Orientation d;
            lastZigZag = doit.RIGHT;
            if (prevField22 == FieldType.LEVEL) {
                lastZigZag = doit.LEFT;
                d = right();
            } else
                d = left();
            refresh22and24();
            return new PlayerUpdate(d);
        }
        else {
            Orientation d;
            lastZigZag = doit.LEFT;
            if (prevField24 == FieldType.LEVEL) {
                lastZigZag = doit.RIGHT;
                d = left();
            } else
                d = right();
            refresh22and24();
            return new PlayerUpdate(d);
        }
    }

    private void refresh22and24(){
        this.prevField22 = this.ps.getPlayerView().getVisibleFields().get(23).getFieldType();
        this.prevField24 = this.ps.getPlayerView().getVisibleFields().get(21).getFieldType();
    }


    private int isPoint() {
        int num = 0;
        for (Field f : this.ps.getPlayerView().getVisibleFields()) {
            if (f.getFieldType() == FieldType.POINT)
                return num;
            num++;
        }
        return -1;
    }

    private Orientation right() {
        switch (ps.getPlayerView().getCurrentOrientation()) {
            case UP:
                return Orientation.RIGHT;
            case RIGHT:
                return Orientation.DOWN;
            case DOWN:
                return Orientation.LEFT;
            case LEFT:
                return Orientation.UP;
        }
        return ps.getPlayerView().getCurrentOrientation();
    }

    private Orientation left() {
        switch (ps.getPlayerView().getCurrentOrientation()) {
            case UP:
                return Orientation.LEFT;
            case RIGHT:
                return Orientation.UP;
            case DOWN:
                return Orientation.RIGHT;
            case LEFT:
                return Orientation.DOWN;
        }
        return ps.getPlayerView().getCurrentOrientation();
    }

    private Orientation forward() {
        return ps.getPlayerView().getCurrentOrientation();
    }

    // .....___5____
    // ....|5.....25|
    // ....|4.9...24|_
    // ..5.|3.8...23|_<Schlange
    // ....|2.7.....|
    // ....|1_6_____|

    private pos indexToCoordinate(int index) {
        int tindex = index;
        int y = 0;
        while (tindex > 4) {
            y++;
            tindex -= 5;
        }
        y = 5-y;
        int x = tindex-2;
        return new pos(x,y);
    }

    private static String printField(List<Field> field, int id) {
        FieldType f = field.get(id).getFieldType();
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
