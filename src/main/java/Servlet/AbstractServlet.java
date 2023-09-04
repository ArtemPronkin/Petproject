package Servlet;

import MyException.ExceptionError;
import Utils.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AbstractServlet", value = "/AbstractServlet")
public abstract class AbstractServlet extends HttpServlet {
    protected static ObjectMapper objectMapper = new ObjectMapper();
      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            Get(request,response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            ErrorStatus.setStatus(500,"Database is unavailable ",response);
        } catch (ExceptionError e) {
            ErrorStatus.setStatus(e.getCode(), e.getMessage(),response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Post(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        } catch (ExceptionError e) {
            ErrorStatus.setStatus(e.getCode(), e.getMessage(),response);
        } catch (SQLException e) {
            ErrorStatus.setStatus(500,"Database is unavailable ",response);
        }
    }

    protected abstract void Get (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, ExceptionError;
    protected abstract void Post (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ExceptionError, SQLException;
}