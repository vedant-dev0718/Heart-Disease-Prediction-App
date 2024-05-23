import pandas as pd
import numpy as np
import pickle
from flask import Flask, request, jsonify
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

# Load your dataset
df = pd.read_csv('heart-disease2.csv')

# Rename columns
df.rename(columns={'age': 'Age', 'sex': 'Sex', 'cp': 'Chest_pain', 'trestbps': 'Resting_blood_pressure', 'chol': 'Cholesterol', 'fbs': 'Fasting_blood_sugar',
                   'restecg': 'ECG_results', 'thalach': 'Maximum_heart_rate', 'exang': 'Exercise_induced_angina', 'oldpeak': 'ST_depression', 'slope': 'ST_slope',
                   'ca': 'Major_vessels', 'thal': 'Thalassemia_types', 'target': 'Heart_disease'}, inplace=True)

# Prepare data
X = df.drop('Heart_disease', axis=1)
y = df['Heart_disease']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train Logistic Regression model
logistic_model = LogisticRegression(max_iter=1000)
logistic_model.fit(X_train, y_train)
logistic_preds = logistic_model.predict(X_test)
logistic_acc = accuracy_score(y_test, logistic_preds)
pickle.dump(logistic_model, open('logistic_model.pkl', 'wb'))

# Train KNN model
knn_model = KNeighborsClassifier()
knn_model.fit(X_train, y_train)
knn_preds = knn_model.predict(X_test)
knn_acc = accuracy_score(y_test, knn_preds)
pickle.dump(knn_model, open('knn_model.pkl', 'wb'))

# Train Random Forest model
rf_model = RandomForestClassifier(random_state=42, n_jobs=-1, max_depth=5, n_estimators=100, oob_score=True)
rf_model.fit(X_train, y_train)
rf_preds = rf_model.predict(X_test)
rf_acc = accuracy_score(y_test, rf_preds)
pickle.dump(rf_model, open('random_forest_model.pkl', 'wb'))

# Print model accuracies
print("Logistic Regression Accuracy: ", logistic_acc)
print("KNN Accuracy: ", knn_acc)
print("Random Forest Accuracy: ", rf_acc)

# Flask API
app = Flask(__name__)

@app.route('/')
def home():
    return "Heart Disease Prediction App"

@app.route('/predict', methods=['POST'])
def predict():
    # Extract features from the request form
    features = ['Age', 'Sex', 'Chest_pain', 'Resting_blood_pressure', 'Cholesterol', 'Fasting_blood_sugar', 'ECG_results',
                'Maximum_heart_rate', 'Exercise_induced_angina', 'ST_depression', 'ST_slope', 'Major_vessels', 'Thalassemia_types']
    input_data = [float(request.form.get(feature)) for feature in features]
    
    input_query = np.array([input_data])
    
    # Load trained models
    logistic_model = pickle.load(open('logistic_model.pkl', 'rb'))
    knn_model = pickle.load(open('knn_model.pkl', 'rb'))
    rf_model = pickle.load(open('random_forest_model.pkl', 'rb'))
    
    # Predict with each model
    logistic_result = logistic_model.predict(input_query)[0]
    knn_result = knn_model.predict(input_query)[0]
    rf_result = rf_model.predict(input_query)[0]
    
    return jsonify({
        'logistic_regression_prediction': float(logistic_result),
        'knn_prediction': float(knn_result),
        'random_forest_prediction': float(rf_result)
    })

if __name__ == '__main__':
    app.run(debug=True)
