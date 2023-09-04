package Servlet;

import DTO.CurrencyDTO;
import MyException.ExceptionError;
import Utils.ErrorStatus;
import DAO.CurrencyDAO;
import Utils.Validator;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "currencies", value = "/currencies")
public class Currencies extends AbstractServlet {
    @Override
    protected void Get(HttpServletRequest request, HttpServletResponse response) throws IOException {

            try {
                var json = objectMapper.writeValueAsString(CurrencyDTO.DAOtoDTO(CurrencyDAO.readAll()));
                response.getWriter().write(json);
                response.setStatus(200);
            } catch (SQLException e) {
                System.out.println(e.getErrorCode());
                ErrorStatus.setStatus(500,"Database is unavailable ",response);
            }
    }

    @Override
    protected void Post(HttpServletRequest request, HttpServletResponse response) throws ExceptionError, SQLException, IOException {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        if (!Validator.isCurrenciescodeValid(name,code,sign)) {
            throw new ExceptionError("Argument not valid",404);
        }
        try {
            CurrencyDAO.create(name, code, sign);
            response.sendRedirect("/currency/"+code);
        } catch (SQLException ex) {
            if (ex.getErrorCode()==19){
                throw new ExceptionError("Currency already exists", 409);
            }
            throw ex;

        }
    }
}