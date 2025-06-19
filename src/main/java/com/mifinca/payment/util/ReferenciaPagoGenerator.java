
package com.mifinca.payment.util;

public class ReferenciaPagoGenerator {
    public static String generarReferenciaUnica(String prefijo) {
        long timestamp = System.currentTimeMillis();
        int aleatorio = (int)(Math.random() * 100000);
        return prefijo + "-" + timestamp + "-" + aleatorio;
    }
}
