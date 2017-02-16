package com.jeremy.estiam.appliandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.jeremy.estiam.appliandroid.api.ApiService;
import com.jeremy.estiam.appliandroid.api.NoSSLv3SocketFactory;
import com.jeremy.estiam.appliandroid.api.ServiceGenerator;
import com.jeremy.estiam.appliandroid.models.User;
import com.jeremy.estiam.appliandroid.models.UserConnection;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity{

    @BindView(R.id.login_editText)
    EditText loginView;
    @BindView(R.id.password_edittext)
    EditText passwordView;

    User user= new User();
    UserConnection userConnection = new UserConnection();
    String password;
    String login;
    boolean coGood = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = this.getSharedPreferences("InfosUtilisateur", Context.MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRANCE);
        Date date = new Date();
        String dateStr = sharedPreferences.getString("CreateDate", "NULL");
        if(!dateStr.equals("NULL")){
            Date date2 = null;
            try {
                date2 = dateFormat.parse(dateStr);

                Long date3 = date.getTime()-date2.getTime();
                System.out.println(date3);
                if (date3 < 86400000 ) {
                    Intent intent = new Intent(this, RecyclerActivity.class);
                    startActivity(intent);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        ButterKnife.bind(this);


    }

    @OnClick(R.id.Sinscrire_button)
    public void onClickSinscrire(View view) {
        Intent intent = new Intent(this, InscriptionActivity.class);
        intent.putExtra("idUser", user.getId());
        startActivity(intent);

    }

    @OnClick(R.id.connect_button)
    public void onClickConnexion(View view) {
        final boolean[] response = {true};
        password=passwordView.getText().toString();
        login=loginView.getText().toString();



        if (!password.equals("") && !login.equals("")) {
            userConnection.setPassword(password);
            userConnection.setPseudo(login);

            UserTask userTask = new UserTask();
            userTask.execute( view);



        } else {
            if(passwordView.getText().toString().equals("")) passwordView.setError("Veuillez saisir votre mot de passe  ");
            if(loginView.getText().toString().equals("")) loginView.setError("Veuillez saisir votre login  ");
        }


    }

    public class UserTask extends AsyncTask<View, Void , User> {
        @Override
        protected User doInBackground( View... view ) {

            ApiService apiService = new ServiceGenerator().createService(ApiService.class);

            Call<User> call = apiService.connection(userConnection);

            User userRes= new User();

            try {
                Response<User> userResponse = call.execute();
                userRes = userResponse.body();
                user=userRes;
                if(user.getToken()==null){
                    Snackbar.make(view[0] ,"identifiants incorrects",Snackbar.LENGTH_LONG).show();
                }else{
                    coGood = true;
                    SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("InfosUtilisateur", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("token", user.getToken()).apply();
                    sharedPreferences.edit().putString("id", Integer.toString(user.getId())).apply();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRANCE);
                    Date date = new Date();
                    sharedPreferences.edit().putString("CreateDate", dateFormat.format(date)).apply();

                    Intent intent = new Intent(LoginActivity.this, RecyclerActivity.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(view[0] ,"Connectez-vous à Internet",Snackbar.LENGTH_LONG).show();
            }




            return userRes;
        }

        protected void onPostExecute(User result) {
            Log.w("ghtfytgr", "rfgfd");
        }
    }


    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("InfosUtilisateur", Context.MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRANCE);
        Date date = new Date();
        String dateStr = sharedPreferences.getString("CreateDate", "NULL");
        if(!dateStr.equals("NULL")){
            Date date2 = null;
            try {
                date2 = dateFormat.parse(dateStr);

                Long date3 = date.getTime()-date2.getTime();
                System.out.println(date3);
                if (date3 < 86400000 ) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        super.onBackPressed();
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }



}
