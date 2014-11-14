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
package de.tel.quenference.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import de.tel.quenference.db.ConferenceDBHelper;
import de.tel.quenference.db.dao.ConferenceDAO;

/**
 * The splash screen of the application.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class SplashActivity extends Activity {

    /**
     * The time how long the splash screen is shown.
     */
    private static final Long SPLASH_TIME = 2500l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_view);
        ImageView iv = (ImageView) findViewById(R.id.splash_image);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_to_bottom);
        iv.setAnimation(animation);

        ConferenceDAO.updateDB(this);
//        ConferenceDBHelper dbHelper = new ConferenceDBHelper(getApplicationContext());
//        dbHelper.createDataBase();
        new Handler().postDelayed(new Runnable() {

            public void run() {
                Intent i = new Intent(SplashActivity.this, MainNavigationActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME);
    }

}
