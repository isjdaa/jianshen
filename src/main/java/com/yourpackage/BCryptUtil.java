// BCryptUtil.java
import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
