#!/usr/bin/env python
# coding: utf-8

# In[24]:


import pandas as pd
import argparse
import numpy as np
from sklearn.model_selection import train_test_split
from lightgbm import LGBMClassifier
from sklearn.model_selection import GridSearchCV
from sklearn.metrics import roc_auc_score
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_squared_error, accuracy_score
from sklearn.metrics import roc_curve, confusion_matrix
import joblib
import sys
scaler = StandardScaler()


# In[25]:


def main():
    parser = argparse.ArgumentParser(description='Predict disease risk using trained model.')
    parser.add_argument('--age', type=float, required=True, help='Age of the patient')
    parser.add_argument('--PRS', type=float, required=True, help='Polygenic risk score')
    parser.add_argument('--smoke', type=float, required=True, default=0, help='Smoking status (1 if smoker, 0 if non-smoker)')
    parser.add_argument('--alcohol', type=float, required=True, default=0, help='Alcohol consumption status (1 if consumer, 0 if non-consumer)')
    parser.add_argument('--BloodGlucose', type=float, required=True, default=88, help='Blood Glucose level')
    parser.add_argument('--ASBP', type=float, required=True, default=115, help='Arterial Systolic Blood Pressure')
    parser.add_argument('--cholesterol', type=float, required=True, default=195, help='Total Cholesterol level')

    args = parser.parse_args()
    
    input_data = np.array([[args.age, args.PRS, args.smoke, args.alcohol, args.BloodGlucose, args.ASBP, args.cholesterol]])
    model = joblib.load('af_lgbm.pkl')
    predicted_probs = model.predict_proba(input_data)[:, 1]
    print("predict: {:.2f}".format(predicted_probs[0]))


# In[ ]:


if __name__ == "__main__":
    main()

