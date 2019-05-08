package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColorsGradient {
    private static final Map<String, Integer> colors = new LinkedHashMap();

    static {
        colors.put("indian red", 11540255);
        colors.put("crimson", Integer.valueOf(14423100));
        colors.put("lightpink", Integer.valueOf(16758465));
        colors.put("lightpink 1", Integer.valueOf(16756409));
        colors.put("lightpink 2", Integer.valueOf(15639213));
        colors.put("lightpink 3", Integer.valueOf(13470869));
        colors.put("lightpink 4", Integer.valueOf(9133925));
        colors.put("pink", Integer.valueOf(16761035));
        colors.put("pink 1", Integer.valueOf(16758213));
        colors.put("pink 2", Integer.valueOf(15641016));
        colors.put("pink 3", Integer.valueOf(13472158));
        colors.put("pink 4", Integer.valueOf(9134956));
        colors.put("palevioletred", Integer.valueOf(14381203));
        colors.put("palevioletred 1", Integer.valueOf(16745131));
        colors.put("palevioletred 2", Integer.valueOf(15628703));
        colors.put("palevioletred 3", Integer.valueOf(13461641));
        colors.put("palevioletred 4", Integer.valueOf(9127773));
        colors.put("lavenderblush 1 (lavenderblush)", Integer.valueOf(16773365));
        colors.put("lavenderblush 2", Integer.valueOf(15655141));
        colors.put("lavenderblush 3", Integer.valueOf(13484485));
        colors.put("lavenderblush 4", Integer.valueOf(9143174));
        colors.put("violetred 1", Integer.valueOf(16727702));
        colors.put("violetred 2", Integer.valueOf(15612556));
        colors.put("violetred 3", Integer.valueOf(13447800));
        colors.put("violetred 4", Integer.valueOf(9118290));
        colors.put("hotpink", Integer.valueOf(16738740));
        colors.put("hotpink 1", Integer.valueOf(16740020));
        colors.put("hotpink 2", Integer.valueOf(15624871));
        colors.put("hotpink 3", Integer.valueOf(13459600));
        colors.put("hotpink 4", Integer.valueOf(9124450));
        colors.put("raspberry", Integer.valueOf(8857175));
        colors.put("deeppink 1 (deeppink)", Integer.valueOf(16716947));
        colors.put("deeppink 2", Integer.valueOf(15602313));
        colors.put("deeppink 3", Integer.valueOf(13439094));
        colors.put("deeppink 4", Integer.valueOf(9112144));
        colors.put("maroon 1", Integer.valueOf(16725171));
        colors.put("maroon 2", Integer.valueOf(15610023));
        colors.put("maroon 3", Integer.valueOf(13445520));
        colors.put("maroon 4", Integer.valueOf(9116770));
        colors.put("mediumvioletred", Integer.valueOf(13047173));
        colors.put("violetred", Integer.valueOf(13639824));
        colors.put("orchid", Integer.valueOf(14315734));
        colors.put("orchid 1", Integer.valueOf(16745466));
        colors.put("orchid 2", Integer.valueOf(15629033));
        colors.put("orchid 3", Integer.valueOf(13461961));
        colors.put("orchid 4", Integer.valueOf(9127817));
        colors.put("thistle", Integer.valueOf(14204888));
        colors.put("thistle 1", Integer.valueOf(16769535));
        colors.put("thistle 2", Integer.valueOf(15651566));
        colors.put("thistle 3", Integer.valueOf(13481421));
        colors.put("thistle 4", Integer.valueOf(9141131));
        colors.put("plum 1", Integer.valueOf(16759807));
        colors.put("plum 2", Integer.valueOf(15642350));
        colors.put("plum 3", Integer.valueOf(13473485));
        colors.put("plum 4", Integer.valueOf(9135755));
        colors.put("plum", Integer.valueOf(14524637));
        colors.put("violet", Integer.valueOf(15631086));
        colors.put("magenta (fuchsia*)", Integer.valueOf(16711935));
        colors.put("magenta*", Integer.valueOf(16711935));
        colors.put("magenta 2", Integer.valueOf(15597806));
        colors.put("magenta 3", Integer.valueOf(13435085));
        colors.put("magenta 4 (darkmagenta)", Integer.valueOf(9109643));
        colors.put("purple*", Integer.valueOf(8388736));
        colors.put("mediumorchid", Integer.valueOf(12211667));
        colors.put("mediumorchid 1", Integer.valueOf(14706431));
        colors.put("mediumorchid 2", Integer.valueOf(13721582));
        colors.put("mediumorchid 3", Integer.valueOf(11817677));
        colors.put("mediumorchid 4", Integer.valueOf(8009611));
        colors.put("darkviolet", Integer.valueOf(9699539));
        colors.put("darkorchid", Integer.valueOf(10040012));
        colors.put("darkorchid 1", Integer.valueOf(12533503));
        colors.put("darkorchid 2", Integer.valueOf(11680494));
        colors.put("darkorchid 3", Integer.valueOf(10105549));
        colors.put("darkorchid 4", Integer.valueOf(6824587));
        colors.put("indigo", Integer.valueOf(4915330));
        colors.put("blueviolet", Integer.valueOf(9055202));
        colors.put("purple 1", Integer.valueOf(10170623));
        colors.put("purple 2", Integer.valueOf(9514222));
        colors.put("purple 3", Integer.valueOf(8201933));
        colors.put("purple 4", Integer.valueOf(5577355));
        colors.put("mediumpurple", Integer.valueOf(9662683));
        colors.put("mediumpurple 1", Integer.valueOf(11240191));
        colors.put("mediumpurple 2", Integer.valueOf(10451438));
        colors.put("mediumpurple 3", Integer.valueOf(9005261));
        colors.put("mediumpurple 4", Integer.valueOf(6113163));
        colors.put("darkslateblue", Integer.valueOf(4734347));
        colors.put("lightslateblue", Integer.valueOf(8679679));
        colors.put("mediumslateblue", Integer.valueOf(8087790));
        colors.put("slateblue", Integer.valueOf(6970061));
        colors.put("slateblue 1", Integer.valueOf(8613887));
        colors.put("slateblue 2", Integer.valueOf(8021998));
        colors.put("slateblue 3", Integer.valueOf(6904269));
        colors.put("slateblue 4", Integer.valueOf(4668555));
        colors.put("ghostwhite", Integer.valueOf(16316671));
        colors.put("lavender", Integer.valueOf(15132410));
        colors.put("blue*", Integer.valueOf(255));
        colors.put("blue 2", Integer.valueOf(238));
        colors.put("blue 3 (mediumblue)", Integer.valueOf(205));
        colors.put("blue 4 (darkblue)", Integer.valueOf(139));
        colors.put("navy*", Integer.valueOf(128));
        colors.put("midnightblue", Integer.valueOf(1644912));
        colors.put("cobalt", Integer.valueOf(4020651));
        colors.put("royalblue", Integer.valueOf(4286945));
        colors.put("royalblue 1", Integer.valueOf(4749055));
        colors.put("royalblue 2", Integer.valueOf(4419310));
        colors.put("royalblue 3", Integer.valueOf(3825613));
        colors.put("royalblue 4", Integer.valueOf(2572427));
        colors.put("cornflowerblue", Integer.valueOf(6591981));
        colors.put("lightsteelblue", Integer.valueOf(11584734));
        colors.put("lightsteelblue 1", Integer.valueOf(13296127));
        colors.put("lightsteelblue 2", Integer.valueOf(12374766));
        colors.put("lightsteelblue 3", Integer.valueOf(10663373));
        colors.put("lightsteelblue 4", Integer.valueOf(7240587));
        colors.put("lightslategray", Integer.valueOf(7833753));
        colors.put("slategray", Integer.valueOf(7372944));
        colors.put("slategray 1", Integer.valueOf(13034239));
        colors.put("slategray 2", Integer.valueOf(12178414));
        colors.put("slategray 3", Integer.valueOf(10467021));
        colors.put("slategray 4", Integer.valueOf(7109515));
        colors.put("dodgerblue 1 (dodgerblue)", Integer.valueOf(2003199));
        colors.put("dodgerblue 2", Integer.valueOf(1869550));
        colors.put("dodgerblue 3", Integer.valueOf(1602765));
        colors.put("dodgerblue 4", Integer.valueOf(1068683));
        colors.put("aliceblue", Integer.valueOf(15792383));
        colors.put("steelblue", Integer.valueOf(4620980));
        colors.put("steelblue 1", Integer.valueOf(6535423));
        colors.put("steelblue 2", Integer.valueOf(6073582));
        colors.put("steelblue 3", Integer.valueOf(5215437));
        colors.put("steelblue 4", Integer.valueOf(3564683));
        colors.put("lightskyblue", Integer.valueOf(8900346));
        colors.put("lightskyblue 1", Integer.valueOf(11592447));
        colors.put("lightskyblue 2", Integer.valueOf(10802158));
        colors.put("lightskyblue 3", Integer.valueOf(9287373));
        colors.put("lightskyblue 4", Integer.valueOf(6323083));
        colors.put("skyblue 1", Integer.valueOf(8900351));
        colors.put("skyblue 2", Integer.valueOf(8306926));
        colors.put("skyblue 3", Integer.valueOf(7120589));
        colors.put("skyblue 4", Integer.valueOf(4878475));
        colors.put("skyblue", Integer.valueOf(8900331));
        colors.put("deepskyblue 1 (deepskyblue)", Integer.valueOf(49151));
        colors.put("deepskyblue 2", Integer.valueOf(45806));
        colors.put("deepskyblue 3", Integer.valueOf(39629));
        colors.put("deepskyblue 4", Integer.valueOf(26763));
        colors.put("peacock", Integer.valueOf(3383753));
        colors.put("lightblue", Integer.valueOf(11393254));
        colors.put("lightblue 1", Integer.valueOf(12578815));
        colors.put("lightblue 2", Integer.valueOf(11722734));
        colors.put("lightblue 3", Integer.valueOf(10141901));
        colors.put("lightblue 4", Integer.valueOf(6849419));
        colors.put("powderblue", Integer.valueOf(11591910));
        colors.put("cadetblue 1", Integer.valueOf(10024447));
        colors.put("cadetblue 2", Integer.valueOf(9364974));
        colors.put("cadetblue 3", Integer.valueOf(8046029));
        colors.put("cadetblue 4", Integer.valueOf(5473931));
        colors.put("turquoise 1", Integer.valueOf(62975));
        colors.put("turquoise 2", Integer.valueOf(58862));
        colors.put("turquoise 3", Integer.valueOf(50637));
        colors.put("turquoise 4", Integer.valueOf(34443));
        colors.put("cadetblue", Integer.valueOf(6266528));
        colors.put("darkturquoise", Integer.valueOf(52945));
        colors.put("azure 1 (azure)", Integer.valueOf(15794175));
        colors.put("azure 2", Integer.valueOf(14741230));
        colors.put("azure 3", Integer.valueOf(12701133));
        colors.put("azure 4", Integer.valueOf(8620939));
        colors.put("lightcyan 1 (lightcyan)", Integer.valueOf(14745599));
        colors.put("lightcyan 2", Integer.valueOf(13758190));
        colors.put("lightcyan 3", Integer.valueOf(11849165));
        colors.put("lightcyan 4", Integer.valueOf(8031115));
        colors.put("paleturquoise 1", Integer.valueOf(12320767));
        colors.put("paleturquoise 2 (paleturquoise)", Integer.valueOf(11464430));
        colors.put("paleturquoise 3", Integer.valueOf(9883085));
        colors.put("paleturquoise 4", Integer.valueOf(6720395));
        colors.put("darkslategray", Integer.valueOf(3100495));
        colors.put("darkslategray 1", Integer.valueOf(9961471));
        colors.put("darkslategray 2", Integer.valueOf(9301742));
        colors.put("darkslategray 3", Integer.valueOf(7982541));
        colors.put("darkslategray 4", Integer.valueOf(5409675));
        colors.put("cyan / aqua*", Integer.valueOf(65535));
        colors.put("cyan*", Integer.valueOf(65535));
        colors.put("cyan 2", Integer.valueOf(61166));
        colors.put("cyan 3", Integer.valueOf(52685));
        colors.put("cyan 4 (darkcyan)", Integer.valueOf(35723));
        colors.put("teal*", Integer.valueOf(32896));
        colors.put("mediumturquoise", Integer.valueOf(4772300));
        colors.put("lightseagreen", Integer.valueOf(2142890));
        colors.put("manganeseblue", Integer.valueOf(239774));
        colors.put("turquoise", Integer.valueOf(4251856));
        colors.put("coldgrey", Integer.valueOf(8424071));
        colors.put("turquoiseblue", Integer.valueOf(51084));
        colors.put("aquamarine 1 (aquamarine)", Integer.valueOf(8388564));
        colors.put("aquamarine 2", Integer.valueOf(7794374));
        colors.put("aquamarine 3 (mediumaquamarine)", Integer.valueOf(6737322));
        colors.put("aquamarine 4", Integer.valueOf(4557684));
        colors.put("mediumspringgreen", Integer.valueOf(64154));
        colors.put("mintcream", Integer.valueOf(16121850));
        colors.put("springgreen", Integer.valueOf(65407));
        colors.put("springgreen 1", Integer.valueOf(61046));
        colors.put("springgreen 2", Integer.valueOf(52582));
        colors.put("springgreen 3", Integer.valueOf(35653));
        colors.put("mediumseagreen", Integer.valueOf(3978097));
        colors.put("seagreen 1", Integer.valueOf(5570463));
        colors.put("seagreen 2", Integer.valueOf(5172884));
        colors.put("seagreen 3", Integer.valueOf(4443520));
        colors.put("seagreen 4 (seagreen)", Integer.valueOf(3050327));
        colors.put("emeraldgreen", Integer.valueOf(51543));
        colors.put("mint", Integer.valueOf(12451017));
        colors.put("cobaltgreen", Integer.valueOf(4034880));
        colors.put("honeydew 1 (honeydew)", Integer.valueOf(15794160));
        colors.put("honeydew 2", Integer.valueOf(14741216));
        colors.put("honeydew 3", Integer.valueOf(12701121));
        colors.put("honeydew 4", Integer.valueOf(8620931));
        colors.put("darkseagreen", Integer.valueOf(9419919));
        colors.put("darkseagreen 1", Integer.valueOf(12713921));
        colors.put("darkseagreen 2", Integer.valueOf(11857588));
        colors.put("darkseagreen 3", Integer.valueOf(10210715));
        colors.put("darkseagreen 4", Integer.valueOf(6916969));
        colors.put("palegreen", Integer.valueOf(10025880));
        colors.put("palegreen 1", Integer.valueOf(10157978));
        colors.put("palegreen 2 (lightgreen)", Integer.valueOf(9498256));
        colors.put("palegreen 3", Integer.valueOf(8179068));
        colors.put("palegreen 4", Integer.valueOf(5540692));
        colors.put("limegreen", Integer.valueOf(3329330));
        colors.put("forestgreen", Integer.valueOf(2263842));
        colors.put("green 1 (lime*)", Integer.valueOf(65280));
        colors.put("lime*", Integer.valueOf(65280));
        colors.put("green 2", Integer.valueOf(60928));
        colors.put("green 3", Integer.valueOf(52480));
        colors.put("green 4", Integer.valueOf(35584));
        colors.put("green*", Integer.valueOf(32768));
        colors.put("darkgreen", Integer.valueOf(25600));
        colors.put("sapgreen", Integer.valueOf(3178516));
        colors.put("lawngreen", Integer.valueOf(8190976));
        colors.put("chartreuse 1 (chartreuse)", Integer.valueOf(8388352));
        colors.put("chartreuse 2", Integer.valueOf(7794176));
        colors.put("chartreuse 3", Integer.valueOf(6737152));
        colors.put("chartreuse 4", Integer.valueOf(4557568));
        colors.put("greenyellow", Integer.valueOf(11403055));
        colors.put("darkolivegreen 1", Integer.valueOf(13303664));
        colors.put("darkolivegreen 2", Integer.valueOf(12381800));
        colors.put("darkolivegreen 3", Integer.valueOf(10669402));
        colors.put("darkolivegreen 4", Integer.valueOf(7244605));
        colors.put("darkolivegreen", Integer.valueOf(5597999));
        colors.put("olivedrab", Integer.valueOf(7048739));
        colors.put("olivedrab 1", Integer.valueOf(12648254));
        colors.put("olivedrab 2", Integer.valueOf(11791930));
        colors.put("olivedrab 3 (yellowgreen)", Integer.valueOf(10145074));
        colors.put("olivedrab 4", Integer.valueOf(6916898));
        colors.put("ivory 1 (ivory)", Integer.valueOf(16777200));
        colors.put("ivory 2", Integer.valueOf(15658720));
        colors.put("ivory 3", Integer.valueOf(13487553));
        colors.put("ivory 4", Integer.valueOf(9145219));
        colors.put("beige", Integer.valueOf(16119260));
        colors.put("lightyellow 1 (lightyellow)", Integer.valueOf(16777184));
        colors.put("lightyellow 2", Integer.valueOf(15658705));
        colors.put("lightyellow 3", Integer.valueOf(13487540));
        colors.put("lightyellow 4", Integer.valueOf(9145210));
        colors.put("lightgoldenrodyellow", Integer.valueOf(16448210));
        colors.put("yellow 1 (yellow*)", Integer.valueOf(16776960));
        colors.put("yellow*", Integer.valueOf(16776960));
        colors.put("yellow 2", Integer.valueOf(15658496));
        colors.put("yellow 3", Integer.valueOf(13487360));
        colors.put("yellow 4", Integer.valueOf(9145088));
        colors.put("warmgrey", Integer.valueOf(8421481));
        colors.put("olive*", Integer.valueOf(8421376));
        colors.put("darkkhaki", Integer.valueOf(12433259));
        colors.put("khaki 1", Integer.valueOf(16774799));
        colors.put("khaki 2", Integer.valueOf(15656581));
        colors.put("khaki 3", Integer.valueOf(13485683));
        colors.put("khaki 4", Integer.valueOf(9143886));
        colors.put("khaki", Integer.valueOf(15787660));
        colors.put("palegoldenrod", Integer.valueOf(15657130));
        colors.put("lemonchiffon 1 (lemonchiffon)", Integer.valueOf(16775885));
        colors.put("lemonchiffon 2", Integer.valueOf(15657407));
        colors.put("lemonchiffon 3", Integer.valueOf(13486501));
        colors.put("lemonchiffon 4", Integer.valueOf(9144688));
        colors.put("lightgoldenrod 1", Integer.valueOf(16772235));
        colors.put("lightgoldenrod 2", Integer.valueOf(15654018));
        colors.put("lightgoldenrod 3", Integer.valueOf(13483632));
        colors.put("lightgoldenrod 4", Integer.valueOf(9142604));
        colors.put("banana", Integer.valueOf(14929751));
        colors.put("gold 1 (gold)", Integer.valueOf(16766720));
        colors.put("gold 2", Integer.valueOf(15649024));
        colors.put("gold 3", Integer.valueOf(13479168));
        colors.put("gold 4", Integer.valueOf(9139456));
        colors.put("cornsilk 1 (cornsilk)", Integer.valueOf(16775388));
        colors.put("cornsilk 2", Integer.valueOf(15657165));
        colors.put("cornsilk 3", Integer.valueOf(13486257));
        colors.put("cornsilk 4", Integer.valueOf(9144440));
        colors.put("goldenrod", Integer.valueOf(14329120));
        colors.put("goldenrod 1", Integer.valueOf(16761125));
        colors.put("goldenrod 2", Integer.valueOf(15643682));
        colors.put("goldenrod 3", Integer.valueOf(13474589));
        colors.put("goldenrod 4", Integer.valueOf(9136404));
        colors.put("darkgoldenrod", Integer.valueOf(12092939));
        colors.put("darkgoldenrod 1", Integer.valueOf(16759055));
        colors.put("darkgoldenrod 2", Integer.valueOf(15641870));
        colors.put("darkgoldenrod 3", Integer.valueOf(13473036));
        colors.put("darkgoldenrod 4", Integer.valueOf(9135368));
        colors.put("orange 1 (orange)", Integer.valueOf(16753920));
        colors.put("orange 2", Integer.valueOf(15636992));
        colors.put("orange 3", Integer.valueOf(13468928));
        colors.put("orange 4", Integer.valueOf(9132544));
        colors.put("floralwhite", Integer.valueOf(16775920));
        colors.put("oldlace", Integer.valueOf(16643558));
        colors.put("wheat", Integer.valueOf(16113331));
        colors.put("wheat 1", Integer.valueOf(16771002));
        colors.put("wheat 2", Integer.valueOf(15653038));
        colors.put("wheat 3", Integer.valueOf(13482646));
        colors.put("wheat 4", Integer.valueOf(9141862));
        colors.put("moccasin", Integer.valueOf(16770229));
        colors.put("papayawhip", Integer.valueOf(16773077));
        colors.put("blanchedalmond", Integer.valueOf(16772045));
        colors.put("navajowhite 1 (navajowhite)", Integer.valueOf(16768685));
        colors.put("navajowhite 2", Integer.valueOf(15650721));
        colors.put("navajowhite 3", Integer.valueOf(13480843));
        colors.put("navajowhite 4", Integer.valueOf(9140574));
        colors.put("eggshell", Integer.valueOf(16574153));
        colors.put("tan", Integer.valueOf(13808780));
        colors.put("brick", Integer.valueOf(10249759));
        colors.put("cadmiumyellow", Integer.valueOf(16750866));
        colors.put("antiquewhite", Integer.valueOf(16444375));
        colors.put("antiquewhite 1", Integer.valueOf(16773083));
        colors.put("antiquewhite 2", Integer.valueOf(15654860));
        colors.put("antiquewhite 3", Integer.valueOf(13484208));
        colors.put("antiquewhite 4", Integer.valueOf(9143160));
        colors.put("burlywood", Integer.valueOf(14596231));
        colors.put("burlywood 1", Integer.valueOf(16765851));
        colors.put("burlywood 2", Integer.valueOf(15648145));
        colors.put("burlywood 3", Integer.valueOf(13478525));
        colors.put("burlywood 4", Integer.valueOf(9139029));
        colors.put("bisque 1 (bisque)", Integer.valueOf(16770244));
        colors.put("bisque 2", Integer.valueOf(15652279));
        colors.put("bisque 3", Integer.valueOf(13481886));
        colors.put("bisque 4", Integer.valueOf(9141611));
        colors.put("melon", Integer.valueOf(14919785));
        colors.put("carrot", Integer.valueOf(15569185));
        colors.put("darkorange", Integer.valueOf(16747520));
        colors.put("darkorange 1", Integer.valueOf(16744192));
        colors.put("darkorange 2", Integer.valueOf(15627776));
        colors.put("darkorange 3", Integer.valueOf(13460992));
        colors.put("darkorange 4", Integer.valueOf(9127168));
        colors.put("orange", Integer.valueOf(16744448));
        colors.put("tan 1", Integer.valueOf(16753999));
        colors.put("tan 2", Integer.valueOf(15637065));
        colors.put("tan 3 (peru)", Integer.valueOf(13468991));
        colors.put("tan 4", Integer.valueOf(9132587));
        colors.put("linen", Integer.valueOf(16445670));
        colors.put("peachpuff 1 (peachpuff)", Integer.valueOf(16767673));
        colors.put("peachpuff 2", Integer.valueOf(15649709));
        colors.put("peachpuff 3", Integer.valueOf(13479829));
        colors.put("peachpuff 4", Integer.valueOf(9140069));
        colors.put("seashell 1 (seashell)", Integer.valueOf(16774638));
        colors.put("seashell 2", Integer.valueOf(15656414));
        colors.put("seashell 3", Integer.valueOf(13485503));
        colors.put("seashell 4", Integer.valueOf(9143938));
        colors.put("sandybrown", Integer.valueOf(16032864));
        colors.put("rawsienna", Integer.valueOf(13066516));
        colors.put("chocolate", Integer.valueOf(13789470));
        colors.put("chocolate 1", Integer.valueOf(16744228));
        colors.put("chocolate 2", Integer.valueOf(15627809));
        colors.put("chocolate 3", Integer.valueOf(13461021));
        colors.put("chocolate 4 (saddlebrown)", Integer.valueOf(9127187));
        colors.put("ivoryblack", Integer.valueOf(2696225));
        colors.put("flesh", Integer.valueOf(16743744));
        colors.put("cadmiumorange", Integer.valueOf(16736515));
        colors.put("burntsienna", Integer.valueOf(9057807));
        colors.put("sienna", Integer.valueOf(10506797));
        colors.put("sienna 1", Integer.valueOf(16745031));
        colors.put("sienna 2", Integer.valueOf(15628610));
        colors.put("sienna 3", Integer.valueOf(13461561));
        colors.put("sienna 4", Integer.valueOf(9127718));
        colors.put("lightsalmon 1 (lightsalmon)", Integer.valueOf(16752762));
        colors.put("lightsalmon 2", Integer.valueOf(15635826));
        colors.put("lightsalmon 3", Integer.valueOf(13468002));
        colors.put("lightsalmon 4", Integer.valueOf(9131842));
        colors.put("coral", Integer.valueOf(16744272));
        colors.put("orangered 1 (orangered)", Integer.valueOf(16729344));
        colors.put("orangered 2", Integer.valueOf(15613952));
        colors.put("orangered 3", Integer.valueOf(13448960));
        colors.put("orangered 4", Integer.valueOf(9118976));
        colors.put("sepia", Integer.valueOf(6170130));
        colors.put("darksalmon", Integer.valueOf(15308410));
        colors.put("salmon 1", Integer.valueOf(16747625));
        colors.put("salmon 2", Integer.valueOf(15630946));
        colors.put("salmon 3", Integer.valueOf(13463636));
        colors.put("salmon 4", Integer.valueOf(9129017));
        colors.put("coral 1", Integer.valueOf(16740950));
        colors.put("coral 2", Integer.valueOf(15624784));
        colors.put("coral 3", Integer.valueOf(13458245));
        colors.put("coral 4", Integer.valueOf(9125423));
        colors.put("burntumber", Integer.valueOf(9057060));
        colors.put("tomato 1 (tomato)", Integer.valueOf(16737095));
        colors.put("tomato 2", Integer.valueOf(15621186));
        colors.put("tomato 3", Integer.valueOf(13455161));
        colors.put("tomato 4", Integer.valueOf(9123366));
        colors.put("salmon", Integer.valueOf(16416882));
        colors.put("mistyrose 1 (mistyrose)", Integer.valueOf(16770273));
        colors.put("mistyrose 2", Integer.valueOf(15652306));
        colors.put("mistyrose 3", Integer.valueOf(13481909));
        colors.put("mistyrose 4", Integer.valueOf(9141627));
        colors.put("snow 1 (snow)", Integer.valueOf(16775930));
        colors.put("snow 2", Integer.valueOf(15657449));
        colors.put("snow 3", Integer.valueOf(13486537));
        colors.put("snow 4", Integer.valueOf(9144713));
        colors.put("rosybrown", Integer.valueOf(12357519));
        colors.put("rosybrown 1", Integer.valueOf(16761281));
        colors.put("rosybrown 2", Integer.valueOf(15643828));
        colors.put("rosybrown 3", Integer.valueOf(13474715));
        colors.put("rosybrown 4", Integer.valueOf(9136489));
        colors.put("lightcoral", Integer.valueOf(15761536));
        colors.put("indianred", Integer.valueOf(13458524));
        colors.put("indianred 1", Integer.valueOf(16738922));
        colors.put("indianred 2", Integer.valueOf(15623011));
        colors.put("indianred 4", Integer.valueOf(9124410));
        colors.put("indianred 3", Integer.valueOf(13456725));
        colors.put("brown", Integer.valueOf(10824234));
        colors.put("brown 1", Integer.valueOf(16728128));
        colors.put("brown 2", Integer.valueOf(15612731));
        colors.put("brown 3", Integer.valueOf(13447987));
        colors.put("brown 4", Integer.valueOf(9118499));
        colors.put("firebrick", Integer.valueOf(11674146));
        colors.put("firebrick 1", Integer.valueOf(16724016));
        colors.put("firebrick 2", Integer.valueOf(15608876));
        colors.put("firebrick 3", Integer.valueOf(13444646));
        colors.put("firebrick 4", Integer.valueOf(9116186));
        colors.put("red 1 (red*)", Integer.valueOf(16711680));
        colors.put("red*", Integer.valueOf(16711680));
        colors.put("red 2", Integer.valueOf(15597568));
        colors.put("red 3", Integer.valueOf(13434880));
        colors.put("red 4 (darkred)", Integer.valueOf(9109504));
        colors.put("maroon*", Integer.valueOf(8388608));
        colors.put("sgi beet", Integer.valueOf(9320590));
        colors.put("sgi slateblue", Integer.valueOf(7434694));
        colors.put("sgi lightblue", Integer.valueOf(8232640));
        colors.put("sgi teal", Integer.valueOf(3706510));
        colors.put("sgi chartreuse", Integer.valueOf(7456369));
        colors.put("sgi olivedrab", Integer.valueOf(9342520));
        colors.put("sgi brightgray", Integer.valueOf(12960170));
        colors.put("sgi salmon", Integer.valueOf(13005169));
        colors.put("sgi darkgray", Integer.valueOf(5592405));
        colors.put("sgi gray 12", Integer.valueOf(1973790));
        colors.put("sgi gray 16", Integer.valueOf(2631720));
        colors.put("sgi gray 32", Integer.valueOf(5329233));
        colors.put("sgi gray 36", Integer.valueOf(5987163));
        colors.put("sgi gray 52", Integer.valueOf(8684676));
        colors.put("sgi gray 56", Integer.valueOf(9342606));
        colors.put("sgi lightgray", Integer.valueOf(11184810));
        colors.put("sgi gray 72", Integer.valueOf(12040119));
        colors.put("sgi gray 76", Integer.valueOf(12698049));
        colors.put("sgi gray 92", Integer.valueOf(15395562));
        colors.put("sgi gray 96", Integer.valueOf(16053492));
        colors.put("white*", Integer.valueOf(16777215));
        colors.put("white smoke (gray 96)", Integer.valueOf(16119285));
        colors.put("gainsboro", Integer.valueOf(14474460));
        colors.put("lightgrey", Integer.valueOf(13882323));
        colors.put("silver*", Integer.valueOf(12632256));
        colors.put("darkgray", Integer.valueOf(11119017));
        colors.put("gray*", Integer.valueOf(8421504));
        colors.put("dimgray (gray 42)", Integer.valueOf(6908265));
        colors.put("black*", Integer.valueOf(0));
        colors.put("gray 99", Integer.valueOf(16579836));
        colors.put("gray 98", Integer.valueOf(16448250));
        colors.put("gray 97", Integer.valueOf(16250871));
        colors.put("white smoke (gray 96)", Integer.valueOf(16119285));
        colors.put("gray 95", Integer.valueOf(15921906));
        colors.put("gray 94", Integer.valueOf(15790320));
        colors.put("gray 93", Integer.valueOf(15592941));
        colors.put("gray 92", Integer.valueOf(15461355));
        colors.put("gray 91", Integer.valueOf(15263976));
        colors.put("gray 90", Integer.valueOf(15066597));
        colors.put("gray 89", Integer.valueOf(14935011));
        colors.put("gray 88", Integer.valueOf(14737632));
        colors.put("gray 87", Integer.valueOf(14606046));
        colors.put("gray 86", Integer.valueOf(14408667));
        colors.put("gray 85", Integer.valueOf(14277081));
        colors.put("gray 84", Integer.valueOf(14079702));
        colors.put("gray 83", Integer.valueOf(13948116));
        colors.put("gray 82", Integer.valueOf(13750737));
        colors.put("gray 81", Integer.valueOf(13619151));
        colors.put("gray 80", Integer.valueOf(13421772));
        colors.put("gray 79", Integer.valueOf(13224393));
        colors.put("gray 78", Integer.valueOf(13092807));
        colors.put("gray 77", Integer.valueOf(12895428));
        colors.put("gray 76", Integer.valueOf(12763842));
        colors.put("gray 75", Integer.valueOf(12566463));
        colors.put("gray 74", Integer.valueOf(12434877));
        colors.put("gray 73", Integer.valueOf(12237498));
        colors.put("gray 72", Integer.valueOf(12105912));
        colors.put("gray 71", Integer.valueOf(11908533));
        colors.put("gray 70", Integer.valueOf(11776947));
        colors.put("gray 69", Integer.valueOf(11579568));
        colors.put("gray 68", Integer.valueOf(11382189));
        colors.put("gray 67", Integer.valueOf(11250603));
        colors.put("gray 66", Integer.valueOf(11053224));
        colors.put("gray 65", Integer.valueOf(10921638));
        colors.put("gray 64", Integer.valueOf(10724259));
        colors.put("gray 63", Integer.valueOf(10592673));
        colors.put("gray 62", Integer.valueOf(10395294));
        colors.put("gray 61", Integer.valueOf(10263708));
        colors.put("gray 60", Integer.valueOf(10066329));
        colors.put("gray 59", Integer.valueOf(9868950));
        colors.put("gray 58", Integer.valueOf(9737364));
        colors.put("gray 57", Integer.valueOf(9539985));
        colors.put("gray 56", Integer.valueOf(9408399));
        colors.put("gray 55", Integer.valueOf(9211020));
        colors.put("gray 54", Integer.valueOf(9079434));
        colors.put("gray 53", Integer.valueOf(8882055));
        colors.put("gray 52", Integer.valueOf(8750469));
        colors.put("gray 51", Integer.valueOf(8553090));
        colors.put("gray 50", Integer.valueOf(8355711));
        colors.put("gray 49", Integer.valueOf(8224125));
        colors.put("gray 48", Integer.valueOf(8026746));
        colors.put("gray 47", Integer.valueOf(7895160));
        colors.put("gray 46", Integer.valueOf(7697781));
        colors.put("gray 45", Integer.valueOf(7566195));
        colors.put("gray 44", Integer.valueOf(7368816));
        colors.put("gray 43", Integer.valueOf(7237230));
        colors.put("gray 42", Integer.valueOf(7039851));
        colors.put("dimgray (gray 42)", Integer.valueOf(6908265));
        colors.put("gray 40", Integer.valueOf(6710886));
        colors.put("gray 39", Integer.valueOf(6513507));
        colors.put("gray 38", Integer.valueOf(6381921));
        colors.put("gray 37", Integer.valueOf(6184542));
        colors.put("gray 36", Integer.valueOf(6052956));
        colors.put("gray 35", Integer.valueOf(5855577));
        colors.put("gray 34", Integer.valueOf(5723991));
        colors.put("gray 33", Integer.valueOf(5526612));
        colors.put("gray 32", Integer.valueOf(5395026));
        colors.put("gray 31", Integer.valueOf(5197647));
        colors.put("gray 30", Integer.valueOf(5066061));
        colors.put("gray 29", Integer.valueOf(4868682));
        colors.put("gray 28", Integer.valueOf(4671303));
        colors.put("gray 27", Integer.valueOf(4539717));
        colors.put("gray 26", Integer.valueOf(4342338));
        colors.put("gray 25", Integer.valueOf(4210752));
        colors.put("gray 24", Integer.valueOf(4013373));
        colors.put("gray 23", Integer.valueOf(3881787));
        colors.put("gray 22", Integer.valueOf(3684408));
        colors.put("gray 21", Integer.valueOf(3552822));
        colors.put("gray 20", Integer.valueOf(3355443));
        colors.put("gray 19", Integer.valueOf(3158064));
        colors.put("gray 18", Integer.valueOf(3026478));
        colors.put("gray 17", Integer.valueOf(2829099));
        colors.put("gray 16", Integer.valueOf(2697513));
        colors.put("gray 15", Integer.valueOf(2500134));
        colors.put("gray 14", Integer.valueOf(2368548));
        colors.put("gray 13", Integer.valueOf(2171169));
        colors.put("gray 12", Integer.valueOf(2039583));
        colors.put("gray 11", Integer.valueOf(1842204));
        colors.put("gray 10", Integer.valueOf(1710618));
        colors.put("gray 9", Integer.valueOf(1513239));
        colors.put("gray 8", Integer.valueOf(1315860));
        colors.put("gray 7", Integer.valueOf(1184274));
        colors.put("gray 6", Integer.valueOf(986895));
        colors.put("gray 5", Integer.valueOf(855309));
        colors.put("gray 4", Integer.valueOf(657930));
        colors.put("gray 3", Integer.valueOf(526344));
        colors.put("gray 2", Integer.valueOf(328965));
        colors.put("gray 1", Integer.valueOf(197379));
    }

    private static List<Integer> rgbvalues = new ArrayList(colors.values());

    private static int get(String colorName, boolean loose) {
        if (colorName == null) {
            return 0;
        }
        colorName = Strings.trim(colorName).toLowerCase();
        Integer v = (Integer) colors.get(colorName);
        if ((v == null) && (!colorName.endsWith("*"))) {
            v = (Integer) colors.get(colorName + "*");
        }
        if ((v == null) && (loose)) {
            for (String key : colors.keySet()) {
                if (key.contains(colorName)) {
                    v = (Integer) colors.get(key);
                    break;
                }
            }
        }
        return v == null ? 0 : v.intValue();
    }

    public static int get(String colorName) {
        return get(colorName, false);
    }

    public static int getx(String colorName) {
        return get(colorName, true);
    }

    public static Map<String, Integer> getAll() {
        return Collections.unmodifiableMap(colors);
    }

    public static Iterator<Integer> getSequentialIterator() {
        return getSequentialIterator(0, 1);
    }

    public static Iterator<Integer> getSequentialIterator(int start, int increment) {
        return new InfiIter(start, increment);
    }

    public static Iterator<Integer> getRandomIterator() {
        return new RandomIter();
    }

    private static class InfiIter implements Iterator<Integer> {
        int index;
        int increment;

        InfiIter(int start, int increment) {
            if ((start < 0) || (increment <= 0)) {
                throw new IllegalArgumentException();
            }
            this.increment = increment;
            this.index = (start % ColorsGradient.colors.size());
        }

        public boolean hasNext() {
            return true;
        }

        public Integer next() {
            int v = ((Integer) ColorsGradient.rgbvalues.get(this.index)).intValue();
            this.index = ((this.index + this.increment) % ColorsGradient.colors.size());
            return Integer.valueOf(v);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class RandomIter implements Iterator<Integer> {
        public boolean hasNext() {
            return true;
        }

        public Integer next() {
            int index = (int) (Math.random() * ColorsGradient.rgbvalues.size()) % ColorsGradient.rgbvalues.size();
            return (Integer) ColorsGradient.rgbvalues.get(index);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


