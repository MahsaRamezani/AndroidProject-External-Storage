package preferences.mahsaramezani.com.fileread;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;

//http://www.mkyong.com/android/android-spinner-drop-down-list-example/
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView editText;
    private EditText fileName2;
    private TextView textView;
    private Button saveButton;
    private Button readButton;
    MediaPlayer myMediaPlayerOne;
    MediaPlayer myMediaPlayerTwo;
    static final int READ_BLOCK_SIZE = 100;
    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    public TextView label;
    Spinner spinner;
    String var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (TextView) findViewById(R.id.label);
        fileName2 = (EditText) findViewById(R.id.editText2);

        saveButton = (Button) findViewById(R.id.write);
        readButton = (Button) findViewById(R.id.read);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                askPermissionAndWriteFile();
            }

        });

        readButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                askPermissionAndReadFile(null);
            }

        });

        myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a1);
        myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a2);

        View.OnClickListener pleaseListenToOne = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView myTextView = (TextView) findViewById(R.id.label);
                myTextView.append("a1,");
                myMediaPlayerOne.start();
                var += "a1,";
            }
        };

        View.OnClickListener pleaseListenToTwo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView myTextView = (TextView) findViewById(R.id.label);
                myTextView.append("a2,");
                myMediaPlayerTwo.start();
                var += "a2,";
            }
        };

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        Button btnOne = (Button) findViewById(R.id.a1);
        btnOne.setOnClickListener(pleaseListenToOne);

        Button btnTwo = (Button) findViewById(R.id.a2);
        btnTwo.setOnClickListener(pleaseListenToTwo);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        TextView myTextView = (TextView) findViewById(R.id.label);
        myTextView.setText("");
        var = "";
        if (spinner.getSelectedItem() != null && String.valueOf(spinner.getSelectedItem()).equals("Music TWO")) {
            myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a26);
            myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a27);
        } else {
            myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a1);
            myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a2);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // interrupt in play sounds
    public void buttonClick(View view) {
        long endTime = System.currentTimeMillis() + 500;

        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
    }

    // write text to file
    public void WriteBtn(View v) {
        // add-write text into file
        try {
            TextView myTextView = (TextView) findViewById(R.id.label);
            FileOutputStream fileout = openFileOutput("mytextfile.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            var += (String.valueOf(spinner.getSelectedItem()) + ",");
            outputWriter.write(var);
            outputWriter.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read text from file
    public void ReadBtn(View v) {
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("mytextfile.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
                System.out.print("test1111111111111111 : " + readstring);
            }
            String[] notes = s.split(",");
            for (String note : notes) {
                switch (note) {
                    case "a1": {
                        myMediaPlayerOne.start();
                        buttonClick(v);
                        break;
                    }
                    case "a2": {
                        myMediaPlayerTwo.start();
                        buttonClick(v);
                        break;
                    }
                    case "Music TWO": {
                        myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a26);
                        myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a27);
                        buttonClick(v);
                        break;
                    }
                    case "Music ONE": {
                        myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a1);
                        myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a2);
                        buttonClick(v);
                        break;
                    }
                    default:
                        break;
                }
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askPermissionAndWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            this.writeFile();
        }
    }

    private void askPermissionAndReadFile(View v) {
        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        //
        if (canRead) {
            this.readFile(v);
        }
    }

    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }

    private void writeFile() {

        File extStore = Environment.getExternalStorageDirectory();
        // ==> /storage/emulated/0/note.txt
        String nameFile = fileName2.getText().toString();
        if (!nameFile.equals("")) {
            String path = extStore.getAbsolutePath() + "/" + nameFile;
            Log.i("ExternalStorageDemo", "Save to: " + path);

            String data = editText.getText().toString();

            try {
                File myFile = new File(path);
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            myOutWriter.append(data);
                var += (String.valueOf(spinner.getSelectedItem()) + ",");
                myOutWriter.append(var);
                myOutWriter.close();
                fOut.close();

                Toast.makeText(getApplicationContext(), nameFile + " ذخیره شد.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), " لطفا نام فایل را وارد کنید. ", Toast.LENGTH_LONG).show();
        }
    }

    private void readFile(View v) {
        String nameFile = fileName2.getText().toString();
        if (!nameFile.equals("")) {
            File extStore = Environment.getExternalStorageDirectory();
            // ==> /storage/emulated/0/note.txt
            String path = extStore.getAbsolutePath() + "/" + nameFile;
            Log.i("ExternalStorageDemo", "Read file: " + path);

            String s = "";
            StringBuilder fileContent = new StringBuilder();
            try {
                File myFile = new File(path);
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));
                while ((s = myReader.readLine()) != null) {
                    fileContent.append(s).append("\n");
                }
                String[] notes = fileContent.toString().split(",");
                for (String note : notes) {
                    switch (note) {
                        case "a1": {
                            myMediaPlayerOne.start();
                            buttonClick(v);
                            break;
                        }
                        case "a2": {
                            myMediaPlayerTwo.start();
                            buttonClick(v);
                            break;
                        }
                        case "Music TWO": {
                            myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a26);
                            myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a27);
                            buttonClick(v);
                            break;
                        }
                        case "Music ONE": {
                            myMediaPlayerOne = MediaPlayer.create(MainActivity.this, R.raw.a1);
                            myMediaPlayerTwo = MediaPlayer.create(MainActivity.this, R.raw.a2);
                            buttonClick(v);
                            break;
                        }
                        default:
                            break;
                    }
                }
                myReader.close();
//            this.textView.setText(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), fileContent.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), " لطفا نام فایل را وارد کنید. ", Toast.LENGTH_LONG).show();
        }
    }
}
