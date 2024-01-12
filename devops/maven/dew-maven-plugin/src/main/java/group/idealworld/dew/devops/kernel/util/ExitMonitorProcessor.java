package group.idealworld.dew.devops.kernel.util;

import java.security.Permission;
import java.util.function.Consumer;

/**
 * The type Exit monitor processor.
 *
 * @author gudaoxuri
 */
public class ExitMonitorProcessor {

    private static Integer EXIT_STATUS = 0;

    /**
     * Hook.
     *
     * @param procFun the proc fun
     */
    public static void hook(Consumer<Integer> procFun) {
        System.setSecurityManager(new ExitMonitorSecurityManager());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            procFun.accept(EXIT_STATUS);
        }));
    }

    private static class ExitMonitorSecurityManager extends SecurityManager {

        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        @Override
        public void checkExit(int status) {
            EXIT_STATUS = status;
            super.checkExit(status);
        }

    }

}
