package com.pnfsoftware.jeb;

import com.pnfsoftware.jeb.client.AbstractClientContext;
import com.pnfsoftware.jeb.client.AbstractContext;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jebglobal.SS;

public class Launcher {
    private static final ILogger SW;

    public Launcher() {
    }

    public static void main(String[] var0) throws Exception {
        Object var1 = null;
        boolean var2 = false;
        byte var3 = 1;
        Object var4 = null;
        Object var5 = null;
        int var6 = 0;
        String[] var7 = var0;
        int var8 = var0.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            if (!var10.equalsIgnoreCase("-c")) {
                if (var10.equalsIgnoreCase("--license")) {
                    System.out.println(AbstractClientContext.generateLicenseInformation());
                    System.exit(0);
                } else {
                    SS var11;
                    if (var10.equalsIgnoreCase("--generate-key")) {
                        var11 = new SS();
                        var11.initialize(var0);
                        var11.start();
                        var11.stop();
                    } else if (var10.equalsIgnoreCase("--check-update")) {
                        var11 = new SS();
                        var11.initialize(var0);
                        var11.start();
                        var11.checkUpdate();
                        var11.stop();
                    } else if (!var10.equals("-h") && !var10.equals("--help")) {
                        System.out.format("Disregarding unknown argument \"%s\"\n", var10);
                    } else {
                        System.out.format("%s v%s - %s (c) %s\n", "JEB", AbstractContext.app_ver, "PNF Software, Inc.", "2015-2018");
                        System.out.println("");
                        System.out.println("Standard options:");
                        System.out.println("  --license           : View license information");
                        System.out.println("  --generate-key      : Generate a license key in headless environments");
                        System.out.println("  --check-update      : Download the latest update (requires an Internet connection)");
                        System.out.println("");
                        System.exit(-1);
                    }
                }
            }

            ++var6;
        }

        if (var1 != null) {
            if (var3 == 1) {
                com.pnfsoftware.jeb.SS var12 = new com.pnfsoftware.jeb.SS(var2, (String[])var5);
                var12.SW((String)var1, (String)var4);
                var12.SW();
            } else if (var3 == 2) {
                Nq var13 = new Nq(var2, (String)var1, (String)var4, (String[])var5);
                var13.initialize(var0);
                var13.start();
                var13.stop();
            }
        }
        SW.info("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");

    }

    static {
        GlobalLog.addDestinationStream(System.out);
        SW = GlobalLog.getLogger(Launcher.class);
    }
}
