package Servlet;

import DTO.ExchangeRatesDTO;
import MyException.ExceptionError;
import Utils.ErrorStatus;
import Utils.Validator;
import DAO.ExchangeRatesDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "ExchangeRatesSpecific", value = "/ExchangeRatesSpecific")
public class ExchangeRatesSpecific extends AbstractServlet {
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
            response.sendRedirect("/exchangeRate/"+code);
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
    protected void Post(HttpServletRequest request, HttpServletResponse response) throws IOException, ExceptionError, SQLException {

        String bcode = request.getParameter("baseCurrencyCode");
        String tcode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        if (!Validator.isExchangeRatesCodeValid(bcode,tcode,rate)) {
            throw new ExceptionError("Argument not valid", 400);

        }
        try {
            ExchangeRatesDAO.create(bcode , tcode , new BigDecimal(rate));
            response.sendRedirect("/exchangeRate/"+bcode+tcode);

        } catch (SQLException e) {
            if (e.getErrorCode()==19){
                throw new ExceptionError("ExchangeRate already exists", 409);
            }
            else throw e;
        }


    }
}