package ph.edu.dlsu.lbycpob.memorymatch.ui;

/**
 * Classpath-friendly launcher
 * for IntelliJ and Maven.
 */
// UNDERSTAND: A tiny wrapper class whose only job is to call MainApp.main().
// DECISION: This separate Launcher class was added instead of running MainApp directly, because
// MainApp extends javafx.application.Application — some IDE/Maven setups fail to launch a class that
// extends Application directly from the classpath, so a plain non-Application class is used as the
// actual entry point instead.
public class Launcher {

    public static void main(String[] args) {
        MainApp.main(args);
    }
}