package DAO;

import Connect.ConnectManager;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class CurrencyDAO {
    Integer id;
    String fullname;
    String code;
    String sign;

    public CurrencyDAO(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        fullname = rs.getString("FullName");
        code = rs.getString("Code");
        sign = rs.getString("Sign");
    }

    public CurrencyDAO(int id, String fullname, String code, String sign) {
        this.id = id;
        this.fullname = fullname;
        this.code = code;
        this.sign = sign;
    }
    public static Optional<CurrencyDAO> read (String code) throws SQLException {
        String sql = "select * from Currencies where Code = ?;";
        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            Optional<CurrencyDAO> optional = Optional.empty();
            while (rs.next()) {
                optional =Optional.of(new CurrencyDAO(rs)) ;
            }
            ps.close();
            return optional;
        }
    }

    public static void create(String name, String code, String sign) throws SQLException {

        String sql = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?,?,?);";

        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, sign);
            ps.executeUpdate();
        }
    }
    public static List<CurrencyDAO> readAll () throws SQLException {
        String sql = "select * from Currencies;";
        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<CurrencyDAO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new CurrencyDAO(rs));
            }
            return list;
        }
    }
}
