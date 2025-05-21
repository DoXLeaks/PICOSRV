package kr.rth.picoserver.battle;

import org.bukkit.entity.Player;

public class Storage {
//    boolean isHealedPlayer1 = false;
//    boolean isHealedPlayer2 = false;
//
//
//    Player p1 = null;
//    Player p2 = null;
//
//    boolean isStarted = false;
//    boolean isRunning = false;


    static Storage instance;


    public void reset(){
//        isHealedPlayer1 = false; isHealedPlayer2 = false; p1 = null; p2 = null; boolean isStarted = false; boolean isRunning = false;
    }


//    public void setPlayer1(Player p ) {
//        p1 = p;
//    }
//    public void setPlayer2(Player p ) {
//        p2 = p;
//    }
//    public void setStarted(boolean value) {
//        isStarted = value;
//    }
//    public void setHealedPlayer1(Player p ) {
//        p1 =p;
//    }
//    public void setHealedPlayer2(Player p ) {
//        p2 =p;
//    }
//
//
//    public Player getPlayer1(Player p ) {
//        return p1;
//    }
//    public Player getPlayer2(Player p ) {
//        return p2;
//    }
//    public boolean getStarted() {
//
//        return isStarted;
//    }
//    public boolean getHealedPlayer1(Player p ) {
//        return isHealedPlayer1;
//    }
//    public boolean getHealedPlayer2(Player p ) {
//        return isHealedPlayer2;
//    }
//

    private Storage() {

    }

    public static Storage getInstance() {
        if(instance == null) {
            instance = new Storage();
        }
        return instance;
    }
}
