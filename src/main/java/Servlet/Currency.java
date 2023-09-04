package Servlet;

import DTO.CurrencyDTO;
import MyException.ExceptionError;
import Utils.Validator;
import DAO.CurrencyDAO;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "Currency", value = "/currency/*")
public class Currency extends AbstractServlet {

    @Override
    protected void Post(HttpServletRequest request, HttpServletResponse response){


    }

    @Override
    protected void Get(HttpServletRequest request, HttpServletResponse response) throws IOException, ExceptionError, SQLException {
        String code = request.getPathInfo().substring(1);
        if (!Validator.isCodeValid(code)) {
            throw new ExceptionError("You haven't put currency code in path", 400);
        }
        var currencyDao = CurrencyDAO.read(code);
        if (currencyDao.isEmpty()) {
            throw new ExceptionError("Currency not found", 404);
        }
        String json = objectMapper.writeValueAsString(new CurrencyDTO(currencyDao.get()));
        response.getWriter().write(json);
        response.setStatus(200);
    }
}