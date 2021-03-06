package pl.kajmaczek.mctraps.helpop;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kajmaczek.mctraps.helpop.ChatInput.ChatInput;
import pl.kajmaczek.mctraps.helpop.commands.HelpopCommand;
import pl.kajmaczek.mctraps.helpop.listeners.PlayerChatListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class HelpOP extends JavaPlugin {
    FileConfiguration config;

    public String rTable;

    Connection connection;
    public Statement statement;
    private String host, database, username, password;
    private int port;

    HelpopCommand helpopCommand;
    PlayerChatListener playerChatListener;

    public ChatInput ci = new ChatInput();

    @Override
    public void onEnable() {
        getLogger().info("MCTrapsHelpOp has been enabled");

        saveDefaultConfig();
        getDataFolder().mkdir();
        config = getConfig();

        host = config.getString("database.host");
        port = config.getInt("database.port");
        database = config.getString("database.database");
        username = config.getString("database.username");
        password = config.getString("database.password");
        rTable = config.getString("tables.requests");

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(this);

        helpopCommand = new HelpopCommand(this);

        playerChatListener = new PlayerChatListener(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("MCTrapsHelpOp has been disabled");
    }

    void openConnection() throws SQLException, ClassNotFoundException {
        if(connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if(connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);

            getLogger().info("Successfully connected to database. Hurrey!");
        }
    }

    public static String colorify(String s) {
        if(s != null) {
            return ChatColor.translateAlternateColorCodes('&', s);
        }

        return null;
    }
}
