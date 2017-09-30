package de.wnklmnn.brainy;

import com.badlogic.gdx.graphics.Color;
import de.adesso.brainysnake.Gamelogic.GameMaster;
import de.adesso.brainysnake.Gamelogic.Level.Level;
import de.adesso.brainysnake.Gamelogic.Game;

import java.util.Timer;

public class Main {
    public static void main(String[] args){
        int counter = 0;


        while(true){
            Game g = new Game();
            g.init(100, 100);
            while(!g.isGameOver()){
                g.update(1f);
            }
            g=null;
            System.out.println(counter++);
        }


        //System.exit(0);
    }
}

