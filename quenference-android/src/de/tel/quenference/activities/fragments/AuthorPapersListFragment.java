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

package de.tel.quenference.activities.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.ConferenceDBHelper;
import de.tel.quenference.db.dao.AsyncDBListReader;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.dao.SQLQuery;
import de.tel.quenference.db.entities.PaperAuthorsEntity;
import de.tel.quenference.db.entities.PaperEntity;
import de.tel.quenference.util.EntityListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AuthorPapersListFragment extends SearchFragmentVP {
  @Override
  protected void search() {
    if (query == null) {
      String selection = ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID + SQLQuery.SQL_SEARCH_EQUAL;
      query = new SQLQuery(selection, ConferenceDAO.Entity.PAPER_AUTHORS);
      query.setSelectionArgs(new String[]{"1"});
    }
    ConferenceDAO.getSelection(getActivity(), query.getSelectedEntity(), new AsyncDBListReader.PostExecuteJob() {

      public void doJob(final List result) {
        new AsyncTask<Context, Void,  List<PaperEntity>>() {

          @Override
          protected List<PaperEntity> doInBackground(Context... contexts) {
            List<PaperEntity> papers = new ArrayList<PaperEntity>();
            final ConferenceDBHelper DB_HELPER = new ConferenceDBHelper(contexts[0]);
            SQLiteDatabase db = DB_HELPER.getReadableDatabase();
            for (Object obj : result) {
              String select = ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + SQLQuery.SQL_SEARCH_EQUAL;
              SQLQuery.Builder builder = new SQLQuery.Builder(select, ConferenceDAO.Entity.PAPER);
              builder.addArgs(((PaperAuthorsEntity) obj).getPaperID().toString());
              SQLQuery q = builder.build();
              
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
    }, query.getSelection(), query.getSelectionArgs(), null, null, query.getOrderBy()/*ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID + " ASC"*/);
    
  }
  
  
  
}
