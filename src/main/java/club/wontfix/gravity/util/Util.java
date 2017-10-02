package club.wontfix.gravity.util;

public class Util {

    private final static RandomString RANDOM_STRING = new RandomString(16);

    public static String generateVerifyID() {
        return RANDOM_STRING.nextString();
    }

}
