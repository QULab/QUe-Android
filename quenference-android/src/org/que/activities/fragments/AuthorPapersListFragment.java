/*
 * Copyright 2014 Quality and Usability Lab, Telekom Innvation Laboratories, TU Berlin..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.que.activities.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import org.que.db.ConferenceDBContract;
import org.que.db.ConferenceDBHelper;
import org.que.db.Entity;
import org.que.db.dao.AsyncDBListReader;
import org.que.db.dao.ConferenceDAO;
import org.que.db.dao.SQLQuery;
import org.que.db.entities.PaperAuthorsEntity;
import org.que.db.entities.PaperEntity;
import org.que.util.EntityListAdapter;

/**
 * Represents the Fragment which shows the Papers for an given Author.
 * Extends the general SearchFragmentVP and overrides the search method.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AuthorPapersListFragment extends SearchFragmentVP {
  @Override
  protected void search() {
    if (query == null) {
      String selection = ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID + SQLQuery.SQL_SEARCH_EQUAL;
      query = new SQLQuery(selection, Entity.PAPER_AUTHORS, ConferenceDAO.PAPER_AUTHORS_COLUMNS);
      query.setSelectionArgs(new String[]{"1"});
    }
    ConferenceDAO.getSelection(getActivity(), new AsyncDBListReader.PostExecuteJob() {

      public void doJob(final List result) {
        new AsyncTask<Context, Void,  List<PaperEntity>>() {

          @Override
          protected List<PaperEntity> doInBackground(Context... contexts) {
            List<PaperEntity> papers = new ArrayList<PaperEntity>();
            final ConferenceDBHelper DB_HELPER = new ConferenceDBHelper(contexts[0]);
            SQLiteDatabase db = DB_HELPER.getReadableDatabase();
            for (Object obj : result) {
              String select = ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + SQLQuery.SQL_SEARCH_EQUAL;
              SQLQuery q = new SQLQuery(select, Entity.PAPER, ConferenceDAO.PAPER_COLUMNS);
              q.setSelectionArgs(((PaperAuthorsEntity) obj).getPaperID().toString());
              
              Cursor c = db.query(ConferenceDBContract.ConferencePaper.TABLE_NAME,
                                  ConferenceDAO.PAPER_COLUMNS, q.getSelection(),
                                  q.getSelectionArgs(), q.getGroupBy(),
                                  q.getHaving(), q.getHaving());
              c.moveToFirst();  
              while (!c.isAfterLast()) {
                PaperEntity p = (PaperEntity)ConferenceDAO.getPaperCursorExtractor().extract(c);
                papers.add(p);
                c.moveToNext();
              }
              c.close();
            }
            DB_HELPER.close();
            return papers;
          }

          @Override
          protected void onPostExecute(List<PaperEntity> result) {
            super.onPostExecute(result);
            ((EntityListAdapter) getListAdapter()).setResults(result);
            ((EntityListAdapter)getListAdapter()).notifyDataSetChanged();
          }
        }.execute(getActivity());

      }
    }, query);
    
  }
  
  
  
}
