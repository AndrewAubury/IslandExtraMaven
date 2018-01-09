package me.Andrew.IslandExtras.IslandBorder;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.events.ChallengeCompleteEvent;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandExitEvent;
import me.Andrew.IslandExtras.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandle implements Listener {

	Functions func;

	public EventHandle(){
		func = new Functions();

	}


	@EventHandler
	public void onIslandEnter(IslandEnterEvent e) {
		func.sendIslandBorder(e.getPlayer(),10);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		func.sendIslandBorder(e.getPlayer(),10);
	}
	//@EventHandler
	//public void onIslandExit(IslandExitEvent e){
	//	func.removeBorder(Main.getInst().getServer().getPlayer(e.getPlayer()));
	//}

	@EventHandler
	public void onChallangeCompleate(ChallengeCompleteEvent e) {
		ASkyBlockAPI api = Main.getInst().api;
		Island is = api.getIslandOwnedBy(e.getPlayer().getUniqueId());
		if (is == null) {
			return;
		}
		if (api.playerIsOnIsland(e.getPlayer())) {
			if (is.getCenter() == api.getIslandLocation(e.getPlayer().getUniqueId())) {
				func.sendIslandBorder(e.getPlayer(),10);
			}
		}
		for (Player pl : Main.getInst().getServer().getOnlinePlayers()) {
			if (is.getCenter() == api.getIslandLocation(pl.getUniqueId())) {
				func.sendIslandBorder(pl,10);
			}
		}
	}
}
