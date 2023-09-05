package info.kgeorgiy.ja.selivanov.walk;

public class Walk implements WalkInterface {

    public static void main(final String... args) {
        WalkCommon.run(args, new Walk());
    }
}
