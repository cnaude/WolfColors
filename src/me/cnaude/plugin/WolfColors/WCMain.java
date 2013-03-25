package me.cnaude.plugin.WolfColors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cnaude
 */
public class WCMain extends JavaPlugin implements Listener {

    public static final String PLUGIN_NAME = "WolfColors";
    public static final String LOG_HEADER = "[" + PLUGIN_NAME + "]";
    static final Logger log = Logger.getLogger("Minecraft");
    private File pluginFolder;
    private File configFile;
    private static Random randomGenerator;
    public HashMap<String,String> plColors = new HashMap<String,String>();
    private File plColorsFile;

    @Override
    public void onEnable() {
        pluginFolder = getDataFolder();
        configFile = new File(pluginFolder, "config.yml");
        plColorsFile = new File(pluginFolder,"colors.txt");
        loadColorList();
        createConfig();
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("wc").setExecutor(new WCCommands(this));
    }
    
    @Override
    public void onDisable() {
        saveColorList();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTameEvent(EntityTameEvent event) {
        Entity e = event.getEntity();
        Player p = (Player) event.getOwner();
        String pName = p.getName();
        EntityType et = e.getType();

        if (et.equals(EntityType.WOLF)) {
            if (p.hasPermission("wolfcolors.wolfcolors")) {
                if (plColors.containsKey(p.getName())) {
                    ((Wolf) e).setCollarColor(DyeColor.valueOf(plColors.get(pName)));
                } else {
                    randomGenerator = new Random();
                    int r = randomGenerator.nextInt(15);
                    ((Wolf) e).setCollarColor(DyeColor.getByData((byte)r));
                }
            }
        }
    }

    private void createConfig() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                logInfo("ERROR: " + e.getMessage());                
            }
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                logInfo("ERROR: " + e.getMessage());
            }
        }
    }

    private void saveColorList() {
        try {
            PrintWriter out = new PrintWriter(plColorsFile);
            for (String s : plColors.keySet()) {
                out.println(s + ":" + plColors.get(s));                
            }
            out.close();            
        } catch (Exception ex) {
            logError(ex.getMessage());
        }
    }

    private void loadColorList() {
        BufferedReader reader = null;
        if (plColorsFile.exists()) {
            try {
                reader = new BufferedReader(new FileReader(plColorsFile));
                String text;
                while ((text = reader.readLine()) != null) {                    
                    String[] items = text.split(":", 2);
                    plColors.put(items[0], items[1]);                    
                }
            } catch (IOException e) {
                logError(e.getMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    logError(e.getMessage());
                }
            }
        }
    }
            
    public void logInfo(String _message) {
        log.log(Level.INFO, String.format("%s %s", LOG_HEADER, _message));
    }

    public void logError(String _message) {
        log.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, _message));
    }
}
