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
        colors.put("crimson", 14423100);
        colors.put("lightpink", 16758465);
        colors.put("lightpink 1", 16756409);
        colors.put("lightpink 2", 15639213);
        colors.put("lightpink 3", 13470869);
        colors.put("lightpink 4", 9133925);
        colors.put("pink", 16761035);
        colors.put("pink 1", 16758213);
        colors.put("pink 2", 15641016);
        colors.put("pink 3", 13472158);
        colors.put("pink 4", 9134956);
        colors.put("palevioletred", 14381203);
        colors.put("palevioletred 1", 16745131);
        colors.put("palevioletred 2", 15628703);
        colors.put("palevioletred 3", 13461641);
        colors.put("palevioletred 4", 9127773);
        colors.put("lavenderblush 1 (lavenderblush)", 16773365);
        colors.put("lavenderblush 2", 15655141);
        colors.put("lavenderblush 3", 13484485);
        colors.put("lavenderblush 4", 9143174);
        colors.put("violetred 1", 16727702);
        colors.put("violetred 2", 15612556);
        colors.put("violetred 3", 13447800);
        colors.put("violetred 4", 9118290);
        colors.put("hotpink", 16738740);
        colors.put("hotpink 1", 16740020);
        colors.put("hotpink 2", 15624871);
        colors.put("hotpink 3", 13459600);
        colors.put("hotpink 4", 9124450);
        colors.put("raspberry", 8857175);
        colors.put("deeppink 1 (deeppink)", 16716947);
        colors.put("deeppink 2", 15602313);
        colors.put("deeppink 3", 13439094);
        colors.put("deeppink 4", 9112144);
        colors.put("maroon 1", 16725171);
        colors.put("maroon 2", 15610023);
        colors.put("maroon 3", 13445520);
        colors.put("maroon 4", 9116770);
        colors.put("mediumvioletred", 13047173);
        colors.put("violetred", 13639824);
        colors.put("orchid", 14315734);
        colors.put("orchid 1", 16745466);
        colors.put("orchid 2", 15629033);
        colors.put("orchid 3", 13461961);
        colors.put("orchid 4", 9127817);
        colors.put("thistle", 14204888);
        colors.put("thistle 1", 16769535);
        colors.put("thistle 2", 15651566);
        colors.put("thistle 3", 13481421);
        colors.put("thistle 4", 9141131);
        colors.put("plum 1", 16759807);
        colors.put("plum 2", 15642350);
        colors.put("plum 3", 13473485);
        colors.put("plum 4", 9135755);
        colors.put("plum", 14524637);
        colors.put("violet", 15631086);
        colors.put("magenta (fuchsia*)", 16711935);
        colors.put("magenta*", 16711935);
        colors.put("magenta 2", 15597806);
        colors.put("magenta 3", 13435085);
        colors.put("magenta 4 (darkmagenta)", 9109643);
        colors.put("purple*", 8388736);
        colors.put("mediumorchid", 12211667);
        colors.put("mediumorchid 1", 14706431);
        colors.put("mediumorchid 2", 13721582);
        colors.put("mediumorchid 3", 11817677);
        colors.put("mediumorchid 4", 8009611);
        colors.put("darkviolet", 9699539);
        colors.put("darkorchid", 10040012);
        colors.put("darkorchid 1", 12533503);
        colors.put("darkorchid 2", 11680494);
        colors.put("darkorchid 3", 10105549);
        colors.put("darkorchid 4", 6824587);
        colors.put("indigo", 4915330);
        colors.put("blueviolet", 9055202);
        colors.put("purple 1", 10170623);
        colors.put("purple 2", 9514222);
        colors.put("purple 3", 8201933);
        colors.put("purple 4", 5577355);
        colors.put("mediumpurple", 9662683);
        colors.put("mediumpurple 1", 11240191);
        colors.put("mediumpurple 2", 10451438);
        colors.put("mediumpurple 3", 9005261);
        colors.put("mediumpurple 4", 6113163);
        colors.put("darkslateblue", 4734347);
        colors.put("lightslateblue", 8679679);
        colors.put("mediumslateblue", 8087790);
        colors.put("slateblue", 6970061);
        colors.put("slateblue 1", 8613887);
        colors.put("slateblue 2", 8021998);
        colors.put("slateblue 3", 6904269);
        colors.put("slateblue 4", 4668555);
        colors.put("ghostwhite", 16316671);
        colors.put("lavender", 15132410);
        colors.put("blue*", 255);
        colors.put("blue 2", 238);
        colors.put("blue 3 (mediumblue)", 205);
        colors.put("blue 4 (darkblue)", 139);
        colors.put("navy*", 128);
        colors.put("midnightblue", 1644912);
        colors.put("cobalt", 4020651);
        colors.put("royalblue", 4286945);
        colors.put("royalblue 1", 4749055);
        colors.put("royalblue 2", 4419310);
        colors.put("royalblue 3", 3825613);
        colors.put("royalblue 4", 2572427);
        colors.put("cornflowerblue", 6591981);
        colors.put("lightsteelblue", 11584734);
        colors.put("lightsteelblue 1", 13296127);
        colors.put("lightsteelblue 2", 12374766);
        colors.put("lightsteelblue 3", 10663373);
        colors.put("lightsteelblue 4", 7240587);
        colors.put("lightslategray", 7833753);
        colors.put("slategray", 7372944);
        colors.put("slategray 1", 13034239);
        colors.put("slategray 2", 12178414);
        colors.put("slategray 3", 10467021);
        colors.put("slategray 4", 7109515);
        colors.put("dodgerblue 1 (dodgerblue)", 2003199);
        colors.put("dodgerblue 2", 1869550);
        colors.put("dodgerblue 3", 1602765);
        colors.put("dodgerblue 4", 1068683);
        colors.put("aliceblue", 15792383);
        colors.put("steelblue", 4620980);
        colors.put("steelblue 1", 6535423);
        colors.put("steelblue 2", 6073582);
        colors.put("steelblue 3", 5215437);
        colors.put("steelblue 4", 3564683);
        colors.put("lightskyblue", 8900346);
        colors.put("lightskyblue 1", 11592447);
        colors.put("lightskyblue 2", 10802158);
        colors.put("lightskyblue 3", 9287373);
        colors.put("lightskyblue 4", 6323083);
        colors.put("skyblue 1", 8900351);
        colors.put("skyblue 2", 8306926);
        colors.put("skyblue 3", 7120589);
        colors.put("skyblue 4", 4878475);
        colors.put("skyblue", 8900331);
        colors.put("deepskyblue 1 (deepskyblue)", 49151);
        colors.put("deepskyblue 2", 45806);
        colors.put("deepskyblue 3", 39629);
        colors.put("deepskyblue 4", 26763);
        colors.put("peacock", 3383753);
        colors.put("lightblue", 11393254);
        colors.put("lightblue 1", 12578815);
        colors.put("lightblue 2", 11722734);
        colors.put("lightblue 3", 10141901);
        colors.put("lightblue 4", 6849419);
        colors.put("powderblue", 11591910);
        colors.put("cadetblue 1", 10024447);
        colors.put("cadetblue 2", 9364974);
        colors.put("cadetblue 3", 8046029);
        colors.put("cadetblue 4", 5473931);
        colors.put("turquoise 1", 62975);
        colors.put("turquoise 2", 58862);
        colors.put("turquoise 3", 50637);
        colors.put("turquoise 4", 34443);
        colors.put("cadetblue", 6266528);
        colors.put("darkturquoise", 52945);
        colors.put("azure 1 (azure)", 15794175);
        colors.put("azure 2", 14741230);
        colors.put("azure 3", 12701133);
        colors.put("azure 4", 8620939);
        colors.put("lightcyan 1 (lightcyan)", 14745599);
        colors.put("lightcyan 2", 13758190);
        colors.put("lightcyan 3", 11849165);
        colors.put("lightcyan 4", 8031115);
        colors.put("paleturquoise 1", 12320767);
        colors.put("paleturquoise 2 (paleturquoise)", 11464430);
        colors.put("paleturquoise 3", 9883085);
        colors.put("paleturquoise 4", 6720395);
        colors.put("darkslategray", 3100495);
        colors.put("darkslategray 1", 9961471);
        colors.put("darkslategray 2", 9301742);
        colors.put("darkslategray 3", 7982541);
        colors.put("darkslategray 4", 5409675);
        colors.put("cyan / aqua*", 65535);
        colors.put("cyan*", 65535);
        colors.put("cyan 2", 61166);
        colors.put("cyan 3", 52685);
        colors.put("cyan 4 (darkcyan)", 35723);
        colors.put("teal*", 32896);
        colors.put("mediumturquoise", 4772300);
        colors.put("lightseagreen", 2142890);
        colors.put("manganeseblue", 239774);
        colors.put("turquoise", 4251856);
        colors.put("coldgrey", 8424071);
        colors.put("turquoiseblue", 51084);
        colors.put("aquamarine 1 (aquamarine)", 8388564);
        colors.put("aquamarine 2", 7794374);
        colors.put("aquamarine 3 (mediumaquamarine)", 6737322);
        colors.put("aquamarine 4", 4557684);
        colors.put("mediumspringgreen", 64154);
        colors.put("mintcream", 16121850);
        colors.put("springgreen", 65407);
        colors.put("springgreen 1", 61046);
        colors.put("springgreen 2", 52582);
        colors.put("springgreen 3", 35653);
        colors.put("mediumseagreen", 3978097);
        colors.put("seagreen 1", 5570463);
        colors.put("seagreen 2", 5172884);
        colors.put("seagreen 3", 4443520);
        colors.put("seagreen 4 (seagreen)", 3050327);
        colors.put("emeraldgreen", 51543);
        colors.put("mint", 12451017);
        colors.put("cobaltgreen", 4034880);
        colors.put("honeydew 1 (honeydew)", 15794160);
        colors.put("honeydew 2", 14741216);
        colors.put("honeydew 3", 12701121);
        colors.put("honeydew 4", 8620931);
        colors.put("darkseagreen", 9419919);
        colors.put("darkseagreen 1", 12713921);
        colors.put("darkseagreen 2", 11857588);
        colors.put("darkseagreen 3", 10210715);
        colors.put("darkseagreen 4", 6916969);
        colors.put("palegreen", 10025880);
        colors.put("palegreen 1", 10157978);
        colors.put("palegreen 2 (lightgreen)", 9498256);
        colors.put("palegreen 3", 8179068);
        colors.put("palegreen 4", 5540692);
        colors.put("limegreen", 3329330);
        colors.put("forestgreen", 2263842);
        colors.put("green 1 (lime*)", 65280);
        colors.put("lime*", 65280);
        colors.put("green 2", 60928);
        colors.put("green 3", 52480);
        colors.put("green 4", 35584);
        colors.put("green*", 32768);
        colors.put("darkgreen", 25600);
        colors.put("sapgreen", 3178516);
        colors.put("lawngreen", 8190976);
        colors.put("chartreuse 1 (chartreuse)", 8388352);
        colors.put("chartreuse 2", 7794176);
        colors.put("chartreuse 3", 6737152);
        colors.put("chartreuse 4", 4557568);
        colors.put("greenyellow", 11403055);
        colors.put("darkolivegreen 1", 13303664);
        colors.put("darkolivegreen 2", 12381800);
        colors.put("darkolivegreen 3", 10669402);
        colors.put("darkolivegreen 4", 7244605);
        colors.put("darkolivegreen", 5597999);
        colors.put("olivedrab", 7048739);
        colors.put("olivedrab 1", 12648254);
        colors.put("olivedrab 2", 11791930);
        colors.put("olivedrab 3 (yellowgreen)", 10145074);
        colors.put("olivedrab 4", 6916898);
        colors.put("ivory 1 (ivory)", 16777200);
        colors.put("ivory 2", 15658720);
        colors.put("ivory 3", 13487553);
        colors.put("ivory 4", 9145219);
        colors.put("beige", 16119260);
        colors.put("lightyellow 1 (lightyellow)", 16777184);
        colors.put("lightyellow 2", 15658705);
        colors.put("lightyellow 3", 13487540);
        colors.put("lightyellow 4", 9145210);
        colors.put("lightgoldenrodyellow", 16448210);
        colors.put("yellow 1 (yellow*)", 16776960);
        colors.put("yellow*", 16776960);
        colors.put("yellow 2", 15658496);
        colors.put("yellow 3", 13487360);
        colors.put("yellow 4", 9145088);
        colors.put("warmgrey", 8421481);
        colors.put("olive*", 8421376);
        colors.put("darkkhaki", 12433259);
        colors.put("khaki 1", 16774799);
        colors.put("khaki 2", 15656581);
        colors.put("khaki 3", 13485683);
        colors.put("khaki 4", 9143886);
        colors.put("khaki", 15787660);
        colors.put("palegoldenrod", 15657130);
        colors.put("lemonchiffon 1 (lemonchiffon)", 16775885);
        colors.put("lemonchiffon 2", 15657407);
        colors.put("lemonchiffon 3", 13486501);
        colors.put("lemonchiffon 4", 9144688);
        colors.put("lightgoldenrod 1", 16772235);
        colors.put("lightgoldenrod 2", 15654018);
        colors.put("lightgoldenrod 3", 13483632);
        colors.put("lightgoldenrod 4", 9142604);
        colors.put("banana", 14929751);
        colors.put("gold 1 (gold)", 16766720);
        colors.put("gold 2", 15649024);
        colors.put("gold 3", 13479168);
        colors.put("gold 4", 9139456);
        colors.put("cornsilk 1 (cornsilk)", 16775388);
        colors.put("cornsilk 2", 15657165);
        colors.put("cornsilk 3", 13486257);
        colors.put("cornsilk 4", 9144440);
        colors.put("goldenrod", 14329120);
        colors.put("goldenrod 1", 16761125);
        colors.put("goldenrod 2", 15643682);
        colors.put("goldenrod 3", 13474589);
        colors.put("goldenrod 4", 9136404);
        colors.put("darkgoldenrod", 12092939);
        colors.put("darkgoldenrod 1", 16759055);
        colors.put("darkgoldenrod 2", 15641870);
        colors.put("darkgoldenrod 3", 13473036);
        colors.put("darkgoldenrod 4", 9135368);
        colors.put("orange 1 (orange)", 16753920);
        colors.put("orange 2", 15636992);
        colors.put("orange 3", 13468928);
        colors.put("orange 4", 9132544);
        colors.put("floralwhite", 16775920);
        colors.put("oldlace", 16643558);
        colors.put("wheat", 16113331);
        colors.put("wheat 1", 16771002);
        colors.put("wheat 2", 15653038);
        colors.put("wheat 3", 13482646);
        colors.put("wheat 4", 9141862);
        colors.put("moccasin", 16770229);
        colors.put("papayawhip", 16773077);
        colors.put("blanchedalmond", 16772045);
        colors.put("navajowhite 1 (navajowhite)", 16768685);
        colors.put("navajowhite 2", 15650721);
        colors.put("navajowhite 3", 13480843);
        colors.put("navajowhite 4", 9140574);
        colors.put("eggshell", 16574153);
        colors.put("tan", 13808780);
        colors.put("brick", 10249759);
        colors.put("cadmiumyellow", 16750866);
        colors.put("antiquewhite", 16444375);
        colors.put("antiquewhite 1", 16773083);
        colors.put("antiquewhite 2", 15654860);
        colors.put("antiquewhite 3", 13484208);
        colors.put("antiquewhite 4", 9143160);
        colors.put("burlywood", 14596231);
        colors.put("burlywood 1", 16765851);
        colors.put("burlywood 2", 15648145);
        colors.put("burlywood 3", 13478525);
        colors.put("burlywood 4", 9139029);
        colors.put("bisque 1 (bisque)", 16770244);
        colors.put("bisque 2", 15652279);
        colors.put("bisque 3", 13481886);
        colors.put("bisque 4", 9141611);
        colors.put("melon", 14919785);
        colors.put("carrot", 15569185);
        colors.put("darkorange", 16747520);
        colors.put("darkorange 1", 16744192);
        colors.put("darkorange 2", 15627776);
        colors.put("darkorange 3", 13460992);
        colors.put("darkorange 4", 9127168);
        colors.put("orange", 16744448);
        colors.put("tan 1", 16753999);
        colors.put("tan 2", 15637065);
        colors.put("tan 3 (peru)", 13468991);
        colors.put("tan 4", 9132587);
        colors.put("linen", 16445670);
        colors.put("peachpuff 1 (peachpuff)", 16767673);
        colors.put("peachpuff 2", 15649709);
        colors.put("peachpuff 3", 13479829);
        colors.put("peachpuff 4", 9140069);
        colors.put("seashell 1 (seashell)", 16774638);
        colors.put("seashell 2", 15656414);
        colors.put("seashell 3", 13485503);
        colors.put("seashell 4", 9143938);
        colors.put("sandybrown", 16032864);
        colors.put("rawsienna", 13066516);
        colors.put("chocolate", 13789470);
        colors.put("chocolate 1", 16744228);
        colors.put("chocolate 2", 15627809);
        colors.put("chocolate 3", 13461021);
        colors.put("chocolate 4 (saddlebrown)", 9127187);
        colors.put("ivoryblack", 2696225);
        colors.put("flesh", 16743744);
        colors.put("cadmiumorange", 16736515);
        colors.put("burntsienna", 9057807);
        colors.put("sienna", 10506797);
        colors.put("sienna 1", 16745031);
        colors.put("sienna 2", 15628610);
        colors.put("sienna 3", 13461561);
        colors.put("sienna 4", 9127718);
        colors.put("lightsalmon 1 (lightsalmon)", 16752762);
        colors.put("lightsalmon 2", 15635826);
        colors.put("lightsalmon 3", 13468002);
        colors.put("lightsalmon 4", 9131842);
        colors.put("coral", 16744272);
        colors.put("orangered 1 (orangered)", 16729344);
        colors.put("orangered 2", 15613952);
        colors.put("orangered 3", 13448960);
        colors.put("orangered 4", 9118976);
        colors.put("sepia", 6170130);
        colors.put("darksalmon", 15308410);
        colors.put("salmon 1", 16747625);
        colors.put("salmon 2", 15630946);
        colors.put("salmon 3", 13463636);
        colors.put("salmon 4", 9129017);
        colors.put("coral 1", 16740950);
        colors.put("coral 2", 15624784);
        colors.put("coral 3", 13458245);
        colors.put("coral 4", 9125423);
        colors.put("burntumber", 9057060);
        colors.put("tomato 1 (tomato)", 16737095);
        colors.put("tomato 2", 15621186);
        colors.put("tomato 3", 13455161);
        colors.put("tomato 4", 9123366);
        colors.put("salmon", 16416882);
        colors.put("mistyrose 1 (mistyrose)", 16770273);
        colors.put("mistyrose 2", 15652306);
        colors.put("mistyrose 3", 13481909);
        colors.put("mistyrose 4", 9141627);
        colors.put("snow 1 (snow)", 16775930);
        colors.put("snow 2", 15657449);
        colors.put("snow 3", 13486537);
        colors.put("snow 4", 9144713);
        colors.put("rosybrown", 12357519);
        colors.put("rosybrown 1", 16761281);
        colors.put("rosybrown 2", 15643828);
        colors.put("rosybrown 3", 13474715);
        colors.put("rosybrown 4", 9136489);
        colors.put("lightcoral", 15761536);
        colors.put("indianred", 13458524);
        colors.put("indianred 1", 16738922);
        colors.put("indianred 2", 15623011);
        colors.put("indianred 4", 9124410);
        colors.put("indianred 3", 13456725);
        colors.put("brown", 10824234);
        colors.put("brown 1", 16728128);
        colors.put("brown 2", 15612731);
        colors.put("brown 3", 13447987);
        colors.put("brown 4", 9118499);
        colors.put("firebrick", 11674146);
        colors.put("firebrick 1", 16724016);
        colors.put("firebrick 2", 15608876);
        colors.put("firebrick 3", 13444646);
        colors.put("firebrick 4", 9116186);
        colors.put("red 1 (red*)", 16711680);
        colors.put("red*", 16711680);
        colors.put("red 2", 15597568);
        colors.put("red 3", 13434880);
        colors.put("red 4 (darkred)", 9109504);
        colors.put("maroon*", 8388608);
        colors.put("sgi beet", 9320590);
        colors.put("sgi slateblue", 7434694);
        colors.put("sgi lightblue", 8232640);
        colors.put("sgi teal", 3706510);
        colors.put("sgi chartreuse", 7456369);
        colors.put("sgi olivedrab", 9342520);
        colors.put("sgi brightgray", 12960170);
        colors.put("sgi salmon", 13005169);
        colors.put("sgi darkgray", 5592405);
        colors.put("sgi gray 12", 1973790);
        colors.put("sgi gray 16", 2631720);
        colors.put("sgi gray 32", 5329233);
        colors.put("sgi gray 36", 5987163);
        colors.put("sgi gray 52", 8684676);
        colors.put("sgi gray 56", 9342606);
        colors.put("sgi lightgray", 11184810);
        colors.put("sgi gray 72", 12040119);
        colors.put("sgi gray 76", 12698049);
        colors.put("sgi gray 92", 15395562);
        colors.put("sgi gray 96", 16053492);
        colors.put("white*", 16777215);
        colors.put("white smoke (gray 96)", 16119285);
        colors.put("gainsboro", 14474460);
        colors.put("lightgrey", 13882323);
        colors.put("silver*", 12632256);
        colors.put("darkgray", 11119017);
        colors.put("gray*", 8421504);
        colors.put("dimgray (gray 42)", 6908265);
        colors.put("black*", 0);
        colors.put("gray 99", 16579836);
        colors.put("gray 98", 16448250);
        colors.put("gray 97", 16250871);
        colors.put("white smoke (gray 96)", 16119285);
        colors.put("gray 95", 15921906);
        colors.put("gray 94", 15790320);
        colors.put("gray 93", 15592941);
        colors.put("gray 92", 15461355);
        colors.put("gray 91", 15263976);
        colors.put("gray 90", 15066597);
        colors.put("gray 89", 14935011);
        colors.put("gray 88", 14737632);
        colors.put("gray 87", 14606046);
        colors.put("gray 86", 14408667);
        colors.put("gray 85", 14277081);
        colors.put("gray 84", 14079702);
        colors.put("gray 83", 13948116);
        colors.put("gray 82", 13750737);
        colors.put("gray 81", 13619151);
        colors.put("gray 80", 13421772);
        colors.put("gray 79", 13224393);
        colors.put("gray 78", 13092807);
        colors.put("gray 77", 12895428);
        colors.put("gray 76", 12763842);
        colors.put("gray 75", 12566463);
        colors.put("gray 74", 12434877);
        colors.put("gray 73", 12237498);
        colors.put("gray 72", 12105912);
        colors.put("gray 71", 11908533);
        colors.put("gray 70", 11776947);
        colors.put("gray 69", 11579568);
        colors.put("gray 68", 11382189);
        colors.put("gray 67", 11250603);
        colors.put("gray 66", 11053224);
        colors.put("gray 65", 10921638);
        colors.put("gray 64", 10724259);
        colors.put("gray 63", 10592673);
        colors.put("gray 62", 10395294);
        colors.put("gray 61", 10263708);
        colors.put("gray 60", 10066329);
        colors.put("gray 59", 9868950);
        colors.put("gray 58", 9737364);
        colors.put("gray 57", 9539985);
        colors.put("gray 56", 9408399);
        colors.put("gray 55", 9211020);
        colors.put("gray 54", 9079434);
        colors.put("gray 53", 8882055);
        colors.put("gray 52", 8750469);
        colors.put("gray 51", 8553090);
        colors.put("gray 50", 8355711);
        colors.put("gray 49", 8224125);
        colors.put("gray 48", 8026746);
        colors.put("gray 47", 7895160);
        colors.put("gray 46", 7697781);
        colors.put("gray 45", 7566195);
        colors.put("gray 44", 7368816);
        colors.put("gray 43", 7237230);
        colors.put("gray 42", 7039851);
        colors.put("dimgray (gray 42)", 6908265);
        colors.put("gray 40", 6710886);
        colors.put("gray 39", 6513507);
        colors.put("gray 38", 6381921);
        colors.put("gray 37", 6184542);
        colors.put("gray 36", 6052956);
        colors.put("gray 35", 5855577);
        colors.put("gray 34", 5723991);
        colors.put("gray 33", 5526612);
        colors.put("gray 32", 5395026);
        colors.put("gray 31", 5197647);
        colors.put("gray 30", 5066061);
        colors.put("gray 29", 4868682);
        colors.put("gray 28", 4671303);
        colors.put("gray 27", 4539717);
        colors.put("gray 26", 4342338);
        colors.put("gray 25", 4210752);
        colors.put("gray 24", 4013373);
        colors.put("gray 23", 3881787);
        colors.put("gray 22", 3684408);
        colors.put("gray 21", 3552822);
        colors.put("gray 20", 3355443);
        colors.put("gray 19", 3158064);
        colors.put("gray 18", 3026478);
        colors.put("gray 17", 2829099);
        colors.put("gray 16", 2697513);
        colors.put("gray 15", 2500134);
        colors.put("gray 14", 2368548);
        colors.put("gray 13", 2171169);
        colors.put("gray 12", 2039583);
        colors.put("gray 11", 1842204);
        colors.put("gray 10", 1710618);
        colors.put("gray 9", 1513239);
        colors.put("gray 8", 1315860);
        colors.put("gray 7", 1184274);
        colors.put("gray 6", 986895);
        colors.put("gray 5", 855309);
        colors.put("gray 4", 657930);
        colors.put("gray 3", 526344);
        colors.put("gray 2", 328965);
        colors.put("gray 1", 197379);
    }

    private static List<Integer> rgbvalues = new ArrayList<>(colors.values());

    private static int get(String colorName, boolean loose) {
        if (colorName == null) {
            return 0;
        }
        colorName = Strings.trim(colorName).toLowerCase();
        Integer v = colors.get(colorName);
        if ((v == null) && (!colorName.endsWith("*"))) {
            v = colors.get(colorName + "*");
        }
        if ((v == null) && (loose)) {
            for (String key : colors.keySet()) {
                if (key.contains(colorName)) {
                    v = colors.get(key);
                    break;
                }
            }
        }
        return v == null ? 0 : v;
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
            int v = (Integer) ColorsGradient.rgbvalues.get(this.index);
            this.index = ((this.index + this.increment) % ColorsGradient.colors.size());
            return v;
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
            return ColorsGradient.rgbvalues.get(index);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


