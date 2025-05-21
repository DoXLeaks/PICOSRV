package kr.rth.picoserver.etc;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.UUID;
//import de.tr7zw.nbt

public class daegari implements Listener {
    static daegari instance;

    private daegari(){

    }

    public static ArmorStand getArmorFromPlayer(Player p ){
        ArmorStand as = null;
        NBTCompound nbtent = new NBTEntity(p).getPersistentDataContainer();
        UUID asUUID = nbtent.getUUID("teamNameTag");
        if(asUUID != null){
            as = (ArmorStand) Bukkit.getEntity(asUUID);
        }

//        for( var i : p.getPassengers()){
//            if(i.getType().equals(EntityType.ARMOR_STAND)){
//                ArmorStand armorStand = (ArmorStand) i;
//                NBTEntity nbtent = new NBTEntity(armorStand);
//                if(nbtent.getPersistentDataContainer().getBoolean("teamNameTag") == null){
//                    continue;
//                }
//                if(!nbtent.getPersistentDataContainer().getBoolean("teamNameTag")){
//                    continue;
//                }
//                as = armorStand;
//                break;
//            }
//        }
        return as;
    }
    public static void setArmorStandPlayer(Player p){
//
        if(getArmorFromPlayer(p) != null) {
            getArmorFromPlayer(p).remove();
        }

        ArmorStand as =  p.getWorld().spawn(p.getLocation(), ArmorStand.class);
        as.setCustomName("test");
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);
        as.setBasePlate(false);
        as.setCollidable(false);
        as.setInvulnerable(false);
        as.setPersistent(false);
        as.setSilent(true);

        NBTEntity nbtent = new NBTEntity(as);
        nbtent.getPersistentDataContainer().setBoolean("teamNameTag", true);
        new NBTEntity(p).getPersistentDataContainer().setUUID("teamNameTag", as.getUniqueId());
        p.addPassenger(as);
    }
    public static void reloadArmorStandPlayer(Player p){
        setArmorStandPlayer(p);
    }


    @EventHandler
    public void onDeath(EntityDismountEvent e){
        if(e.getEntity().getType().equals(EntityType.ARMOR_STAND) && e.getDismounted().getType().equals(EntityType.PLAYER)){
            NBTEntity nbtent = new NBTEntity(e.getEntity());
            if(nbtent.getPersistentDataContainer().getBoolean("teamNameTag")){
                e.getEntity().remove();
            }
        }
    }

//    @EventHandler
//    public void onDeath(PlayerRespawnEvent e){
//        if(e.getPlayer().getType().equals(EntityType.PLAYER)){
//                reloadArmorStandPlayer((Player) e.getPlayer());
//            Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(),() -> {
//
//            }, 5L);
//        }
//    }
//    @EventHandler
//    public void onChat(PlayerChatEvent e){
//        Player p = e.getPlayer();
//        p.getHealthScale();
//        HealthBar.getInstance().getPlayerBarManager().;
//
//
//    }
//    @EventHandler
//    public void onJoin(PlayerJoinEvent e){
//        reloadArmorStandPlayer(e.getPlayer());
//    }
    public static daegari getInstance() {
        if(instance == null) {
            instance = new daegari();
        }
        return instance;
    }
}
