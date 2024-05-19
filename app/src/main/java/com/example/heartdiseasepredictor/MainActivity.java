package com.example.heartdiseasepredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText age, sex, cp, trestbps, chol, fbs, restecg, thalach, exang, oldpeak, slope, ca, thal;
    private Button predict;
    private TextView result;
    private Button tips;

    private ImageButton infoAge, infoSex, infoCp, infoTrestbps, infoChol, infoFbs, infoRestecg, infoThalach, infoExang, infoOldpeak, infoSlope, infoCa, infoThal;

    String url = "https://58d1-2409-40d2-58-6205-7a50-1725-c8f4-cae7.ngrok-free.app/predict";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        age = findViewById(R.id.age);
        sex = findViewById(R.id.sex);
        cp = findViewById(R.id.cp);
        trestbps = findViewById(R.id.trestbps);
        chol = findViewById(R.id.chol);
        fbs = findViewById(R.id.fbs);
        restecg = findViewById(R.id.restecg);
        thalach = findViewById(R.id.thalach);
        exang = findViewById(R.id.exang);
        oldpeak = findViewById(R.id.oldpeak);
        slope = findViewById(R.id.slope);
        ca = findViewById(R.id.ca);
        thal = findViewById(R.id.thal);
        predict = findViewById(R.id.predict);
        result = findViewById(R.id.result);
        tips = findViewById(R.id.tips);

        infoAge = findViewById(R.id.info_age);
        infoSex = findViewById(R.id.info_sex);
        infoCp = findViewById(R.id.info_cp);
        infoTrestbps = findViewById(R.id.info_trestbps);
        infoChol = findViewById(R.id.info_chol);
        infoFbs = findViewById(R.id.info_fbs);
        infoRestecg = findViewById(R.id.info_restecg);
        infoThalach = findViewById(R.id.info_thalach);
        infoExang = findViewById(R.id.info_exang);
        infoOldpeak = findViewById(R.id.info_oldpeak);
        infoSlope = findViewById(R.id.info_slope);
        infoCa = findViewById(R.id.info_ca);
        infoThal = findViewById(R.id.info_thal);

        infoAge.setOnClickListener(v -> showInfoDialog("Age", "Age of the patient"));
        infoSex.setOnClickListener(v -> showInfoDialog("Sex", "Sex of the patient: 0 = female, 1 = male"));
        infoCp.setOnClickListener(v -> showInfoDialog("Chest Pain Type", "Chest pain type: 0 = typical angina, 1 = atypical angina, 2 = non-anginal pain, 3 = asymptomatic"));
        infoTrestbps.setOnClickListener(v -> showInfoDialog("Resting Blood Pressure", "Resting blood pressure (in mm Hg) on admission to the hospital"));
        infoChol.setOnClickListener(v -> showInfoDialog("Serum Cholesterol", "Serum cholesterol in mg/dl"));
        infoFbs.setOnClickListener(v -> showInfoDialog("Fasting Blood Sugar", "Fasting blood sugar > 120 mg/dl: 1 = true, 0 = false"));
        infoRestecg.setOnClickListener(v -> showInfoDialog("Resting ECG", "Resting electrocardiographic results: 0 = normal, 1 = having ST-T wave abnormality, 2 = showing probable or definite left ventricular hypertrophy"));
        infoThalach.setOnClickListener(v -> showInfoDialog("Maximum Heart Rate", "Maximum heart rate achieved"));
        infoExang.setOnClickListener(v -> showInfoDialog("Exercise Induced Angina", "Exercise induced angina: 1 = yes, 0 = no"));
        infoOldpeak.setOnClickListener(v -> showInfoDialog("ST Depression", "ST depression induced by exercise relative to rest"));
        infoSlope.setOnClickListener(v -> showInfoDialog("ST Slope", "Slope of the peak exercise ST segment: 0 = upsloping, 1 = flat, 2 = downsloping"));
        infoCa.setOnClickListener(v -> showInfoDialog("Major Vessels", "Number of major vessels (0-3) colored by fluoroscopy"));
        infoThal.setOnClickListener(v -> showInfoDialog("Thalassemia", "Thalassemia: 1 = normal, 2 = fixed defect, 3 = reversable defect"));

        predict.setOnClickListener(v -> {
            if (isValidInput()) {
                sendPredictionRequest();
            }
        });

        tips.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
            finish();
        });
    }

    private boolean isValidInput() {
        if (age.getText().toString().isEmpty()) {
            age.setError("Cannot be Empty");
            return false;
        }
        if (sex.getText().toString().isEmpty() || (!sex.getText().toString().equals("0") && !sex.getText().toString().equals("1"))) {
            sex.setError("Should be 0 or 1");
            return false;
        }
        if (cp.getText().toString().isEmpty() || !isInRange(cp.getText().toString(), 0, 3)) {
            cp.setError("Should be in 0-3 range");
            return false;
        }
        if (trestbps.getText().toString().isEmpty() || Integer.parseInt(trestbps.getText().toString()) < 0) {
            trestbps.setError("Cannot be Empty");
            return false;
        }
        if (chol.getText().toString().isEmpty() || Integer.parseInt(chol.getText().toString()) < 0) {
            chol.setError("Cannot be Empty");
            return false;
        }
        if (fbs.getText().toString().isEmpty() || (!fbs.getText().toString().equals("0") && !fbs.getText().toString().equals("1"))) {
            fbs.setError("Should be 0 or 1");
            return false;
        }
        if (restecg.getText().toString().isEmpty() || !isInRange(restecg.getText().toString(), 0, 2)) {
            restecg.setError("Should be in 0-2 range");
            return false;
        }
        if (thalach.getText().toString().isEmpty() || Integer.parseInt(thalach.getText().toString()) < 0) {
            thalach.setError("Cannot be Empty");
            return false;
        }
        if (exang.getText().toString().isEmpty() || (!exang.getText().toString().equals("0") && !exang.getText().toString().equals("1"))) {
            exang.setError("Should be 0 or 1");
            return false;
        }
        if (oldpeak.getText().toString().isEmpty() || Float.parseFloat(oldpeak.getText().toString()) < 0) {
            oldpeak.setError("Cannot be Empty");
            return false;
        }
        if (slope.getText().toString().isEmpty() || !isInRange(slope.getText().toString(), 0, 2)) {
            slope.setError("Should be in 0-2 range");
            return false;
        }
        if (ca.getText().toString().isEmpty() || !isInRange(ca.getText().toString(), 0, 3)) {
            ca.setError("Should be in 0-3 range");
            return false;
        }
        if (thal.getText().toString().isEmpty() || !isInRange(thal.getText().toString(), 1, 3)) {
            thal.setError("Should be in 1-3 range");
            return false;
        }
        return true;
    }

    private boolean isInRange(String value, int min, int max) {
        try {
            int intValue = Integer.parseInt(value);
            return intValue >= min && intValue <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void sendPredictionRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("API_RESPONSE", jsonObject.toString());

                            String logisticPrediction = jsonObject.getString("logistic_regression_prediction");
                            String knnPrediction = jsonObject.getString("knn_prediction");
                            String rfPrediction = jsonObject.getString("random_forest_prediction");

                            String resultText = "Logistic Regression Prediction: " + logisticPrediction + "\n"
                                    + "KNN Prediction: " + knnPrediction + "\n"
                                    + "Random Forest Prediction: " + rfPrediction;

                            result.setText(resultText);
                            tips.setVisibility(View.VISIBLE);

                            clearInputs();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err = (error.getMessage() == null) ? "Failed! Please Try Again" : error.getMessage();
                Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                Log.d("API_ERROR", err);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Age", age.getText().toString());
                params.put("Sex", sex.getText().toString());
                params.put("Chest_pain", cp.getText().toString());
                params.put("Resting_blood_pressure", trestbps.getText().toString());
                params.put("Cholesterol", chol.getText().toString());
                params.put("Fasting_blood_sugar", fbs.getText().toString());
                params.put("ECG_results", restecg.getText().toString());
                params.put("Maximum_heart_rate", thalach.getText().toString());
                params.put("Exercise_induced_angina", exang.getText().toString());
                params.put("ST_depression", oldpeak.getText().toString());
                params.put("ST_slope", slope.getText().toString());
                params.put("Major_vessels", ca.getText().toString());
                params.put("Thalassemia_types", thal.getText().toString());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);
    }

    private void clearInputs() {
        age.setText("");
        sex.setText("");
        cp.setText("");
        trestbps.setText("");
        chol.setText("");
        fbs.setText("");
        restecg.setText("");
        thalach.setText("");
        exang.setText("");
        oldpeak.setText("");
        slope.setText("");
        ca.setText("");
        thal.setText("");
    }

    private void showInfoDialog(String title, String message) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView close = dialog.findViewById(R.id.closeDialog);
        TextView nameDialog = dialog.findViewById(R.id.nameDialog);
        TextView infoDialog = dialog.findViewById(R.id.infoDialog);

        nameDialog.setText(title);
        infoDialog.setText(message);

        close.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
