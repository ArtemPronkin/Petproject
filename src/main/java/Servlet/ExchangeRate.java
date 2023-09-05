package Servlet;

import DAO.ExchangeRatesDAO;
import DTO.ExchangeRatesDTO;

import MyException.ExceptionError;
import Utils.ErrorStatus;
import Utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "ExchangeRate", value = "/exchangeRate")
public class ExchangeRate extends AbstractServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("PATCH")){
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    private void doPatch(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getPathInfo().substring(1);
        String rate = request.getParameter("rate");
        if (!Validator.isCodePairValid(code) || rate == null){
            ErrorStatus.setStatus(400,"You haven't put currency code or rate in path",response);
            return;
        }
        try {
            if (!ExchangeRatesDAO.update(code,new BigDecimal(rate))){
                ErrorStatus.setStatus(404,"Exchange rate for pair not found in table",response);
                return;
            }
            var optional = ExchangeRatesDAO.readByCode(code);
            var json = objectMapper.writeValueAsString(new ExchangeRatesDTO(optional.get()));
            response.getWriter().write(json);
            response.setStatus(200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            ErrorStatus.setStatus(500,"Database is unavailable ",response);
        }
    }
    @Override
    protected void Get(HttpServletRequest request, HttpServletResponse response) throws  IOException, ExceptionError, SQLException {
        String code = request.getPathInfo().substring(1);
        if (!Validator.isCodePairValid(code)) {
            throw new ExceptionError("You haven't put currency code in path", 400);
        }
        var optional = ExchangeRatesDAO.readByCode(code);
        if (optional.isEmpty()) {
            throw new ExceptionError("Exchange rate for pair not found", 404);
        } else {
            var json = objectMapper.writeValueAsString(new ExchangeRatesDTO(optional.get()));
            response.getWriter().write(json);
            response.setStatus(200);
        }
    }

    @Override
    protected void Post(HttpServletRequest request, HttpServletResponse response){


    }
}