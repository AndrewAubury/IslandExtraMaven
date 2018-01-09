package me.Andrew.IslandExtras.Managers;

import me.Andrew.IslandExtras.Main;

/**
 * Created by Andrew on 18/04/2017.
 */
public class EventManager {

public void setupEvents() {
    Main main = Main.getInst();
    main.getServer().getPluginManager().registerEvents(new me.Andrew.IslandExtras.IslandBorder.EventHandle(),main);
    main.getServer().getPluginManager().registerEvents(new me.Andrew.IslandExtras.EnderChest.EventHandle(),main);
}

}
