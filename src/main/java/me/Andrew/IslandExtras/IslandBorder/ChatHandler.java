package me.Andrew.IslandExtras.IslandBorder;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Andrew on 04/06/2017.
 */
public class ChatHandler {
    private String prefix = cc("&a[Prefix] ");

    public void sendHelp(Player p){
        ArrayList help = new ArrayList();
        help.add("&cHelp1");
        help.add("&cHelp2");
        help.add("&cHelp3");
        help.add("&cHelp4");
    }
    public void send(String msg, Player p){
        p.sendMessage(prefix+cc(msg));
    }
    public void sendList(ArrayList<String> al, Player p){
        send("--------------------------",p);
        for(String st : al){
            send(st,p);
        }
        send("--------------------------",p);
    }
    public void sendError(String ce,Player p){

    }
    public String cc(String txt){
        return(ChatColor.translateAlternateColorCodes('&',txt));
    }

}
