package Servlet;

import DAO.ExchangeRatesDAO;
import DTO.ExchangeRatesDTO;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "exchangeRates", value = "/exchangeRates")
public class ExchangeRates extends AbstractServlet {
    @Override
    protected void Get(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
            String json = objectMapper.writeValueAsString(ExchangeRatesDTO.toDTO(ExchangeRatesDAO.readAll()));
            response.getWriter().write(json);
            response.setStatus(200);
    }

    @Override
    protected void Post(HttpServletRequest request, HttpServletResponse response){


    }
}