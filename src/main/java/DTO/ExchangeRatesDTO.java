package DTO;

import DAO.ExchangeRatesDAO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExchangeRatesDTO {

    Integer id;
    CurrencyDTO baseCurrency;
    CurrencyDTO targetCurrency;
    BigDecimal rate;

    public ExchangeRatesDTO(ExchangeRatesDAO dao) {
        this.id= dao.getId();
        this.baseCurrency=new CurrencyDTO(dao.getBaseCurrency());
        this.targetCurrency=new CurrencyDTO(dao.getTargetCurrency());
        this.rate=dao.getRate();
    }
    public static List<ExchangeRatesDTO> toDTO (List<ExchangeRatesDAO> listDAO){
        List<ExchangeRatesDTO> listDTO = new ArrayList<>();
        for (ExchangeRatesDAO dao :
             listDAO) {
            listDTO.add(new ExchangeRatesDTO(dao));
        }
        return listDTO;
    }
}
