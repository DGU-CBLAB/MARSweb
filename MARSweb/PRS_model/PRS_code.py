#!/usr/bin/env python
# coding: utf-8

import pandas as pd
import argparse
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
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


def main():
    parser = argparse.ArgumentParser(description='Predict disease risk using trained model.')
    parser.add_argument('--age', type=float, required=True, help='Age of the patient')
    parser.add_argument('--smoke', type=float, required=False, default=0, help='Smoking status (1 if smoker, 0 if non-smoker)')
    parser.add_argument('--alcohol', type=float, required=False, default=0, help='Alcohol consumption status (1 if consumer, 0 if non-consumer)')
    parser.add_argument('--BloodGlucose', type=float, required=False, default=88, help='Blood Glucose level')
    parser.add_argument('--ASBP', type=float, required=False, default=115, help='Arterial Systolic Blood Pressure')
    parser.add_argument('--cholesterol', type=float, required=False, default=195, help='Total Cholesterol level')
    parser.add_argument('--input', required=True,type=str, help='input PRS score file name')
    parser.add_argument('--output', required=True,type=str, help='Output file name for the plot')
    
    args = parser.parse_args()
    prs_df = pd.read_csv(args.input, header=None)
    user_score_list = prs_df[0].tolist()
    column_name = ["AF","CHD","CHF","Demen","DIA","STR"]
    disease_list = ["Atrial Fibrillation", "Coronary Heart Disease", "Congestive Heart Failure", "Dementia", "Diabetes", "Stroke"]
    model_list = ["af_lgbm.pkl", "chd_cat.pkl", "chf_cat.pkl", "demen_lgbm.pkl", "dia_cat.pkl","str_cat.pkl"]   
    prs_data = ["consent_1_835_af.csv","consent_1_835_chd.csv","consent_1_835_chf.csv","consent_1_835_demen.csv","consent_1_835_dia.csv","consent_1_835_str.csv"]
    DB_path = "/Data/ktg/cblab_webserver/NICER-master/PRS_database/csv_file/"
    pkl_path = "/Data/ktg/cblab_webserver/NICER-master/PRS_model/"
    data = pd.read_csv(DB_path + prs_data[0])
    data = data.dropna()
    data['smoke'] = data['smoke'].astype(float)
    data['BMI'] = data['BMI'].replace("#DIV/0!",np.nan)
    data = data.dropna(subset=["BMI"])
    data['BMI'] = data["BMI"].astype(float)
    data['alcohol'] = data['alcohol'].astype(float)
    
    predict_result = []
    for i in range(6):
        value = user_score_list[i]
        input_data = np.array([[value, args.BloodGlucose, args.cholesterol, args.ASBP,args.age, args.smoke, args.alcohol]])
        model = joblib.load(pkl_path + model_list[i])
        predicted_probs = model.predict_proba(input_data)[:, 1]
        print("predict: {:.2f}".format(predicted_probs[0]))
        predict_result.append(predicted_probs[0])
        
        output_file = args.output
        data2 = pd.read_csv(DB_path + prs_data[i])
        
        plt.figure(figsize=(10,6))
        sns.kdeplot(data=data2, x='PRSice2', hue=column_name[i], common_norm=False)
        plt.axvline(value, color='red', linestyle='--', linewidth = 2)
        plt.title(disease_list[i])
        plt.xlabel('PRS score')
        plt.ylabel('Density')

        plt.savefig(output_file + "/" + disease_list[i] + "_result.png", format='png')
#        plt.show()
#        total_case = data[data['AF'] == 1].shape[0]
#        cases_above_value = data[(data['AF']==1)&(data['PRSice2'] >= value)].shape[0]
#        percentage_above_value = (cases_above_value / total_case) * 100

#        print(f'Among those diagnosed with the disease, {percentage_above_value:.2f}% have a polygenic risk score greater than or equal to {value}.')
        
if __name__ == "__main__":
    main()

