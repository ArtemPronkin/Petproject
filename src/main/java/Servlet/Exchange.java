package Servlet;
import MyException.ExceptionError;
import Service.ExchangeService;
import Utils.Validator;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.sql.SQLException;


@WebServlet(name = "exchange", value = "/exchange")
public class Exchange extends AbstractServlet {
    @SneakyThrows
    @Override
    protected void Get(HttpServletRequest request, HttpServletResponse response) throws ExceptionError, SQLException {
        String baseCode = request.getParameter("from");
        String targetCode = request.getParameter("to");
        String amount = request.getParameter("amount");
        if (!Validator.isExchangeRatesCodeValid(baseCode,targetCode,amount)){
            throw new ExceptionError("Arguments not valid",400);
        }
        var json = objectMapper
                .writeValueAsString(ExchangeService
                        .makeConverted(baseCode,targetCode,new BigDecimal(amount)));
        response.getWriter().write(json);
        response.setStatus(200);

    }

    @Override
    protected void Post(HttpServletRequest request, HttpServletResponse response) {

    }
}