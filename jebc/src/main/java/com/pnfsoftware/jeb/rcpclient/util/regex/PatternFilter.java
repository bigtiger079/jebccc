
package com.pnfsoftware.jeb.rcpclient.util.regex;


import com.pnfsoftware.jeb.util.format.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternFilter
        implements IPattern {
    private Pattern mainRegexPattern;
    private IPatternMatcher patternMatcher;
    private String[] titleColumns;
    private Map<Integer, Pattern> patternByColumn = new HashMap();


    public PatternFilter(IPatternMatcher patternMatcher, String pattern, String[] titleColumns) {

        this.patternMatcher = patternMatcher;

        this.titleColumns = titleColumns;

        if (titleColumns == null) {

            this.mainRegexPattern = Pattern.compile(pattern, 98);

        } else {

            this.patternByColumn = new HashMap();


            for (int i = 0; i < titleColumns.length; i++) {

                if (titleColumns[i] != null) {


                    int filterByColumn = -1;

                    String columnTitle = titleColumns[i].toLowerCase();

                    String startType = columnTitle + ":\"";

                    if (pattern.startsWith(startType)) {

                        filterByColumn = 0;

                    } else {

                        filterByColumn = pattern.indexOf(" " + startType);

                        if (filterByColumn != -1) {

                            filterByColumn++;

                        }

                    }

                    if (filterByColumn != -1) {

                        int endIndex = -1;

                        do {

                            int searchFrom = endIndex == -1 ? filterByColumn + columnTitle.length() + 2 : endIndex + 1;

                            endIndex = pattern.indexOf("\"", searchFrom);

                        }
                        while ((endIndex != -1) && (pattern.charAt(endIndex - 1) == '\\'));

                        if (endIndex != -1) {

                            String intermediatePattern = pattern.substring(filterByColumn + columnTitle.length() + 2, endIndex);


                            pattern = pattern.substring(0, filterByColumn).trim() + " " + pattern.substring(endIndex + 1).trim();

                            this.patternByColumn.put(
                                    Integer.valueOf(i),
                                    Pattern.compile(intermediatePattern, 114));

                        }

                    }

                }

            }


            this.mainRegexPattern = Pattern.compile(pattern.trim(), 98);

        }

    }


    public boolean match(Object element, Object[] list) {

        if (!this.patternByColumn.isEmpty()) {

            for (Map.Entry<Integer, Pattern> entry : this.patternByColumn.entrySet()) {

                String iColumn = Strings.safe(this.patternMatcher.getValueProvider().getStringAt(element, ((Integer) entry.getKey()).intValue()));

                iColumn = iColumn.replace("\"", "\\\"");

                boolean match = ((Pattern) entry.getValue()).matcher(iColumn).find();

                if (!match) {

                    return false;

                }

            }

        }


        return this.patternMatcher.match(this.mainRegexPattern, element);

    }


    public IPattern createInstance(String filterString) {

        return new PatternFilter(this.patternMatcher, filterString, this.titleColumns);

    }

}


