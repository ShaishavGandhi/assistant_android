package com.example.shaishavgandhi.assistant.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.tool.util.StringUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.shaishavgandhi.assistant.R;
import com.example.shaishavgandhi.assistant.data.PreferenceSource;
import com.example.shaishavgandhi.assistant.databinding.ActivityMainBinding;
import com.example.shaishavgandhi.assistant.databinding.ContentMainBinding;
import com.example.shaishavgandhi.assistant.network.APIManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int SPEECH_RECOGNITION_CODE = 0;
    boolean ping;
    ActivityMainBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("lato.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSpeechToText();
            }
        });

        populateSections();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String query = result.get(0);
                    makeNetworkRequest(query);
                }
                break;
        }
    }

    private void makeNetworkRequest(String query) {
        APIManager.getInstance(getApplicationContext()).submitQuery(query);
    }

    private void populateSections() {
        populateIpSection();
        populatePreferencesSection();
    }

    private void populateIpSection() {
        String ipAddress = PreferenceSource.getInstance(getApplicationContext()).getIp();
        if (ipAddress.equals("")) {
            binding.contentMain.sectionIp.ipAddress.setVisibility(View.GONE);
            binding.contentMain.sectionIp.ipAddressEdit.setVisibility(View.VISIBLE);
            binding.contentMain.sectionIp.ipButton.setText("Save");
        } else {
            binding.contentMain.sectionIp.ipAddress.setText(ipAddress);
        }

        binding.contentMain.sectionIp.ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.contentMain.sectionIp.ipButton.getText().toString().equals("Save")) {
                    PreferenceSource.getInstance(getApplicationContext()).setIp(binding.contentMain.sectionIp.ipAddressEdit.getText().toString());
                    binding.contentMain.sectionIp.ipButton.setText("Edit");
                    binding.contentMain.sectionIp.ipAddress.setVisibility(View.VISIBLE);
                    binding.contentMain.sectionIp.ipAddressEdit.setVisibility(View.GONE);
                    binding.contentMain.sectionIp.ipAddress.setText(binding.contentMain.sectionIp.ipAddressEdit.getText().toString());
                } else {
                    binding.contentMain.sectionIp.ipButton.setText("Save");
                    binding.contentMain.sectionIp.ipAddress.setVisibility(View.GONE);
                    binding.contentMain.sectionIp.ipAddressEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.contentMain.sectionIp.pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Pinging your command center",Snackbar.LENGTH_SHORT).show();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        ping = pingHost(PreferenceSource.getInstance(getApplicationContext()).getIp(), 5000, 5000);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (!ping) {
                                    Toast.makeText(getApplicationContext(), "Command Center Is Down! ", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Command Center Is Healthy and Running! ", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void populatePreferencesSection() {
        String preference = PreferenceSource.getInstance(getApplicationContext()).getTemperaturePreference();
        int index = getIndexFromTemperature(preference);
        binding.contentMain.sectionPreferences.temperatureUnitSpinner.setSelection(index);
        binding.contentMain.sectionPreferences.temperatureUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String unit = getResources().getStringArray(R.array.temperature_unit)[i];
                PreferenceSource.getInstance(getApplicationContext()).setTemperaturePreference(unit);
                Snackbar.make(view, "Preference relayed to command center!", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public int getIndexFromTemperature(String unit) {
        String[] array = getResources().getStringArray(R.array.temperature_unit);

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(unit)) {
                return i;
            }
        }

        return 0;
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
