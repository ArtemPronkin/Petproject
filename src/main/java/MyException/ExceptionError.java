package MyException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionError extends Exception {
    private String message;
    private Integer code;
}
