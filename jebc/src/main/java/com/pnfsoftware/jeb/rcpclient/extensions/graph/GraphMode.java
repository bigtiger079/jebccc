
package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.util.format.Strings;

public class GraphMode {
    int id;
    String name;
    String description;

    public GraphMode(int id, String name, String description) {
        this.id = id;
        this.name = Strings.safe(name, "Mode " + id);
        this.description = Strings.safe(description);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.name;
    }
}


