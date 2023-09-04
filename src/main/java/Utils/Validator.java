package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
        public static boolean isExchangeRatesCodeValid(String bcode , String tcode , String rate){
            return !(bcode == null || tcode == null || rate == null ||
                    tcode.length()!=3 || bcode.length()!=3);
        }
        public static boolean isCodeValid(String code) {
            if (code == null) return false;
            return code.matches("[A-Z]{3}");
        }

        public static boolean isCodePairValid(String code) {
            if (code == null)
                return false;
            Pattern pattern = Pattern.compile("[A-Z]{6}");
            Matcher matcher = pattern.matcher(code);

            return matcher.matches() && !code.substring(0, 3).equals(code.substring(3));
        }
    public static boolean isCurrenciescodeValid(String name , String code , String sign){
        return name != null || isCodeValid(code) || sign != null;

    }
}
