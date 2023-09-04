package Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.util.HashMap;

public class ErrorStatus {
    @SneakyThrows
    public static void setStatus (int errorCode, String message, HttpServletResponse resp)  {
        resp.setStatus(errorCode);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new HashMap<>(){{put("message", message);}});
        resp.getWriter().write(json);
    }
}
