package me.Andrew.IslandExtras.IslandBorder;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import me.Andrew.IslandExtras.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Created by Andrew on 18/04/2017.
 */
public class Functions {
    public void sendIslandBorder(UUID p, long timeToStay) {
        sendIslandBorder(Main.getInst().getServer().getPlayer(p), timeToStay);
    }
    public void removeBorder(Player p){
        ProtocolManager pm = Main.getInst().getProtocolManager();
        PacketContainer worldBorder = pm.createPacket(PacketType.Play.Server.WORLD_BORDER);
        worldBorder.getIntegers().write(0, 0);
        worldBorder.getDoubles().write(0, 5000000.00);
        worldBorder.getIntegers().write(0, 2);
        worldBorder.getDoubles().write(0, p.getLocation().getX());
        worldBorder.getDoubles().write(1, p.getLocation().getZ());

        try {
            pm.sendServerPacket(p, worldBorder);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet " + worldBorder, e);
        }

        if(isOpen(p)){
            p.sendMessage(ChatColor.GREEN+"The border is now hidden");
            removeOpen(p);
        }

    }

    public void sendIslandBorder(final Player p, final long timeToStay) {
        Main.getInst().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInst(),new Runnable() {
            public void run() {
                p.sendMessage(ChatColor.GREEN+"Showing island border for "+timeToStay+" seconds.");
                ASkyBlockAPI api = Main.getInst().api;

                Island is = api.getIslandAt(p.getLocation());
                if (is == null) {
                    return;
                }
                api.getChallengeStatus(is.getOwner());


                double min = 50.0;
                double max = is.getProtectionSize();
                int amtOfCha = api.getChallengeStatus(is.getOwner()).size();
                int comp = 0;
                for (Boolean val : api.getChallengeStatus(is.getOwner()).values()) {
                    if (val == true) {
                        comp++;
                    }
                }
                double borderSize = max - (((max - min) / amtOfCha) * (amtOfCha - comp));

                if (is.getCenter() == api.getSpawnLocation()) {
                    borderSize = is.getProtectionSize();
                }

                borderSize = Math.round(borderSize);

                ProtocolManager pm = Main.getInst().getProtocolManager();
//                PacketContainer worldBorder = pm.createPacket(PacketType.Play.Server.WORLD_BORDER);
//                worldBorder.getIntegers().write(0, 0);
//                worldBorder.getDoubles().write(0, borderSize);
//                worldBorder.getIntegers().write(0, 2);
//                worldBorder.getDoubles().write(0, is.getCenter().getX());
//                worldBorder.getDoubles().write(1, is.getCenter().getZ());
//                try {
//                    pm.sendServerPacket(p, worldBorder);
//                } catch (InvocationTargetException e) {
//                    throw new RuntimeException("Cannot send packet " + worldBorder, e);
//                }


                PacketContainer worldBorder = pm.createPacket(PacketType.Play.Server.WORLD_BORDER);

                worldBorder.getIntegers().write(0, 2);
                worldBorder.getDoubles().write(0, p.getLocation().getX());
                worldBorder.getDoubles().write(1, p.getLocation().getZ());

                worldBorder.getIntegers().write(0, 0);
                worldBorder.getDoubles().write(0, 5D);

                worldBorder.getIntegers().write(0, 5);
                worldBorder.getIntegers().write(1, 1);

                try {
                    pm.sendServerPacket(p, worldBorder);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Cannot send packet " + worldBorder, e);
                }

                WorldGuardPlugin wg = Main.getInst().getWorldGuard();
                RegionManager rm = wg.getRegionManager(p.getWorld());
                if(rm.hasRegion("Island"+p.getUniqueId())){
                    rm.removeRegion("Island"+is.getOwner());
                }
                    Location loc1 = is.getCenter().clone().add(borderSize/2,0,borderSize/2);
                    Location loc2 = is.getCenter().clone().subtract(borderSize/2,0,borderSize/2);
                    BlockVector bv1 = new BlockVector(loc1.getBlockX(),0,loc1.getBlockZ());
                    BlockVector bv2 = new BlockVector(loc2.getBlockX(),255,loc2.getBlockZ());
                    ProtectedCuboidRegion region = new ProtectedCuboidRegion("Island"+is.getOwner(),bv1,bv2);
                    //NOW I NEED TO SET LEAVE AND BUILD FLAGS
                    region.setFlag(DefaultFlag.BUILD, StateFlag.State.ALLOW);
                    region.setFlag(DefaultFlag.EXIT, StateFlag.State.DENY);
                    region.setFlag(DefaultFlag.EXIT_DENY_MESSAGE, "Sorry you have not unlocked this part of the island yet. you can do this by the island owner completing challenges");
                    rm.addRegion(region);
                addOpen(p);


                Main.getInst().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInst(),new Runnable() {
                    public void run() {
                        removeBorder(p);
                    }
                },timeToStay*20);
            }
        },10l);

    }
    public void removeOpen(Player p){
        if(isOpen(p)){
            Main.getInst().bordersOpen.remove(p);
        }
    }
    public void addOpen(Player p){
        if(!isOpen(p)){
            Main.getInst().bordersOpen.add(p);
        }
    }
    public boolean isOpen(Player p){
        return Main.getInst().bordersOpen.contains(p);
    }
}
