package Service;

import Connect.ConnectManager;
import DAO.CurrencyDAO;
import DTO.CurrencyDTO;
import MyException.ExceptionError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {


    private static BigDecimal getConvertedAmount(BigDecimal rate, BigDecimal amount) {
        return rate.multiply(amount).setScale(2, RoundingMode.HALF_DOWN);
    }

    private static Optional<BigDecimal> getRateCrossUSD(int BaseId, int TargetId) throws SQLException, ExceptionError {

        var USD = CurrencyDAO.read("USD");
        if (USD.isEmpty()){
            throw new ExceptionError("USD not found",404);
        }

        Optional<BigDecimal> rateUSDTarget = getRateById(TargetId , USD.get().getId());
        Optional<BigDecimal> rateUSDBase = getRateById(BaseId , USD.get().getId());

        if (rateUSDTarget.isEmpty() || rateUSDBase.isEmpty())
            return Optional.empty();

        else return Optional.of(rateUSDBase.get().divide(rateUSDTarget.get() ,6, RoundingMode.HALF_DOWN));
    }

    public static ObjectNode makeConverted(String bcode, String tcode, BigDecimal amount) throws SQLException, ExceptionError {

        var baseDAO = CurrencyDAO.read(bcode);
        if (baseDAO.isEmpty()){
            throw new ExceptionError("Base currency not found",404);
        }

        var targetDAO = CurrencyDAO.read(tcode);
        if (targetDAO.isEmpty()){
            throw new ExceptionError("Target currency not found",404);
        }

        var rate = getRateById(baseDAO.get().getId(),targetDAO.get().getId());
        if (rate.isEmpty()){
            rate = getRateByIdBacked(baseDAO.get().getId(),targetDAO.get().getId());
        }
        if (rate.isEmpty()){
            rate = getRateCrossUSD(baseDAO.get().getId(),targetDAO.get().getId());
        }
        if (rate.isEmpty()){
            throw new ExceptionError("Converted by USD not possible", 404);
        }

        var convertedAmount = getConvertedAmount(rate.get(), amount);

        return sendResponse(
                new CurrencyDTO(baseDAO.get()),
                new CurrencyDTO(targetDAO.get()),
                rate.get(),
                amount,
                convertedAmount);
    }

    private static Optional<BigDecimal> getRateByIdBacked(Integer baseId, Integer targetId) throws SQLException {

        var rate = getRateById(targetId,baseId);

        if (rate.isEmpty()) return rate;

        return Optional.of(new BigDecimal(1).divide(rate.get(),6, RoundingMode.HALF_DOWN));

    }

    private static ObjectNode sendResponse(CurrencyDTO base, CurrencyDTO target, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        ObjectNode baseCurrencyNode = mapper.valueToTree(base);
        ObjectNode targetCurrencyNode = mapper.valueToTree(target);

        rootNode.set("baseCurrency", baseCurrencyNode);
        rootNode.set("targetCurrency", targetCurrencyNode);
        rootNode.put("rate", rate);
        rootNode.put("amount", amount);
        rootNode.put("convertedAmount", convertedAmount);


        return rootNode;
    }

    public static Optional<BigDecimal> getRateById(Integer BaseId, Integer TargetId) throws SQLException {
        String sql = """
                select Rate
                from ExchangeRates
                where BaseCurrencyId = ? AND TargetCurrencyId = ?;
                """;

        try (var con = ConnectManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, BaseId);
            ps.setInt(2, TargetId);
            ResultSet rs = ps.executeQuery();
            Optional<BigDecimal> optional = Optional.empty();
            while (rs.next()) {
                optional = Optional.of(rs.getBigDecimal("Rate"));
            }
            return optional;
        }
    }
}
