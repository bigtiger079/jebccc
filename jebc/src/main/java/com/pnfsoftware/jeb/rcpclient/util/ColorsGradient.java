/*     */
package com.pnfsoftware.jeb.rcpclient.util;
/*     */
/*     */

import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class ColorsGradient
        /*     */ {
    /*  27 */   private static final Map<String, Integer> colors = new LinkedHashMap();

    /*     */
    /*  29 */   static {
        colors.put("indian red", Integer.valueOf(11540255));
        /*  30 */
        colors.put("crimson", Integer.valueOf(14423100));
        /*  31 */
        colors.put("lightpink", Integer.valueOf(16758465));
        /*  32 */
        colors.put("lightpink 1", Integer.valueOf(16756409));
        /*  33 */
        colors.put("lightpink 2", Integer.valueOf(15639213));
        /*  34 */
        colors.put("lightpink 3", Integer.valueOf(13470869));
        /*  35 */
        colors.put("lightpink 4", Integer.valueOf(9133925));
        /*  36 */
        colors.put("pink", Integer.valueOf(16761035));
        /*  37 */
        colors.put("pink 1", Integer.valueOf(16758213));
        /*  38 */
        colors.put("pink 2", Integer.valueOf(15641016));
        /*  39 */
        colors.put("pink 3", Integer.valueOf(13472158));
        /*  40 */
        colors.put("pink 4", Integer.valueOf(9134956));
        /*  41 */
        colors.put("palevioletred", Integer.valueOf(14381203));
        /*  42 */
        colors.put("palevioletred 1", Integer.valueOf(16745131));
        /*  43 */
        colors.put("palevioletred 2", Integer.valueOf(15628703));
        /*  44 */
        colors.put("palevioletred 3", Integer.valueOf(13461641));
        /*  45 */
        colors.put("palevioletred 4", Integer.valueOf(9127773));
        /*  46 */
        colors.put("lavenderblush 1 (lavenderblush)", Integer.valueOf(16773365));
        /*  47 */
        colors.put("lavenderblush 2", Integer.valueOf(15655141));
        /*  48 */
        colors.put("lavenderblush 3", Integer.valueOf(13484485));
        /*  49 */
        colors.put("lavenderblush 4", Integer.valueOf(9143174));
        /*  50 */
        colors.put("violetred 1", Integer.valueOf(16727702));
        /*  51 */
        colors.put("violetred 2", Integer.valueOf(15612556));
        /*  52 */
        colors.put("violetred 3", Integer.valueOf(13447800));
        /*  53 */
        colors.put("violetred 4", Integer.valueOf(9118290));
        /*  54 */
        colors.put("hotpink", Integer.valueOf(16738740));
        /*  55 */
        colors.put("hotpink 1", Integer.valueOf(16740020));
        /*  56 */
        colors.put("hotpink 2", Integer.valueOf(15624871));
        /*  57 */
        colors.put("hotpink 3", Integer.valueOf(13459600));
        /*  58 */
        colors.put("hotpink 4", Integer.valueOf(9124450));
        /*  59 */
        colors.put("raspberry", Integer.valueOf(8857175));
        /*  60 */
        colors.put("deeppink 1 (deeppink)", Integer.valueOf(16716947));
        /*  61 */
        colors.put("deeppink 2", Integer.valueOf(15602313));
        /*  62 */
        colors.put("deeppink 3", Integer.valueOf(13439094));
        /*  63 */
        colors.put("deeppink 4", Integer.valueOf(9112144));
        /*  64 */
        colors.put("maroon 1", Integer.valueOf(16725171));
        /*  65 */
        colors.put("maroon 2", Integer.valueOf(15610023));
        /*  66 */
        colors.put("maroon 3", Integer.valueOf(13445520));
        /*  67 */
        colors.put("maroon 4", Integer.valueOf(9116770));
        /*  68 */
        colors.put("mediumvioletred", Integer.valueOf(13047173));
        /*  69 */
        colors.put("violetred", Integer.valueOf(13639824));
        /*  70 */
        colors.put("orchid", Integer.valueOf(14315734));
        /*  71 */
        colors.put("orchid 1", Integer.valueOf(16745466));
        /*  72 */
        colors.put("orchid 2", Integer.valueOf(15629033));
        /*  73 */
        colors.put("orchid 3", Integer.valueOf(13461961));
        /*  74 */
        colors.put("orchid 4", Integer.valueOf(9127817));
        /*  75 */
        colors.put("thistle", Integer.valueOf(14204888));
        /*  76 */
        colors.put("thistle 1", Integer.valueOf(16769535));
        /*  77 */
        colors.put("thistle 2", Integer.valueOf(15651566));
        /*  78 */
        colors.put("thistle 3", Integer.valueOf(13481421));
        /*  79 */
        colors.put("thistle 4", Integer.valueOf(9141131));
        /*  80 */
        colors.put("plum 1", Integer.valueOf(16759807));
        /*  81 */
        colors.put("plum 2", Integer.valueOf(15642350));
        /*  82 */
        colors.put("plum 3", Integer.valueOf(13473485));
        /*  83 */
        colors.put("plum 4", Integer.valueOf(9135755));
        /*  84 */
        colors.put("plum", Integer.valueOf(14524637));
        /*  85 */
        colors.put("violet", Integer.valueOf(15631086));
        /*  86 */
        colors.put("magenta (fuchsia*)", Integer.valueOf(16711935));
        /*  87 */
        colors.put("magenta*", Integer.valueOf(16711935));
        /*  88 */
        colors.put("magenta 2", Integer.valueOf(15597806));
        /*  89 */
        colors.put("magenta 3", Integer.valueOf(13435085));
        /*  90 */
        colors.put("magenta 4 (darkmagenta)", Integer.valueOf(9109643));
        /*  91 */
        colors.put("purple*", Integer.valueOf(8388736));
        /*  92 */
        colors.put("mediumorchid", Integer.valueOf(12211667));
        /*  93 */
        colors.put("mediumorchid 1", Integer.valueOf(14706431));
        /*  94 */
        colors.put("mediumorchid 2", Integer.valueOf(13721582));
        /*  95 */
        colors.put("mediumorchid 3", Integer.valueOf(11817677));
        /*  96 */
        colors.put("mediumorchid 4", Integer.valueOf(8009611));
        /*  97 */
        colors.put("darkviolet", Integer.valueOf(9699539));
        /*  98 */
        colors.put("darkorchid", Integer.valueOf(10040012));
        /*  99 */
        colors.put("darkorchid 1", Integer.valueOf(12533503));
        /* 100 */
        colors.put("darkorchid 2", Integer.valueOf(11680494));
        /* 101 */
        colors.put("darkorchid 3", Integer.valueOf(10105549));
        /* 102 */
        colors.put("darkorchid 4", Integer.valueOf(6824587));
        /* 103 */
        colors.put("indigo", Integer.valueOf(4915330));
        /* 104 */
        colors.put("blueviolet", Integer.valueOf(9055202));
        /* 105 */
        colors.put("purple 1", Integer.valueOf(10170623));
        /* 106 */
        colors.put("purple 2", Integer.valueOf(9514222));
        /* 107 */
        colors.put("purple 3", Integer.valueOf(8201933));
        /* 108 */
        colors.put("purple 4", Integer.valueOf(5577355));
        /* 109 */
        colors.put("mediumpurple", Integer.valueOf(9662683));
        /* 110 */
        colors.put("mediumpurple 1", Integer.valueOf(11240191));
        /* 111 */
        colors.put("mediumpurple 2", Integer.valueOf(10451438));
        /* 112 */
        colors.put("mediumpurple 3", Integer.valueOf(9005261));
        /* 113 */
        colors.put("mediumpurple 4", Integer.valueOf(6113163));
        /* 114 */
        colors.put("darkslateblue", Integer.valueOf(4734347));
        /* 115 */
        colors.put("lightslateblue", Integer.valueOf(8679679));
        /* 116 */
        colors.put("mediumslateblue", Integer.valueOf(8087790));
        /* 117 */
        colors.put("slateblue", Integer.valueOf(6970061));
        /* 118 */
        colors.put("slateblue 1", Integer.valueOf(8613887));
        /* 119 */
        colors.put("slateblue 2", Integer.valueOf(8021998));
        /* 120 */
        colors.put("slateblue 3", Integer.valueOf(6904269));
        /* 121 */
        colors.put("slateblue 4", Integer.valueOf(4668555));
        /* 122 */
        colors.put("ghostwhite", Integer.valueOf(16316671));
        /* 123 */
        colors.put("lavender", Integer.valueOf(15132410));
        /* 124 */
        colors.put("blue*", Integer.valueOf(255));
        /* 125 */
        colors.put("blue 2", Integer.valueOf(238));
        /* 126 */
        colors.put("blue 3 (mediumblue)", Integer.valueOf(205));
        /* 127 */
        colors.put("blue 4 (darkblue)", Integer.valueOf(139));
        /* 128 */
        colors.put("navy*", Integer.valueOf(128));
        /* 129 */
        colors.put("midnightblue", Integer.valueOf(1644912));
        /* 130 */
        colors.put("cobalt", Integer.valueOf(4020651));
        /* 131 */
        colors.put("royalblue", Integer.valueOf(4286945));
        /* 132 */
        colors.put("royalblue 1", Integer.valueOf(4749055));
        /* 133 */
        colors.put("royalblue 2", Integer.valueOf(4419310));
        /* 134 */
        colors.put("royalblue 3", Integer.valueOf(3825613));
        /* 135 */
        colors.put("royalblue 4", Integer.valueOf(2572427));
        /* 136 */
        colors.put("cornflowerblue", Integer.valueOf(6591981));
        /* 137 */
        colors.put("lightsteelblue", Integer.valueOf(11584734));
        /* 138 */
        colors.put("lightsteelblue 1", Integer.valueOf(13296127));
        /* 139 */
        colors.put("lightsteelblue 2", Integer.valueOf(12374766));
        /* 140 */
        colors.put("lightsteelblue 3", Integer.valueOf(10663373));
        /* 141 */
        colors.put("lightsteelblue 4", Integer.valueOf(7240587));
        /* 142 */
        colors.put("lightslategray", Integer.valueOf(7833753));
        /* 143 */
        colors.put("slategray", Integer.valueOf(7372944));
        /* 144 */
        colors.put("slategray 1", Integer.valueOf(13034239));
        /* 145 */
        colors.put("slategray 2", Integer.valueOf(12178414));
        /* 146 */
        colors.put("slategray 3", Integer.valueOf(10467021));
        /* 147 */
        colors.put("slategray 4", Integer.valueOf(7109515));
        /* 148 */
        colors.put("dodgerblue 1 (dodgerblue)", Integer.valueOf(2003199));
        /* 149 */
        colors.put("dodgerblue 2", Integer.valueOf(1869550));
        /* 150 */
        colors.put("dodgerblue 3", Integer.valueOf(1602765));
        /* 151 */
        colors.put("dodgerblue 4", Integer.valueOf(1068683));
        /* 152 */
        colors.put("aliceblue", Integer.valueOf(15792383));
        /* 153 */
        colors.put("steelblue", Integer.valueOf(4620980));
        /* 154 */
        colors.put("steelblue 1", Integer.valueOf(6535423));
        /* 155 */
        colors.put("steelblue 2", Integer.valueOf(6073582));
        /* 156 */
        colors.put("steelblue 3", Integer.valueOf(5215437));
        /* 157 */
        colors.put("steelblue 4", Integer.valueOf(3564683));
        /* 158 */
        colors.put("lightskyblue", Integer.valueOf(8900346));
        /* 159 */
        colors.put("lightskyblue 1", Integer.valueOf(11592447));
        /* 160 */
        colors.put("lightskyblue 2", Integer.valueOf(10802158));
        /* 161 */
        colors.put("lightskyblue 3", Integer.valueOf(9287373));
        /* 162 */
        colors.put("lightskyblue 4", Integer.valueOf(6323083));
        /* 163 */
        colors.put("skyblue 1", Integer.valueOf(8900351));
        /* 164 */
        colors.put("skyblue 2", Integer.valueOf(8306926));
        /* 165 */
        colors.put("skyblue 3", Integer.valueOf(7120589));
        /* 166 */
        colors.put("skyblue 4", Integer.valueOf(4878475));
        /* 167 */
        colors.put("skyblue", Integer.valueOf(8900331));
        /* 168 */
        colors.put("deepskyblue 1 (deepskyblue)", Integer.valueOf(49151));
        /* 169 */
        colors.put("deepskyblue 2", Integer.valueOf(45806));
        /* 170 */
        colors.put("deepskyblue 3", Integer.valueOf(39629));
        /* 171 */
        colors.put("deepskyblue 4", Integer.valueOf(26763));
        /* 172 */
        colors.put("peacock", Integer.valueOf(3383753));
        /* 173 */
        colors.put("lightblue", Integer.valueOf(11393254));
        /* 174 */
        colors.put("lightblue 1", Integer.valueOf(12578815));
        /* 175 */
        colors.put("lightblue 2", Integer.valueOf(11722734));
        /* 176 */
        colors.put("lightblue 3", Integer.valueOf(10141901));
        /* 177 */
        colors.put("lightblue 4", Integer.valueOf(6849419));
        /* 178 */
        colors.put("powderblue", Integer.valueOf(11591910));
        /* 179 */
        colors.put("cadetblue 1", Integer.valueOf(10024447));
        /* 180 */
        colors.put("cadetblue 2", Integer.valueOf(9364974));
        /* 181 */
        colors.put("cadetblue 3", Integer.valueOf(8046029));
        /* 182 */
        colors.put("cadetblue 4", Integer.valueOf(5473931));
        /* 183 */
        colors.put("turquoise 1", Integer.valueOf(62975));
        /* 184 */
        colors.put("turquoise 2", Integer.valueOf(58862));
        /* 185 */
        colors.put("turquoise 3", Integer.valueOf(50637));
        /* 186 */
        colors.put("turquoise 4", Integer.valueOf(34443));
        /* 187 */
        colors.put("cadetblue", Integer.valueOf(6266528));
        /* 188 */
        colors.put("darkturquoise", Integer.valueOf(52945));
        /* 189 */
        colors.put("azure 1 (azure)", Integer.valueOf(15794175));
        /* 190 */
        colors.put("azure 2", Integer.valueOf(14741230));
        /* 191 */
        colors.put("azure 3", Integer.valueOf(12701133));
        /* 192 */
        colors.put("azure 4", Integer.valueOf(8620939));
        /* 193 */
        colors.put("lightcyan 1 (lightcyan)", Integer.valueOf(14745599));
        /* 194 */
        colors.put("lightcyan 2", Integer.valueOf(13758190));
        /* 195 */
        colors.put("lightcyan 3", Integer.valueOf(11849165));
        /* 196 */
        colors.put("lightcyan 4", Integer.valueOf(8031115));
        /* 197 */
        colors.put("paleturquoise 1", Integer.valueOf(12320767));
        /* 198 */
        colors.put("paleturquoise 2 (paleturquoise)", Integer.valueOf(11464430));
        /* 199 */
        colors.put("paleturquoise 3", Integer.valueOf(9883085));
        /* 200 */
        colors.put("paleturquoise 4", Integer.valueOf(6720395));
        /* 201 */
        colors.put("darkslategray", Integer.valueOf(3100495));
        /* 202 */
        colors.put("darkslategray 1", Integer.valueOf(9961471));
        /* 203 */
        colors.put("darkslategray 2", Integer.valueOf(9301742));
        /* 204 */
        colors.put("darkslategray 3", Integer.valueOf(7982541));
        /* 205 */
        colors.put("darkslategray 4", Integer.valueOf(5409675));
        /* 206 */
        colors.put("cyan / aqua*", Integer.valueOf(65535));
        /* 207 */
        colors.put("cyan*", Integer.valueOf(65535));
        /* 208 */
        colors.put("cyan 2", Integer.valueOf(61166));
        /* 209 */
        colors.put("cyan 3", Integer.valueOf(52685));
        /* 210 */
        colors.put("cyan 4 (darkcyan)", Integer.valueOf(35723));
        /* 211 */
        colors.put("teal*", Integer.valueOf(32896));
        /* 212 */
        colors.put("mediumturquoise", Integer.valueOf(4772300));
        /* 213 */
        colors.put("lightseagreen", Integer.valueOf(2142890));
        /* 214 */
        colors.put("manganeseblue", Integer.valueOf(239774));
        /* 215 */
        colors.put("turquoise", Integer.valueOf(4251856));
        /* 216 */
        colors.put("coldgrey", Integer.valueOf(8424071));
        /* 217 */
        colors.put("turquoiseblue", Integer.valueOf(51084));
        /* 218 */
        colors.put("aquamarine 1 (aquamarine)", Integer.valueOf(8388564));
        /* 219 */
        colors.put("aquamarine 2", Integer.valueOf(7794374));
        /* 220 */
        colors.put("aquamarine 3 (mediumaquamarine)", Integer.valueOf(6737322));
        /* 221 */
        colors.put("aquamarine 4", Integer.valueOf(4557684));
        /* 222 */
        colors.put("mediumspringgreen", Integer.valueOf(64154));
        /* 223 */
        colors.put("mintcream", Integer.valueOf(16121850));
        /* 224 */
        colors.put("springgreen", Integer.valueOf(65407));
        /* 225 */
        colors.put("springgreen 1", Integer.valueOf(61046));
        /* 226 */
        colors.put("springgreen 2", Integer.valueOf(52582));
        /* 227 */
        colors.put("springgreen 3", Integer.valueOf(35653));
        /* 228 */
        colors.put("mediumseagreen", Integer.valueOf(3978097));
        /* 229 */
        colors.put("seagreen 1", Integer.valueOf(5570463));
        /* 230 */
        colors.put("seagreen 2", Integer.valueOf(5172884));
        /* 231 */
        colors.put("seagreen 3", Integer.valueOf(4443520));
        /* 232 */
        colors.put("seagreen 4 (seagreen)", Integer.valueOf(3050327));
        /* 233 */
        colors.put("emeraldgreen", Integer.valueOf(51543));
        /* 234 */
        colors.put("mint", Integer.valueOf(12451017));
        /* 235 */
        colors.put("cobaltgreen", Integer.valueOf(4034880));
        /* 236 */
        colors.put("honeydew 1 (honeydew)", Integer.valueOf(15794160));
        /* 237 */
        colors.put("honeydew 2", Integer.valueOf(14741216));
        /* 238 */
        colors.put("honeydew 3", Integer.valueOf(12701121));
        /* 239 */
        colors.put("honeydew 4", Integer.valueOf(8620931));
        /* 240 */
        colors.put("darkseagreen", Integer.valueOf(9419919));
        /* 241 */
        colors.put("darkseagreen 1", Integer.valueOf(12713921));
        /* 242 */
        colors.put("darkseagreen 2", Integer.valueOf(11857588));
        /* 243 */
        colors.put("darkseagreen 3", Integer.valueOf(10210715));
        /* 244 */
        colors.put("darkseagreen 4", Integer.valueOf(6916969));
        /* 245 */
        colors.put("palegreen", Integer.valueOf(10025880));
        /* 246 */
        colors.put("palegreen 1", Integer.valueOf(10157978));
        /* 247 */
        colors.put("palegreen 2 (lightgreen)", Integer.valueOf(9498256));
        /* 248 */
        colors.put("palegreen 3", Integer.valueOf(8179068));
        /* 249 */
        colors.put("palegreen 4", Integer.valueOf(5540692));
        /* 250 */
        colors.put("limegreen", Integer.valueOf(3329330));
        /* 251 */
        colors.put("forestgreen", Integer.valueOf(2263842));
        /* 252 */
        colors.put("green 1 (lime*)", Integer.valueOf(65280));
        /* 253 */
        colors.put("lime*", Integer.valueOf(65280));
        /* 254 */
        colors.put("green 2", Integer.valueOf(60928));
        /* 255 */
        colors.put("green 3", Integer.valueOf(52480));
        /* 256 */
        colors.put("green 4", Integer.valueOf(35584));
        /* 257 */
        colors.put("green*", Integer.valueOf(32768));
        /* 258 */
        colors.put("darkgreen", Integer.valueOf(25600));
        /* 259 */
        colors.put("sapgreen", Integer.valueOf(3178516));
        /* 260 */
        colors.put("lawngreen", Integer.valueOf(8190976));
        /* 261 */
        colors.put("chartreuse 1 (chartreuse)", Integer.valueOf(8388352));
        /* 262 */
        colors.put("chartreuse 2", Integer.valueOf(7794176));
        /* 263 */
        colors.put("chartreuse 3", Integer.valueOf(6737152));
        /* 264 */
        colors.put("chartreuse 4", Integer.valueOf(4557568));
        /* 265 */
        colors.put("greenyellow", Integer.valueOf(11403055));
        /* 266 */
        colors.put("darkolivegreen 1", Integer.valueOf(13303664));
        /* 267 */
        colors.put("darkolivegreen 2", Integer.valueOf(12381800));
        /* 268 */
        colors.put("darkolivegreen 3", Integer.valueOf(10669402));
        /* 269 */
        colors.put("darkolivegreen 4", Integer.valueOf(7244605));
        /* 270 */
        colors.put("darkolivegreen", Integer.valueOf(5597999));
        /* 271 */
        colors.put("olivedrab", Integer.valueOf(7048739));
        /* 272 */
        colors.put("olivedrab 1", Integer.valueOf(12648254));
        /* 273 */
        colors.put("olivedrab 2", Integer.valueOf(11791930));
        /* 274 */
        colors.put("olivedrab 3 (yellowgreen)", Integer.valueOf(10145074));
        /* 275 */
        colors.put("olivedrab 4", Integer.valueOf(6916898));
        /* 276 */
        colors.put("ivory 1 (ivory)", Integer.valueOf(16777200));
        /* 277 */
        colors.put("ivory 2", Integer.valueOf(15658720));
        /* 278 */
        colors.put("ivory 3", Integer.valueOf(13487553));
        /* 279 */
        colors.put("ivory 4", Integer.valueOf(9145219));
        /* 280 */
        colors.put("beige", Integer.valueOf(16119260));
        /* 281 */
        colors.put("lightyellow 1 (lightyellow)", Integer.valueOf(16777184));
        /* 282 */
        colors.put("lightyellow 2", Integer.valueOf(15658705));
        /* 283 */
        colors.put("lightyellow 3", Integer.valueOf(13487540));
        /* 284 */
        colors.put("lightyellow 4", Integer.valueOf(9145210));
        /* 285 */
        colors.put("lightgoldenrodyellow", Integer.valueOf(16448210));
        /* 286 */
        colors.put("yellow 1 (yellow*)", Integer.valueOf(16776960));
        /* 287 */
        colors.put("yellow*", Integer.valueOf(16776960));
        /* 288 */
        colors.put("yellow 2", Integer.valueOf(15658496));
        /* 289 */
        colors.put("yellow 3", Integer.valueOf(13487360));
        /* 290 */
        colors.put("yellow 4", Integer.valueOf(9145088));
        /* 291 */
        colors.put("warmgrey", Integer.valueOf(8421481));
        /* 292 */
        colors.put("olive*", Integer.valueOf(8421376));
        /* 293 */
        colors.put("darkkhaki", Integer.valueOf(12433259));
        /* 294 */
        colors.put("khaki 1", Integer.valueOf(16774799));
        /* 295 */
        colors.put("khaki 2", Integer.valueOf(15656581));
        /* 296 */
        colors.put("khaki 3", Integer.valueOf(13485683));
        /* 297 */
        colors.put("khaki 4", Integer.valueOf(9143886));
        /* 298 */
        colors.put("khaki", Integer.valueOf(15787660));
        /* 299 */
        colors.put("palegoldenrod", Integer.valueOf(15657130));
        /* 300 */
        colors.put("lemonchiffon 1 (lemonchiffon)", Integer.valueOf(16775885));
        /* 301 */
        colors.put("lemonchiffon 2", Integer.valueOf(15657407));
        /* 302 */
        colors.put("lemonchiffon 3", Integer.valueOf(13486501));
        /* 303 */
        colors.put("lemonchiffon 4", Integer.valueOf(9144688));
        /* 304 */
        colors.put("lightgoldenrod 1", Integer.valueOf(16772235));
        /* 305 */
        colors.put("lightgoldenrod 2", Integer.valueOf(15654018));
        /* 306 */
        colors.put("lightgoldenrod 3", Integer.valueOf(13483632));
        /* 307 */
        colors.put("lightgoldenrod 4", Integer.valueOf(9142604));
        /* 308 */
        colors.put("banana", Integer.valueOf(14929751));
        /* 309 */
        colors.put("gold 1 (gold)", Integer.valueOf(16766720));
        /* 310 */
        colors.put("gold 2", Integer.valueOf(15649024));
        /* 311 */
        colors.put("gold 3", Integer.valueOf(13479168));
        /* 312 */
        colors.put("gold 4", Integer.valueOf(9139456));
        /* 313 */
        colors.put("cornsilk 1 (cornsilk)", Integer.valueOf(16775388));
        /* 314 */
        colors.put("cornsilk 2", Integer.valueOf(15657165));
        /* 315 */
        colors.put("cornsilk 3", Integer.valueOf(13486257));
        /* 316 */
        colors.put("cornsilk 4", Integer.valueOf(9144440));
        /* 317 */
        colors.put("goldenrod", Integer.valueOf(14329120));
        /* 318 */
        colors.put("goldenrod 1", Integer.valueOf(16761125));
        /* 319 */
        colors.put("goldenrod 2", Integer.valueOf(15643682));
        /* 320 */
        colors.put("goldenrod 3", Integer.valueOf(13474589));
        /* 321 */
        colors.put("goldenrod 4", Integer.valueOf(9136404));
        /* 322 */
        colors.put("darkgoldenrod", Integer.valueOf(12092939));
        /* 323 */
        colors.put("darkgoldenrod 1", Integer.valueOf(16759055));
        /* 324 */
        colors.put("darkgoldenrod 2", Integer.valueOf(15641870));
        /* 325 */
        colors.put("darkgoldenrod 3", Integer.valueOf(13473036));
        /* 326 */
        colors.put("darkgoldenrod 4", Integer.valueOf(9135368));
        /* 327 */
        colors.put("orange 1 (orange)", Integer.valueOf(16753920));
        /* 328 */
        colors.put("orange 2", Integer.valueOf(15636992));
        /* 329 */
        colors.put("orange 3", Integer.valueOf(13468928));
        /* 330 */
        colors.put("orange 4", Integer.valueOf(9132544));
        /* 331 */
        colors.put("floralwhite", Integer.valueOf(16775920));
        /* 332 */
        colors.put("oldlace", Integer.valueOf(16643558));
        /* 333 */
        colors.put("wheat", Integer.valueOf(16113331));
        /* 334 */
        colors.put("wheat 1", Integer.valueOf(16771002));
        /* 335 */
        colors.put("wheat 2", Integer.valueOf(15653038));
        /* 336 */
        colors.put("wheat 3", Integer.valueOf(13482646));
        /* 337 */
        colors.put("wheat 4", Integer.valueOf(9141862));
        /* 338 */
        colors.put("moccasin", Integer.valueOf(16770229));
        /* 339 */
        colors.put("papayawhip", Integer.valueOf(16773077));
        /* 340 */
        colors.put("blanchedalmond", Integer.valueOf(16772045));
        /* 341 */
        colors.put("navajowhite 1 (navajowhite)", Integer.valueOf(16768685));
        /* 342 */
        colors.put("navajowhite 2", Integer.valueOf(15650721));
        /* 343 */
        colors.put("navajowhite 3", Integer.valueOf(13480843));
        /* 344 */
        colors.put("navajowhite 4", Integer.valueOf(9140574));
        /* 345 */
        colors.put("eggshell", Integer.valueOf(16574153));
        /* 346 */
        colors.put("tan", Integer.valueOf(13808780));
        /* 347 */
        colors.put("brick", Integer.valueOf(10249759));
        /* 348 */
        colors.put("cadmiumyellow", Integer.valueOf(16750866));
        /* 349 */
        colors.put("antiquewhite", Integer.valueOf(16444375));
        /* 350 */
        colors.put("antiquewhite 1", Integer.valueOf(16773083));
        /* 351 */
        colors.put("antiquewhite 2", Integer.valueOf(15654860));
        /* 352 */
        colors.put("antiquewhite 3", Integer.valueOf(13484208));
        /* 353 */
        colors.put("antiquewhite 4", Integer.valueOf(9143160));
        /* 354 */
        colors.put("burlywood", Integer.valueOf(14596231));
        /* 355 */
        colors.put("burlywood 1", Integer.valueOf(16765851));
        /* 356 */
        colors.put("burlywood 2", Integer.valueOf(15648145));
        /* 357 */
        colors.put("burlywood 3", Integer.valueOf(13478525));
        /* 358 */
        colors.put("burlywood 4", Integer.valueOf(9139029));
        /* 359 */
        colors.put("bisque 1 (bisque)", Integer.valueOf(16770244));
        /* 360 */
        colors.put("bisque 2", Integer.valueOf(15652279));
        /* 361 */
        colors.put("bisque 3", Integer.valueOf(13481886));
        /* 362 */
        colors.put("bisque 4", Integer.valueOf(9141611));
        /* 363 */
        colors.put("melon", Integer.valueOf(14919785));
        /* 364 */
        colors.put("carrot", Integer.valueOf(15569185));
        /* 365 */
        colors.put("darkorange", Integer.valueOf(16747520));
        /* 366 */
        colors.put("darkorange 1", Integer.valueOf(16744192));
        /* 367 */
        colors.put("darkorange 2", Integer.valueOf(15627776));
        /* 368 */
        colors.put("darkorange 3", Integer.valueOf(13460992));
        /* 369 */
        colors.put("darkorange 4", Integer.valueOf(9127168));
        /* 370 */
        colors.put("orange", Integer.valueOf(16744448));
        /* 371 */
        colors.put("tan 1", Integer.valueOf(16753999));
        /* 372 */
        colors.put("tan 2", Integer.valueOf(15637065));
        /* 373 */
        colors.put("tan 3 (peru)", Integer.valueOf(13468991));
        /* 374 */
        colors.put("tan 4", Integer.valueOf(9132587));
        /* 375 */
        colors.put("linen", Integer.valueOf(16445670));
        /* 376 */
        colors.put("peachpuff 1 (peachpuff)", Integer.valueOf(16767673));
        /* 377 */
        colors.put("peachpuff 2", Integer.valueOf(15649709));
        /* 378 */
        colors.put("peachpuff 3", Integer.valueOf(13479829));
        /* 379 */
        colors.put("peachpuff 4", Integer.valueOf(9140069));
        /* 380 */
        colors.put("seashell 1 (seashell)", Integer.valueOf(16774638));
        /* 381 */
        colors.put("seashell 2", Integer.valueOf(15656414));
        /* 382 */
        colors.put("seashell 3", Integer.valueOf(13485503));
        /* 383 */
        colors.put("seashell 4", Integer.valueOf(9143938));
        /* 384 */
        colors.put("sandybrown", Integer.valueOf(16032864));
        /* 385 */
        colors.put("rawsienna", Integer.valueOf(13066516));
        /* 386 */
        colors.put("chocolate", Integer.valueOf(13789470));
        /* 387 */
        colors.put("chocolate 1", Integer.valueOf(16744228));
        /* 388 */
        colors.put("chocolate 2", Integer.valueOf(15627809));
        /* 389 */
        colors.put("chocolate 3", Integer.valueOf(13461021));
        /* 390 */
        colors.put("chocolate 4 (saddlebrown)", Integer.valueOf(9127187));
        /* 391 */
        colors.put("ivoryblack", Integer.valueOf(2696225));
        /* 392 */
        colors.put("flesh", Integer.valueOf(16743744));
        /* 393 */
        colors.put("cadmiumorange", Integer.valueOf(16736515));
        /* 394 */
        colors.put("burntsienna", Integer.valueOf(9057807));
        /* 395 */
        colors.put("sienna", Integer.valueOf(10506797));
        /* 396 */
        colors.put("sienna 1", Integer.valueOf(16745031));
        /* 397 */
        colors.put("sienna 2", Integer.valueOf(15628610));
        /* 398 */
        colors.put("sienna 3", Integer.valueOf(13461561));
        /* 399 */
        colors.put("sienna 4", Integer.valueOf(9127718));
        /* 400 */
        colors.put("lightsalmon 1 (lightsalmon)", Integer.valueOf(16752762));
        /* 401 */
        colors.put("lightsalmon 2", Integer.valueOf(15635826));
        /* 402 */
        colors.put("lightsalmon 3", Integer.valueOf(13468002));
        /* 403 */
        colors.put("lightsalmon 4", Integer.valueOf(9131842));
        /* 404 */
        colors.put("coral", Integer.valueOf(16744272));
        /* 405 */
        colors.put("orangered 1 (orangered)", Integer.valueOf(16729344));
        /* 406 */
        colors.put("orangered 2", Integer.valueOf(15613952));
        /* 407 */
        colors.put("orangered 3", Integer.valueOf(13448960));
        /* 408 */
        colors.put("orangered 4", Integer.valueOf(9118976));
        /* 409 */
        colors.put("sepia", Integer.valueOf(6170130));
        /* 410 */
        colors.put("darksalmon", Integer.valueOf(15308410));
        /* 411 */
        colors.put("salmon 1", Integer.valueOf(16747625));
        /* 412 */
        colors.put("salmon 2", Integer.valueOf(15630946));
        /* 413 */
        colors.put("salmon 3", Integer.valueOf(13463636));
        /* 414 */
        colors.put("salmon 4", Integer.valueOf(9129017));
        /* 415 */
        colors.put("coral 1", Integer.valueOf(16740950));
        /* 416 */
        colors.put("coral 2", Integer.valueOf(15624784));
        /* 417 */
        colors.put("coral 3", Integer.valueOf(13458245));
        /* 418 */
        colors.put("coral 4", Integer.valueOf(9125423));
        /* 419 */
        colors.put("burntumber", Integer.valueOf(9057060));
        /* 420 */
        colors.put("tomato 1 (tomato)", Integer.valueOf(16737095));
        /* 421 */
        colors.put("tomato 2", Integer.valueOf(15621186));
        /* 422 */
        colors.put("tomato 3", Integer.valueOf(13455161));
        /* 423 */
        colors.put("tomato 4", Integer.valueOf(9123366));
        /* 424 */
        colors.put("salmon", Integer.valueOf(16416882));
        /* 425 */
        colors.put("mistyrose 1 (mistyrose)", Integer.valueOf(16770273));
        /* 426 */
        colors.put("mistyrose 2", Integer.valueOf(15652306));
        /* 427 */
        colors.put("mistyrose 3", Integer.valueOf(13481909));
        /* 428 */
        colors.put("mistyrose 4", Integer.valueOf(9141627));
        /* 429 */
        colors.put("snow 1 (snow)", Integer.valueOf(16775930));
        /* 430 */
        colors.put("snow 2", Integer.valueOf(15657449));
        /* 431 */
        colors.put("snow 3", Integer.valueOf(13486537));
        /* 432 */
        colors.put("snow 4", Integer.valueOf(9144713));
        /* 433 */
        colors.put("rosybrown", Integer.valueOf(12357519));
        /* 434 */
        colors.put("rosybrown 1", Integer.valueOf(16761281));
        /* 435 */
        colors.put("rosybrown 2", Integer.valueOf(15643828));
        /* 436 */
        colors.put("rosybrown 3", Integer.valueOf(13474715));
        /* 437 */
        colors.put("rosybrown 4", Integer.valueOf(9136489));
        /* 438 */
        colors.put("lightcoral", Integer.valueOf(15761536));
        /* 439 */
        colors.put("indianred", Integer.valueOf(13458524));
        /* 440 */
        colors.put("indianred 1", Integer.valueOf(16738922));
        /* 441 */
        colors.put("indianred 2", Integer.valueOf(15623011));
        /* 442 */
        colors.put("indianred 4", Integer.valueOf(9124410));
        /* 443 */
        colors.put("indianred 3", Integer.valueOf(13456725));
        /* 444 */
        colors.put("brown", Integer.valueOf(10824234));
        /* 445 */
        colors.put("brown 1", Integer.valueOf(16728128));
        /* 446 */
        colors.put("brown 2", Integer.valueOf(15612731));
        /* 447 */
        colors.put("brown 3", Integer.valueOf(13447987));
        /* 448 */
        colors.put("brown 4", Integer.valueOf(9118499));
        /* 449 */
        colors.put("firebrick", Integer.valueOf(11674146));
        /* 450 */
        colors.put("firebrick 1", Integer.valueOf(16724016));
        /* 451 */
        colors.put("firebrick 2", Integer.valueOf(15608876));
        /* 452 */
        colors.put("firebrick 3", Integer.valueOf(13444646));
        /* 453 */
        colors.put("firebrick 4", Integer.valueOf(9116186));
        /* 454 */
        colors.put("red 1 (red*)", Integer.valueOf(16711680));
        /* 455 */
        colors.put("red*", Integer.valueOf(16711680));
        /* 456 */
        colors.put("red 2", Integer.valueOf(15597568));
        /* 457 */
        colors.put("red 3", Integer.valueOf(13434880));
        /* 458 */
        colors.put("red 4 (darkred)", Integer.valueOf(9109504));
        /* 459 */
        colors.put("maroon*", Integer.valueOf(8388608));
        /* 460 */
        colors.put("sgi beet", Integer.valueOf(9320590));
        /* 461 */
        colors.put("sgi slateblue", Integer.valueOf(7434694));
        /* 462 */
        colors.put("sgi lightblue", Integer.valueOf(8232640));
        /* 463 */
        colors.put("sgi teal", Integer.valueOf(3706510));
        /* 464 */
        colors.put("sgi chartreuse", Integer.valueOf(7456369));
        /* 465 */
        colors.put("sgi olivedrab", Integer.valueOf(9342520));
        /* 466 */
        colors.put("sgi brightgray", Integer.valueOf(12960170));
        /* 467 */
        colors.put("sgi salmon", Integer.valueOf(13005169));
        /* 468 */
        colors.put("sgi darkgray", Integer.valueOf(5592405));
        /* 469 */
        colors.put("sgi gray 12", Integer.valueOf(1973790));
        /* 470 */
        colors.put("sgi gray 16", Integer.valueOf(2631720));
        /* 471 */
        colors.put("sgi gray 32", Integer.valueOf(5329233));
        /* 472 */
        colors.put("sgi gray 36", Integer.valueOf(5987163));
        /* 473 */
        colors.put("sgi gray 52", Integer.valueOf(8684676));
        /* 474 */
        colors.put("sgi gray 56", Integer.valueOf(9342606));
        /* 475 */
        colors.put("sgi lightgray", Integer.valueOf(11184810));
        /* 476 */
        colors.put("sgi gray 72", Integer.valueOf(12040119));
        /* 477 */
        colors.put("sgi gray 76", Integer.valueOf(12698049));
        /* 478 */
        colors.put("sgi gray 92", Integer.valueOf(15395562));
        /* 479 */
        colors.put("sgi gray 96", Integer.valueOf(16053492));
        /* 480 */
        colors.put("white*", Integer.valueOf(16777215));
        /* 481 */
        colors.put("white smoke (gray 96)", Integer.valueOf(16119285));
        /* 482 */
        colors.put("gainsboro", Integer.valueOf(14474460));
        /* 483 */
        colors.put("lightgrey", Integer.valueOf(13882323));
        /* 484 */
        colors.put("silver*", Integer.valueOf(12632256));
        /* 485 */
        colors.put("darkgray", Integer.valueOf(11119017));
        /* 486 */
        colors.put("gray*", Integer.valueOf(8421504));
        /* 487 */
        colors.put("dimgray (gray 42)", Integer.valueOf(6908265));
        /* 488 */
        colors.put("black*", Integer.valueOf(0));
        /* 489 */
        colors.put("gray 99", Integer.valueOf(16579836));
        /* 490 */
        colors.put("gray 98", Integer.valueOf(16448250));
        /* 491 */
        colors.put("gray 97", Integer.valueOf(16250871));
        /* 492 */
        colors.put("white smoke (gray 96)", Integer.valueOf(16119285));
        /* 493 */
        colors.put("gray 95", Integer.valueOf(15921906));
        /* 494 */
        colors.put("gray 94", Integer.valueOf(15790320));
        /* 495 */
        colors.put("gray 93", Integer.valueOf(15592941));
        /* 496 */
        colors.put("gray 92", Integer.valueOf(15461355));
        /* 497 */
        colors.put("gray 91", Integer.valueOf(15263976));
        /* 498 */
        colors.put("gray 90", Integer.valueOf(15066597));
        /* 499 */
        colors.put("gray 89", Integer.valueOf(14935011));
        /* 500 */
        colors.put("gray 88", Integer.valueOf(14737632));
        /* 501 */
        colors.put("gray 87", Integer.valueOf(14606046));
        /* 502 */
        colors.put("gray 86", Integer.valueOf(14408667));
        /* 503 */
        colors.put("gray 85", Integer.valueOf(14277081));
        /* 504 */
        colors.put("gray 84", Integer.valueOf(14079702));
        /* 505 */
        colors.put("gray 83", Integer.valueOf(13948116));
        /* 506 */
        colors.put("gray 82", Integer.valueOf(13750737));
        /* 507 */
        colors.put("gray 81", Integer.valueOf(13619151));
        /* 508 */
        colors.put("gray 80", Integer.valueOf(13421772));
        /* 509 */
        colors.put("gray 79", Integer.valueOf(13224393));
        /* 510 */
        colors.put("gray 78", Integer.valueOf(13092807));
        /* 511 */
        colors.put("gray 77", Integer.valueOf(12895428));
        /* 512 */
        colors.put("gray 76", Integer.valueOf(12763842));
        /* 513 */
        colors.put("gray 75", Integer.valueOf(12566463));
        /* 514 */
        colors.put("gray 74", Integer.valueOf(12434877));
        /* 515 */
        colors.put("gray 73", Integer.valueOf(12237498));
        /* 516 */
        colors.put("gray 72", Integer.valueOf(12105912));
        /* 517 */
        colors.put("gray 71", Integer.valueOf(11908533));
        /* 518 */
        colors.put("gray 70", Integer.valueOf(11776947));
        /* 519 */
        colors.put("gray 69", Integer.valueOf(11579568));
        /* 520 */
        colors.put("gray 68", Integer.valueOf(11382189));
        /* 521 */
        colors.put("gray 67", Integer.valueOf(11250603));
        /* 522 */
        colors.put("gray 66", Integer.valueOf(11053224));
        /* 523 */
        colors.put("gray 65", Integer.valueOf(10921638));
        /* 524 */
        colors.put("gray 64", Integer.valueOf(10724259));
        /* 525 */
        colors.put("gray 63", Integer.valueOf(10592673));
        /* 526 */
        colors.put("gray 62", Integer.valueOf(10395294));
        /* 527 */
        colors.put("gray 61", Integer.valueOf(10263708));
        /* 528 */
        colors.put("gray 60", Integer.valueOf(10066329));
        /* 529 */
        colors.put("gray 59", Integer.valueOf(9868950));
        /* 530 */
        colors.put("gray 58", Integer.valueOf(9737364));
        /* 531 */
        colors.put("gray 57", Integer.valueOf(9539985));
        /* 532 */
        colors.put("gray 56", Integer.valueOf(9408399));
        /* 533 */
        colors.put("gray 55", Integer.valueOf(9211020));
        /* 534 */
        colors.put("gray 54", Integer.valueOf(9079434));
        /* 535 */
        colors.put("gray 53", Integer.valueOf(8882055));
        /* 536 */
        colors.put("gray 52", Integer.valueOf(8750469));
        /* 537 */
        colors.put("gray 51", Integer.valueOf(8553090));
        /* 538 */
        colors.put("gray 50", Integer.valueOf(8355711));
        /* 539 */
        colors.put("gray 49", Integer.valueOf(8224125));
        /* 540 */
        colors.put("gray 48", Integer.valueOf(8026746));
        /* 541 */
        colors.put("gray 47", Integer.valueOf(7895160));
        /* 542 */
        colors.put("gray 46", Integer.valueOf(7697781));
        /* 543 */
        colors.put("gray 45", Integer.valueOf(7566195));
        /* 544 */
        colors.put("gray 44", Integer.valueOf(7368816));
        /* 545 */
        colors.put("gray 43", Integer.valueOf(7237230));
        /* 546 */
        colors.put("gray 42", Integer.valueOf(7039851));
        /* 547 */
        colors.put("dimgray (gray 42)", Integer.valueOf(6908265));
        /* 548 */
        colors.put("gray 40", Integer.valueOf(6710886));
        /* 549 */
        colors.put("gray 39", Integer.valueOf(6513507));
        /* 550 */
        colors.put("gray 38", Integer.valueOf(6381921));
        /* 551 */
        colors.put("gray 37", Integer.valueOf(6184542));
        /* 552 */
        colors.put("gray 36", Integer.valueOf(6052956));
        /* 553 */
        colors.put("gray 35", Integer.valueOf(5855577));
        /* 554 */
        colors.put("gray 34", Integer.valueOf(5723991));
        /* 555 */
        colors.put("gray 33", Integer.valueOf(5526612));
        /* 556 */
        colors.put("gray 32", Integer.valueOf(5395026));
        /* 557 */
        colors.put("gray 31", Integer.valueOf(5197647));
        /* 558 */
        colors.put("gray 30", Integer.valueOf(5066061));
        /* 559 */
        colors.put("gray 29", Integer.valueOf(4868682));
        /* 560 */
        colors.put("gray 28", Integer.valueOf(4671303));
        /* 561 */
        colors.put("gray 27", Integer.valueOf(4539717));
        /* 562 */
        colors.put("gray 26", Integer.valueOf(4342338));
        /* 563 */
        colors.put("gray 25", Integer.valueOf(4210752));
        /* 564 */
        colors.put("gray 24", Integer.valueOf(4013373));
        /* 565 */
        colors.put("gray 23", Integer.valueOf(3881787));
        /* 566 */
        colors.put("gray 22", Integer.valueOf(3684408));
        /* 567 */
        colors.put("gray 21", Integer.valueOf(3552822));
        /* 568 */
        colors.put("gray 20", Integer.valueOf(3355443));
        /* 569 */
        colors.put("gray 19", Integer.valueOf(3158064));
        /* 570 */
        colors.put("gray 18", Integer.valueOf(3026478));
        /* 571 */
        colors.put("gray 17", Integer.valueOf(2829099));
        /* 572 */
        colors.put("gray 16", Integer.valueOf(2697513));
        /* 573 */
        colors.put("gray 15", Integer.valueOf(2500134));
        /* 574 */
        colors.put("gray 14", Integer.valueOf(2368548));
        /* 575 */
        colors.put("gray 13", Integer.valueOf(2171169));
        /* 576 */
        colors.put("gray 12", Integer.valueOf(2039583));
        /* 577 */
        colors.put("gray 11", Integer.valueOf(1842204));
        /* 578 */
        colors.put("gray 10", Integer.valueOf(1710618));
        /* 579 */
        colors.put("gray 9", Integer.valueOf(1513239));
        /* 580 */
        colors.put("gray 8", Integer.valueOf(1315860));
        /* 581 */
        colors.put("gray 7", Integer.valueOf(1184274));
        /* 582 */
        colors.put("gray 6", Integer.valueOf(986895));
        /* 583 */
        colors.put("gray 5", Integer.valueOf(855309));
        /* 584 */
        colors.put("gray 4", Integer.valueOf(657930));
        /* 585 */
        colors.put("gray 3", Integer.valueOf(526344));
        /* 586 */
        colors.put("gray 2", Integer.valueOf(328965));
        /* 587 */
        colors.put("gray 1", Integer.valueOf(197379));
        /*     */
    }

    /*     */
    /* 590 */   private static List<Integer> rgbvalues = new ArrayList(colors.values());

    /*     */
    /*     */
    private static int get(String colorName, boolean loose) {
        /* 593 */
        if (colorName == null) {
            /* 594 */
            return 0;
            /*     */
        }
        /* 596 */
        colorName = Strings.trim(colorName).toLowerCase();
        /* 597 */
        Integer v = (Integer) colors.get(colorName);
        /* 598 */
        if ((v == null) && (!colorName.endsWith("*"))) {
            /* 599 */
            v = (Integer) colors.get(colorName + "*");
            /*     */
        }
        /* 601 */
        if ((v == null) && (loose)) {
            /* 602 */
            for (String key : colors.keySet()) {
                /* 603 */
                if (key.contains(colorName)) {
                    /* 604 */
                    v = (Integer) colors.get(key);
                    /* 605 */
                    break;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 609 */
        return v == null ? 0 : v.intValue();
        /*     */
    }

    /*     */
    /*     */
    public static int get(String colorName) {
        /* 613 */
        return get(colorName, false);
        /*     */
    }

    /*     */
    /*     */
    public static int getx(String colorName) {
        /* 617 */
        return get(colorName, true);
        /*     */
    }

    /*     */
    /*     */
    public static Map<String, Integer> getAll() {
        /* 621 */
        return Collections.unmodifiableMap(colors);
        /*     */
    }

    /*     */
    /*     */
    public static Iterator<Integer> getSequentialIterator() {
        /* 625 */
        return getSequentialIterator(0, 1);
        /*     */
    }

    /*     */
    /*     */
    public static Iterator<Integer> getSequentialIterator(int start, int increment) {
        /* 629 */
        return new InfiIter(start, increment);
        /*     */
    }

    /*     */
    /*     */
    public static Iterator<Integer> getRandomIterator() {
        /* 633 */
        return new RandomIter(null);
        /*     */
    }

    /*     */
    /*     */   private static class InfiIter implements Iterator<Integer> {
        /*     */ int index;
        /*     */ int increment;

        /*     */
        /*     */     InfiIter(int start, int increment) {
            /* 641 */
            if ((start < 0) || (increment <= 0)) {
                /* 642 */
                throw new IllegalArgumentException();
                /*     */
            }
            /* 644 */
            this.increment = increment;
            /* 645 */
            this.index = (start % ColorsGradient.colors.size());
            /*     */
        }

        /*     */
        /*     */
        public boolean hasNext()
        /*     */ {
            /* 650 */
            return true;
            /*     */
        }

        /*     */
        /*     */
        public Integer next()
        /*     */ {
            /* 655 */
            int v = ((Integer) ColorsGradient.rgbvalues.get(this.index)).intValue();
            /* 656 */
            this.index = ((this.index + this.increment) % ColorsGradient.colors.size());
            /* 657 */
            return Integer.valueOf(v);
            /*     */
        }

        /*     */
        /*     */
        public void remove()
        /*     */ {
            /* 662 */
            throw new UnsupportedOperationException();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   private static class RandomIter implements Iterator<Integer>
            /*     */ {
        /*     */
        public boolean hasNext()
        /*     */ {
            /* 670 */
            return true;
            /*     */
        }

        /*     */
        /*     */
        public Integer next()
        /*     */ {
            /* 675 */
            int index = (int) (Math.random() * ColorsGradient.rgbvalues.size()) % ColorsGradient.rgbvalues.size();
            /* 676 */
            return (Integer) ColorsGradient.rgbvalues.get(index);
            /*     */
        }

        /*     */
        /*     */
        public void remove()
        /*     */ {
            /* 681 */
            throw new UnsupportedOperationException();
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\ColorsGradient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */