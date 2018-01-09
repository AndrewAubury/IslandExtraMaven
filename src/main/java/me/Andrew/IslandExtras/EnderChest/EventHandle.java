package me.Andrew.IslandExtras.EnderChest;

import java.util.UUID;

import me.Andrew.IslandExtras.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.events.ChallengeCompleteEvent;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;


public class EventHandle implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {
				if (p.isSneaking()) {
					e.setCancelled(true);
					p.openInventory(IslandInventoryStorage.getInstance().getInventory(p));
					p.playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_ENDERCHEST_OPEN, 100, 100);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryPickupItem(InventoryPickupItemEvent e) {
		if (e.getInventory().getType() == InventoryType.HOPPER) {
			Main.getInst().getServer().broadcastMessage("Moving Item to hopper!");
			ASkyBlockAPI api = Main.getInst().api;
			// THE ITEM HAS GONE IN TO THE HOPPER!
			Inventory inv = e.getInventory();
			Location loc = inv.getLocation();
			Block b = loc.getBlock();
			UUID owner = api.getIslandAt(loc).getOwner();
			if (loc.subtract(0, 1, 0).getBlock().getType() == Material.ENDER_CHEST) {
				Main.getInst().getServer().broadcastMessage("Will attempt to put it in the ender inv!");
				Inventory einv = IslandInventoryStorage.getInstance().getInventory(owner);
				einv.addItem(e.getItem().getItemStack());
				e.setCancelled(true);
				e.getItem().remove();
			}
		}
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		if (e.getDestination().getType() == InventoryType.HOPPER) {
			Main.getInst().getServer().broadcastMessage("Moving Item to hopper!");
			ASkyBlockAPI api = Main.getInst().api;
			// THE ITEM HAS GONE IN TO THE HOPPER!
			Inventory inv = e.getDestination();
			Location loc = inv.getLocation();
			Block b = loc.getBlock();
			UUID owner = api.getIslandAt(loc).getOwner();
			if (loc.subtract(0, 1, 0).getBlock().getType() == Material.ENDER_CHEST) {
				Main.getInst().getServer().broadcastMessage("Will attempt to put it in the ender inv!");
				Inventory einv = IslandInventoryStorage.getInstance().getInventory(owner);
				einv.addItem(e.getItem());
				if(e.getSource().getType() != InventoryType.CHEST){
					//e.setCancelled(true);
				}

                e.getInitiator().clear();
				e.getSource().clear();
				e.getInitiator().remove(e.getItem());
				e.getInitiator().removeItem(e.getItem());
				e.getSource().removeItem(e.getItem());
				e.getSource().remove(e.getItem());
				e.getDestination().removeItem(e.getItem());
				e.getDestination().remove(e.getItem());


			}
		}
	}
}
