package Servlet;

import DTO.ExchangeRatesDTO;
import MyException.ExceptionError;
import Utils.Validator;
import DAO.ExchangeRatesDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "ExchangeRates", value = "/ExchangeRates")
public class ExchangeRates extends AbstractServlet {

    @Override
    protected void Get(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String json = objectMapper.writeValueAsString(ExchangeRatesDTO.toDTO(ExchangeRatesDAO.readAll()));
        response.getWriter().write(json);
        response.setStatus(200);
    }


    @Override
    protected void Post(HttpServletRequest request, HttpServletResponse response) throws IOException, ExceptionError, SQLException {

        String bcode = request.getParameter("baseCurrencyCode");
        String tcode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        if (!Validator.isExchangeRatesCodeValid(bcode,tcode,rate)) {
            throw new ExceptionError("Argument not valid", 400);

        }
        try {
            ExchangeRatesDAO.create(bcode , tcode , new BigDecimal(rate));

            var optional = ExchangeRatesDAO.readByCode(bcode+tcode);
            var json = objectMapper.writeValueAsString(new ExchangeRatesDTO(optional.get()));
            response.getWriter().write(json);
            response.setStatus(200);

        } catch (SQLException e) {
            if (e.getErrorCode()==19){
                throw new ExceptionError("ExchangeRate already exists", 409);
            }
            else throw e;
        }


    }
}