package ru.v1as.tg.cat.utils;

public class ThrowableFunctionalInterfacesWrapper {

    private ThrowableFunctionalInterfacesWrapper() {}

    public static Runnable wrapExceptions(ThrowableRunnableInner runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RunnableException(e);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowableRunnableInner {

        void run() throws Exception;
    }

    public static class RunnableException extends RuntimeException {

        public RunnableException(Throwable cause) {
            super(cause);
        }
    }
}
