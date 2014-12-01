/*
 * QUe
 * 
 * Copyright (c) 2014 Quality and Usability Lab,
 * Telekom Innvation Laboratories, TU Berlin. All rights reserved.
 * https://github.com/QULab/QUe-Android
 * 
 * This file is part of QUe.
 * 
 * QUe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * QUe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with QUe. If not, see <http://www.gnu.org/licenses/>.
 */
package de.tel.quenference.db.dao;

import android.content.ContentValues;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.Entity;
import java.io.Serializable;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */

public class SQLQuery implements Serializable {

    /**
     * The SQL LIKE expression which will be used to search a value
     * which should be like the given value.
     */
    public static final String SQL_SEARCH_LIKE = " LIKE ? ";

    public static final String SQL_SEARCH_EQUAL = " = ?";
    /**
     * The SQL OR operator which will be used for the where clause.
     */
    public static final String SQL_OR = " OR ";

    public static final String SQL_ASC_ORDER = " ASC";

    public static final String SQL_DESC_ORDER = " DESC";

    /**
     * The SQL percentage operator which will be used with the LIKE operator.
     */
    public static final String SQL_VARIABLE_EXP = "%";


    /**
     * The selection for the author search query (where clause).
     */
    private static final String authorSelection =
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_AFFILIATION + SQL_SEARCH_LIKE + SQL_OR
                    + ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME + SQL_SEARCH_LIKE + SQL_OR
                    + ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME + SQL_SEARCH_LIKE;

    private static final String authorOrder =
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME + SQL_ASC_ORDER;
    /**
     * The selection for the paper search query (where clause).
     */
    private static final String paperSelection =
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_TITLE + SQL_SEARCH_LIKE + SQL_OR
                    + ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR + SQL_SEARCH_LIKE + SQL_OR
                    + ConferenceDBContract.ConferencePaper.COLUMN_NAME_ABSTRACT + SQL_SEARCH_LIKE;


    private static final String paperIDSelection =
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + SQL_SEARCH_EQUAL;

    private static final String paperIDOrderBy =
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + SQL_ASC_ORDER;


    /**
     * The selection for the session search query (where clause).
     */
    private static final String sessionSelection =
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE + SQL_SEARCH_LIKE + SQL_OR
                    + ConferenceDBContract.ConferenceSession.COLUMN_NAME_CHAIR + SQL_SEARCH_LIKE + SQL_OR
                    + ConferenceDBContract.ConferenceSession.COLUMN_NAME_CO_CHAIR + SQL_SEARCH_LIKE;
    private static final String paperSessionOrder =
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE + SQL_ASC_ORDER;
    
    private static final String sessionDaySelection =
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_DAY + SQL_SEARCH_EQUAL;

    private static final String sessionDayOrderBy =
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME + SQL_ASC_ORDER;

    private static final String sessionIDSelection =
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID + SQL_SEARCH_EQUAL;

    private static final String sessionIDOrderBy =
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID + SQL_ASC_ORDER;

    public static SQLQuery getPaperQuery(String arg) {
        arg = SQL_VARIABLE_EXP + arg + SQL_VARIABLE_EXP;
        SQLQuery query = new SQLQuery(paperSelection, Entity.PAPER);
        query.setOrderBy(paperSessionOrder);
        query.setSelectionArgs(new String[]{arg, arg, arg});
        System.out.println("paper query is: " + arg);
        return query;
    }

    public static SQLQuery getAuthorQuery(String arg) {
        arg = SQL_VARIABLE_EXP + arg + SQL_VARIABLE_EXP;
        SQLQuery query = new SQLQuery(authorSelection, Entity.AUTHOR);
        query.setOrderBy(authorOrder);
        query.setSelectionArgs(new String[]{arg, arg, arg});

        return query;
    }

    public static SQLQuery getSessionQuery(String arg) {
        arg = SQL_VARIABLE_EXP + arg + SQL_VARIABLE_EXP;
        SQLQuery query = new SQLQuery(sessionSelection, Entity.SESSION);
        query.setOrderBy(paperSessionOrder);
        query.setSelectionArgs(new String[]{arg, arg, arg});
        return query;
    }

    public static SQLQuery getSessionDateQuery(String arg) {
        SQLQuery query = new SQLQuery(sessionDaySelection, Entity.SESSION);
        query.setOrderBy(sessionDayOrderBy);
        query.setSelectionArgs(arg);
        return query;
    }

    public static SQLQuery getSessionIDQuery(String arg) {
        SQLQuery query = new SQLQuery(sessionIDSelection, Entity.SESSION);
        query.setOrderBy(sessionIDOrderBy);
        query.setSelectionArgs(arg);
        return query;
    }

    private String selection;
    private String[] selectionArgs;
    private Entity selectedEntity;
    private String orderBy;
    private String groupBy;
    private String having;
    private ContentValues values;

    public SQLQuery(String selection, Entity selectedEntity) {
        this.selection = selection;
        this.selectedEntity = selectedEntity;
    }


    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(String... selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getHaving() {
        return having;
    }

    public void setHaving(String having) {
        this.having = having;
    }

    public ContentValues getValues() {
        return values;
    }

    public void setValues(ContentValues values) {
        this.values = values;
    }

    public static class Builder {

        private SQLQuery query;

        public Builder(String select, Entity entity) {
            query = new SQLQuery(select, entity);
        }

        public Builder addOrder(String order) {
            query.setOrderBy(order);
            return this;
        }

        public Builder addGroupBy(String group) {
            query.setGroupBy(group);
            return this;
        }

        public Builder addHaving(String h) {
            query.setHaving(h);
            return this;
        }

        public Builder addArgs(String... selectArgs) {
            query.setSelectionArgs(selectArgs);
            return this;
        }

        public Builder addValues(String columnName, String value) {
            ContentValues v = query.getValues();
            if (v == null)
                v = new ContentValues();

            v.put(columnName, value);
            query.setValues(v);
            return this;
        }

        public SQLQuery build() {
            return query;
        }

    }


}
