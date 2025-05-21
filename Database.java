package kr.rth.picoserver;


import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Database {
    static Database instance;
    public String connectionString  = "jdbc:mysql://localhost/";
    public String ID = "root";
    public String PW = "dmstjrdmstjr1001@!!@!!";
    HikariDataSource datasource;
    Connection conn;


    private Database() {
        String dbname = "testplugindata";
        Integer maxPoolSize = 5;
        PICOSERVER.getInstance().getLogger().info("Server Port : " + Bukkit.getServer().getPort());
        if(Bukkit.getServer().getPort() == 2){
            PICOSERVER.getInstance().getLogger().info("Using database 'plugindata'.");
            dbname = "plugindata";
            maxPoolSize = 100;
        }

        this.datasource = new HikariDataSource();
        this.datasource.setJdbcUrl(connectionString + dbname);
        this.datasource.setUsername(ID);
        this.datasource.setPassword(PW);
        this.datasource.setMaximumPoolSize(maxPoolSize);
//        this.datasource.size

        this.datasource.setMaxLifetime(30 * 1000);
        try {
            conn = datasource.getConnection();
        } catch (SQLException e) {
            PICOSERVER.getInstance().getLogger().severe("Cannot get connection from database! Disabling...");
            Bukkit.getPluginManager().disablePlugin(PICOSERVER.getInstance());
        }
    }
    public ArrayList<Map<String, Object>>
    execute(String sql, @Nullable ArrayList<Object> args ) throws SQLException  {
        PreparedStatement ps = conn.prepareStatement(sql);

        if(args != null) {
            for(int i = 1; i < args.size() + 1; i ++) {
                ps.setObject(i, args.get(i - 1));
            }
        }

        ps.execute();
        ResultSet rs = ps.getResultSet();
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        try{
            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
        }

        catch (Exception a) {
            result = null;
        }

        if (rs != null) rs.close();
        ps.close();
        return result;
    }

//    private void setVariable(String key, Object value) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(baos);
//        out.writeObject(value);
//        out.close();
//        byte[] serializedObject = baos.toByteArray();
//        String base64String = Base64.getEncoder().encodeToString(serializedObject);
//    }



    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }
}
