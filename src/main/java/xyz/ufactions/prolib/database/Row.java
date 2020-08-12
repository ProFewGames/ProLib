package xyz.ufactions.prolib.database;

import xyz.ufactions.prolib.database.column.Column;

import java.util.HashMap;

public class Row {
    public HashMap<String, Column<?>> Columns = new HashMap<>();
}
