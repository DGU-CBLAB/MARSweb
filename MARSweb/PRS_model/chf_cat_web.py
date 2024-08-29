#!/usr/bin/env python
# coding: utf-8

# In[1]:


import numpy as np
import pandas as pd
import catboost as cb
from sklearn.model_selection import train_test_split
from catboost import CatBoostClassifier
from sklearn.metrics import roc_auc_score
from sklearn.model_selection import GridSearchCV
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_squared_error
from sklearn.metrics import roc_curve, confusion_matrix
import joblib
import argparse
scaler = StandardScaler()


# In[2]:


def main():
    parser = argparse.ArgumentParser(description='Predict disease risk using trained model.')
    parser.add_argument('--age', type=float, required=True, help='Age of the patient')
    parser.add_argument('--PRS', type=float, required=True, help='Polygenic risk score')
    parser.add_argument('--smoke', type=float, required=True, help='Smoking status (1 if smoker, 0 if non-smoker)')
    parser.add_argument('--alcohol', type=float, required=True, help='Alcohol consumption status (1 if consumer, 0 if non-consumer)')
    parser.add_argument('--BloodGlucose', type=float, required=True, help='Blood Glucose level')
    parser.add_argument('--ASBP', type=float, required=True, help='Arterial Systolic Blood Pressure')
    parser.add_argument('--cholesterol', type=float, required=True, help='Total Cholesterol level')

    args = parser.parse_args()
    
    input_data = np.array([[args.PRS, args.BloodGlucose, args.cholesterol, args.ASBP, args.age, args.smoke, args.alcohol]])
    model = joblib.load('chf_cat.pkl')
    predicted_probs = model.predict_proba(input_data)[:, 0]
    print("predict: {:.2f}".format(predicted_probs[0]))


# In[ ]:


if __name__ == "__main__":
    main()


# In[ ]:




