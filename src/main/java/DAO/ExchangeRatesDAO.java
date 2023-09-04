package DAO;

import Connect.ConnectManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
@AllArgsConstructor
public class ExchangeRatesDAO {

    Integer id;
    CurrencyDAO baseCurrency;
    CurrencyDAO targetCurrency;
    BigDecimal rate;


    public static boolean update(String code, BigDecimal rate) throws SQLException {
        String sql = """
                UPDATE  ExchangeRates SET Rate = ?
                WHERE
                BaseCurrencyId = (SELECT id FROM currencies WHERE code=?)
                AND
                TargetCurrencyId = (SELECT id FROM currencies WHERE code=?);""";
        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(2, code.substring(0,3));
            ps.setString(3, code.substring(3));
            ps.setBigDecimal(1, rate);
           return (0!=ps.executeUpdate());
        }
    }



    public static void create(String bcode, String tcode , BigDecimal rate) throws SQLException {

        String sql = """
                INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, rate)
                VALUES ((SELECT id FROM currencies WHERE code=?),
                        (SELECT id FROM currencies WHERE code=?),
                        ?)""";
        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bcode);
            ps.setString(2, tcode);
            ps.setBigDecimal(3, rate);
            ps.executeUpdate();
        }
    }

    public static List<ExchangeRatesDAO> readAll() throws SQLException {

        String sql = """
                select e.id,
                       c.id BId , c.FullName BName  , c.Code BCode  ,c.Sign BSign,
                       c2.id TId , c2.FullName TName , c2.Code TCode ,c2.Sign TSign,
                       e.Rate
                    from ExchangeRates e
                    join   Currencies  c ON c.id = e.BaseCurrencyId
                    join   Currencies  c2 ON c2.id = e.TargetCurrencyId;
                """;

        try(var con = ConnectManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            List<ExchangeRatesDAO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new ExchangeRatesDAO(rs));
            }

            return list;
        }
    }
    public static Optional<ExchangeRatesDAO> readByCode(String code) throws SQLException {
        String sql = """
                select e.id ,
                       c.id BId , c.FullName BName  , c.Code BCode  ,c.Sign BSign,
                       c2.id TId , c2.FullName TName , c2.Code TCode,c2.Sign TSign,
                       e.Rate
                from ExchangeRates e
                join Currencies c on c.id = e. BaseCurrencyId
                join Currencies c2 on c2.id = TargetCurrencyId where BCode = ? AND TCode= ?;
                """;

        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.substring(0, 3));
            ps.setString(2, code.substring(3));
            ResultSet rs = ps.executeQuery();
            Optional<ExchangeRatesDAO> optional = Optional.empty();
            while (rs.next()) {
                optional = Optional.of(new ExchangeRatesDAO(rs));
            }
            return optional;
        }
    }

    private ExchangeRatesDAO(ResultSet rs) throws SQLException {
        CurrencyDAO base = new CurrencyDAO(
                rs.getInt("BId"),
                rs.getString("BName"),
                rs.getString("BCode"),
                rs.getString("BSign")
        );
        CurrencyDAO target = new CurrencyDAO(
                rs.getInt("TId"),
                rs.getString("TName"),
                rs.getString("TCode"),
                rs.getString("TSign")
        );
        this.id = rs.getInt("id");
        this.baseCurrency=base;
        this.targetCurrency =target;
        this.rate=rs.getBigDecimal("Rate");
    }
}
