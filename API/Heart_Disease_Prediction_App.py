import pickle

import numpy as np
from flask import Flask, request, jsonify


model = pickle.load(open('model1.pkl', 'rb'))

app = Flask(__name__)


@app.route('/')
def home():
    return "Heart Disease Prediction App"


@app.route('/predict', methods=['POST'])
def predict():
    cp = float(request.form.get('cp'))
    thalach = float(request.form.get('thalach'))
    slope = float(request.form.get('slope'))
    restecg = float(request.form.get('restecg'))
    chol = float(request.form.get('chol'))
    trestbps = float(request.form.get('trestbps'))
    fbs = float(request.form.get('fbs'))
    oldpeak = float(request.form.get('oldpeak'))

    input_query = np.array([[cp, thalach, slope, restecg, chol, trestbps, fbs, oldpeak]])

    result = model.predict(input_query)[0]

    return jsonify({'heart_disease': str(result)})
    

if __name__=='__main__':
    app.run(debug=True)