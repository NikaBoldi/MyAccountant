package com.mikeos.demo.myaccountant.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Arrays;
import java.util.List;

import nl.qbusict.cupboard.convert.EntityConverter;

/**
 From Mike Osipov: Copied from cupboard sources for using in {@link com.mikeos.demo.myaccountant.model.DbModel#fromCursor(Cursor, Class)}


 * A cursor that guarantees that it will return columns of the wrapped cursor in the requested order and with the requested casing.
 * This cursor is passed to {@link EntityConverter#fromCursor(Cursor)} so that the converter does not have to do any look up from column to translation:
 * it can just assume that the column at a certain index is the same as returned from {@link EntityConverter#getColumns()} at the same index.
 */
public class PreferredColumnOrderCursorWrapper extends CursorWrapper {

    private String[] mColumns;
    private final int[] mColumnMap;

    public PreferredColumnOrderCursorWrapper(Cursor cursor, String[] columns) {
        super(cursor);
        this.mColumns = columns;
        this.mColumnMap = new int[columns.length];
        Arrays.fill(mColumnMap, -1);
        this.mColumns = remapColumns(cursor.getColumnNames(), columns);
    }

    public PreferredColumnOrderCursorWrapper(Cursor cursor, List<EntityConverter.Column> columns) {
        this(cursor, toColumNames(columns));
    }

    private static String[] toColumNames(List<EntityConverter.Column> columns) {
        String[] cols = new String[columns.size()];
        for (int i = cols.length - 1; i >= 0; i--) {
            cols[i] = columns.get(i).name;
        }
        return cols;
    }

    private String[] remapColumns(String[] cursorColumns, String[] columns) {
        int last = 0;
        for (int i = 0; i < columns.length; i++) {
            int index = super.getColumnIndex(columns[i]);
            mColumnMap[i] = index;
            if (index != -1) {
                last = i;
            }
        }
        if (last + 1 < columns.length) {
            String[] newCols = new String[last + 1];
            System.arraycopy(columns, 0, newCols, 0, last + 1);
            columns = newCols;
        }
        return columns;
    }

    @Override
    public String[] getColumnNames() {
        return mColumns;
    }

    @Override
    public short getShort(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return 0;
        }
        return super.getShort(index);
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return null;
        }
        return super.getBlob(index);
    }

    @Override
    public double getDouble(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return 0;
        }
        return super.getDouble(index);
    }

    @Override
    public float getFloat(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return 0;
        }
        return super.getFloat(index);
    }

    @Override
    public int getInt(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return 0;
        }
        return super.getInt(index);
    }

    @Override
    public long getLong(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return 0;
        }
        return super.getLong(index);
    }

    @Override
    public String getString(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return null;
        }
        return super.getString(index);
    }

    @Override
    public boolean isNull(int columnIndex) {
        int index = mColumnMap[columnIndex];
        if (index == -1) {
            return true;
        }
        return super.isNull(index);
    }

    @Override
    public int getColumnCount() {
        return mColumns.length;
    }

    /**
     * Overridden as a warning and will throw an Exception. Since this cursor will remap the underlying cursor, the
     * column index for each column will be already known.
     */
    @Override
    public int getColumnIndex(String columnName) {
        throw new RuntimeException("Don't use getColumnIndex(), but use the indices supplied in the constructor.\nFor use in an EntityConverter, the columns and indices are always in the same order as returned from EntityConverter.getColumns()");
    }

    /**
     * Overridden as a warning and will throw an Exception. Since this cursor will remap the underlying cursor, the
     * column index for each column will be already known.
     */
    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        throw new RuntimeException("Don't use getColumnIndex(), but use the indices supplied in the constructor.\nFor use in an EntityConverter, the columns and indices are always in the same order as returned from EntityConverter.getColumns()");
    }
}
