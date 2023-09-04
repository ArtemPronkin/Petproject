package DTO;

import DAO.CurrencyDAO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CurrencyDTO {
    Integer id;
    String name;
    String code;
    String sign;

    public CurrencyDTO(CurrencyDAO dao) {
        this.id = dao.getId();
        this.name = dao.getFullname();
        this.code = dao.getCode();
        this.sign = dao.getSign();
    }
    public static List<CurrencyDTO> DAOtoDTO(List<CurrencyDAO> listDAO){
        List<CurrencyDTO> listDTO = new ArrayList<>();
        for (CurrencyDAO dao:
             listDAO) {
            listDTO.add(new CurrencyDTO(dao));
        }
        return listDTO;
    }
    
}
