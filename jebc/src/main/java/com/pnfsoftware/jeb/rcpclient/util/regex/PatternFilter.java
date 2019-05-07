/*     */
package com.pnfsoftware.jeb.rcpclient.util.regex;
/*     */
/*     */

import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;

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
/*     */
/*     */ public class PatternFilter
        /*     */ implements IPattern
        /*     */ {
    /*     */   private Pattern mainRegexPattern;
    /*     */   private IPatternMatcher patternMatcher;
    /*     */   private String[] titleColumns;
    /*  31 */   private Map<Integer, Pattern> patternByColumn = new HashMap();

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
    public PatternFilter(IPatternMatcher patternMatcher, String pattern, String[] titleColumns)
    /*     */ {
        /*  47 */
        this.patternMatcher = patternMatcher;
        /*  48 */
        this.titleColumns = titleColumns;
        /*  49 */
        if (titleColumns == null) {
            /*  50 */
            this.mainRegexPattern = Pattern.compile(pattern, 98);
            /*     */
        }
        /*     */
        else
            /*     */ {
            /*  54 */
            this.patternByColumn = new HashMap();
            /*     */
            /*     */
            /*  57 */
            for (int i = 0; i < titleColumns.length; i++) {
                /*  58 */
                if (titleColumns[i] != null)
                    /*     */ {
                    /*     */
                    /*     */
                    /*  62 */
                    int filterByColumn = -1;
                    /*  63 */
                    String columnTitle = titleColumns[i].toLowerCase();
                    /*  64 */
                    String startType = columnTitle + ":\"";
                    /*  65 */
                    if (pattern.startsWith(startType)) {
                        /*  66 */
                        filterByColumn = 0;
                        /*     */
                    }
                    /*     */
                    else {
                        /*  69 */
                        filterByColumn = pattern.indexOf(" " + startType);
                        /*  70 */
                        if (filterByColumn != -1) {
                            /*  71 */
                            filterByColumn++;
                            /*     */
                        }
                        /*     */
                    }
                    /*  74 */
                    if (filterByColumn != -1)
                        /*     */ {
                        /*  76 */
                        int endIndex = -1;
                        /*     */
                        do {
                            /*  78 */
                            int searchFrom = endIndex == -1 ? filterByColumn + columnTitle.length() + 2 : endIndex + 1;
                            /*  79 */
                            endIndex = pattern.indexOf("\"", searchFrom);
                            /*     */
                        }
                        /*  81 */             while ((endIndex != -1) && (pattern.charAt(endIndex - 1) == '\\'));
                        /*  82 */
                        if (endIndex != -1) {
                            /*  83 */
                            String intermediatePattern = pattern.substring(filterByColumn + columnTitle.length() + 2, endIndex);
                            /*     */
                            /*     */
                            /*  86 */
                            pattern = pattern.substring(0, filterByColumn).trim() + " " + pattern.substring(endIndex + 1).trim();
                            /*  87 */
                            this.patternByColumn.put(
                                    /*  88 */                 Integer.valueOf(i),
                                    /*  89 */                 Pattern.compile(intermediatePattern, 114));
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
            /*     */
            /*  96 */
            this.mainRegexPattern = Pattern.compile(pattern.trim(), 98);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public boolean match(Object element, Object[] list)
    /*     */ {
        /* 104 */
        if (!this.patternByColumn.isEmpty()) {
            /* 105 */
            for (Map.Entry<Integer, Pattern> entry : this.patternByColumn.entrySet()) {
                /* 106 */
                String iColumn = Strings.safe(this.patternMatcher.getValueProvider().getStringAt(element, ((Integer) entry.getKey()).intValue()));
                /* 107 */
                iColumn = iColumn.replace("\"", "\\\"");
                /* 108 */
                boolean match = ((Pattern) entry.getValue()).matcher(iColumn).find();
                /* 109 */
                if (!match) {
                    /* 110 */
                    return false;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 115 */
        return this.patternMatcher.match(this.mainRegexPattern, element);
        /*     */
    }

    /*     */
    /*     */
    public IPattern createInstance(String filterString)
    /*     */ {
        /* 120 */
        return new PatternFilter(this.patternMatcher, filterString, this.titleColumns);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\regex\PatternFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */