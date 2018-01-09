package me.Andrew.IslandExtras.EnderChest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import me.Andrew.IslandExtras.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

public class IslandInventoryStorage {
	private static IslandInventoryStorage me;
	private HashMap<UUID, Inventory> data = new HashMap<UUID, Inventory>();

	public IslandInventoryStorage() {
		me = this;
	}

	public static IslandInventoryStorage getInstance() {
		return me;
	}

	private void storeInv(Player p, Inventory ii) {
		me.data.put(p.getUniqueId(), ii);
	}
	
	private void storeInv(UUID uuid, Inventory ii) {
		me.data.put(uuid, ii);
	}

	private Inventory fromStorage(Player p) {
		ASkyBlockAPI api = Main.getInst().api;
		Island is = api.getIslandAt(p.getLocation());
		if (api.getCoopIslands(p).contains(is.getCenter())) {
			return me.data.get(is.getOwner());
		} else if (api.inTeam(p.getUniqueId())) {
			UUID leader = api.getTeamLeader(p.getUniqueId());
			return me.data.get(leader);
		} else {
			return null;
		}
	}
	
	private Inventory fromStorage(UUID uuid) {
		ASkyBlockAPI api = Main.getInst().api;
		UUID leader = uuid;
		return me.data.get(leader);
	}

	private ItemStack[] getFromFile(Player p) {
		UUID lookfor = null;
		ASkyBlockAPI api = Main.getInst().api;
		Island is = api.getIslandAt(p.getLocation());
		if (api.getCoopIslands(p).contains(is.getCenter())) {
			lookfor = is.getOwner();
		} else if (api.inTeam(p.getUniqueId())) {
			lookfor = api.getTeamLeader(p.getUniqueId());
		} else {
			lookfor = p.getUniqueId();
		}

		Main ma = Main.getInst();
		if (!ma.getConfig().isConfigurationSection("Inventorys")) {
			ma.getConfig().createSection("Inventorys");
			ma.saveConfig();
		}
		ConfigurationSection cs = ma.getConfig().getConfigurationSection("Inventorys");
		if (cs.get(lookfor.toString()) != null) {
			ItemStack[] items = stringToItems(cs.getString(lookfor.toString()));
			return items;
		} else {
			return null;
		}
	}
	
	private ItemStack[] getFromFile(UUID uuid) {
		UUID lookfor = uuid;
		ASkyBlockAPI api = Main.getInst().api;

		Main ma = Main.getInst();
		if (!ma.getConfig().isConfigurationSection("Inventorys")) {
			ma.getConfig().createSection("Inventorys");
			ma.saveConfig();
		}
		ConfigurationSection cs = ma.getConfig().getConfigurationSection("Inventorys");
		if (cs.get(lookfor.toString()) != null) {
			ItemStack[] items = stringToItems(cs.getString(lookfor.toString()));
			return items;
		} else {
			return null;
		}
	}

	public Inventory getInventory(Player p) {
		Inventory ii = fromStorage(p);
		if (ii != null) {
			return (ii);
		}

		Main ma = Main.getInst();
		Inventory iinv = ma.getServer().createInventory(null, 54, " Island Ender Chest");

		ItemStack[] items = getFromFile(p);
		if (items != null) {
			for (ItemStack item : items) {
				if(item != null){
					iinv.addItem(item);
				}
				
			}
		}

		storeInv(p, iinv);
		return iinv;
	}

	public Inventory getInventory(UUID uuid) {
		Inventory ii = fromStorage(uuid);
		if (ii != null) {
			return (ii);
		}

		Main ma = Main.getInst();
		Inventory iinv = ma.getServer().createInventory(null, 54, " Island Ender Chest");

		ItemStack[] items = getFromFile(uuid);
		if (items != null) {
			for (ItemStack item : items) {
				if(item != null){
					iinv.addItem(item);
				}
				
			}
		}

		storeInv(uuid, iinv);
		return iinv;
	}
	public void saveToConfig() {
		Main ma = Main.getInst();
		ConfigurationSection cs = ma.getConfig().getConfigurationSection("Inventorys");
		for (UUID cur : me.data.keySet()) {
			String itemString = "";
			itemString = itemsToString(me.data.get(cur).getContents());
			
			cs.set(cur.toString(), itemString);
		}
		ma.saveConfig();
	}

	public String itemsToString(ItemStack[] items) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(serializeItemStack(items));
			oos.flush();
			return DatatypeConverter.printBase64Binary(bos.toByteArray());
		} catch (Exception e) {

		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public ItemStack[] stringToItems(String s) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(s));
			ObjectInputStream ois = new ObjectInputStream(bis);
			return deserializeItemStack((Map<String, Object>[]) ois.readObject());
		} catch (Exception e) {

		}
		return new ItemStack[] { new ItemStack(Material.AIR) };
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object>[] serializeItemStack(ItemStack[] items) {

		Map<String, Object>[] result = new Map[items.length];

		for (int i = 0; i < items.length; i++) {
			ItemStack is = items[i];
			if (is == null) {
				result[i] = new HashMap<>();
			} else {
				result[i] = is.serialize();
				if (is.hasItemMeta()) {
					result[i].put("meta", is.getItemMeta().serialize());
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static ItemStack[] deserializeItemStack(Map<String, Object>[] map) {
		ItemStack[] items = new ItemStack[map.length];

		for (int i = 0; i < items.length; i++) {
			Map<String, Object> s = map[i];
			if (s.size() == 0) {
				items[i] = null;
			} else {
				try {
					if (s.containsKey("meta")) {
						Map<String, Object> im = new HashMap<>((Map<String, Object>) s.remove("meta"));
						im.put("==", "ItemMeta");
						ItemStack is = ItemStack.deserialize(s);
						is.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(im));
						items[i] = is;
					} else {
						items[i] = ItemStack.deserialize(s);
					}
				} catch (Exception e) {
					items[i] = null;
				}
			}

		}

		return items;
	}

}
