package com.example.demo;

public class ThreadLocalUtil {
    private static ThreadLocal tenanThreadLocal;

    private static ThreadLocal getThreadLocal() {
        if (tenanThreadLocal == null) {
            synchronized (ThreadLocalUtil.class) {
                if (tenanThreadLocal == null) {
                    tenanThreadLocal = new ThreadLocal<>();
                }
            }
        }
        return tenanThreadLocal;
    }

    public static final synchronized String getTenant() {
        if (getThreadLocal().get() == null) {
//            synchronized (ThreadLocalUtil.class) {
////                if (getThreadLocal().get() == null) {
////                    return "TESTDB";
////                }
////            }
            throw  new RuntimeException("未知租户信息");
        }
        return getThreadLocal().get().toString();
    }

    public static synchronized void setTenant(String scheme) {
        getThreadLocal().set(scheme);
    }
}