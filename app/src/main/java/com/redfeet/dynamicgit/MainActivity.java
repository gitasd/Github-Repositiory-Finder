package com.redfeet.dynamicgit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redfeet.dynamicgit.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView textview;
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.btnSerch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchterm = binding.etSeachTerm.getText().toString();

                if(TextUtils.isEmpty(searchterm)){
                    return;
                }

                URL searchUrl = NetworkUtil.buildRepoSearch(searchterm);

                new GitHubQueryTask().execute(searchUrl);

            }
        });
    }
    public class GitHubQueryTask extends AsyncTask<URL,Void,String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String githubsearchresults = null;

            try {
                githubsearchresults = NetworkUtil.getResponceFromHTTP(searchUrl);
            }catch (IOException e){
                e.printStackTrace();
            }

            return githubsearchresults;
        }

        @Override
        protected void onPostExecute(String str) {
            if(str!=null && !str.equals("")){
                parseAndDisplay(str);
            }
        }

        private void parseAndDisplay(String json){
            List<GithubRepository> repositories = NetworkUtil.parseGithubRepos(json);
            StringBuilder sb = new StringBuilder();

            for(GithubRepository repo_itr:repositories){
                sb.append("ID: ").append(repo_itr.getId()).append("\n").append("Name: ").append(repo_itr.getName())
                        .append("\n\n");
            }

            binding.tvOutput.setText(sb.toString());
        }
    }

}