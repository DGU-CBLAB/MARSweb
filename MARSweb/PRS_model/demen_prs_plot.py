#!/usr/bin/env python
# coding: utf-8

# In[3]:


import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import seaborn as sns
import argparse

data = pd.read_csv("/Data/ktg/cblab_webserver/NICER-master/PRS_database/consent_1_835_demen.csv")
data = data.dropna()
data['smoke'] = data['smoke'].astype(float)
data['BMI'] = data['BMI'].replace("#DIV/0!",np.nan)
data = data.dropna(subset=["BMI"])
data['BMI'] = data["BMI"].astype(float)
data['alcohol'] = data['alcohol'].astype(float)


# In[ ]:


def main():
    parser = argparse.ArgumentParser(description='Predict disease risk using trained model.')
    parser.add_argument('--value', type=float, required=True, help='polygenic risk score')
    parser.add_argument('--output', type=str, default='demen_plot.png', help='Output file name for the plot')

    args = parser.parse_args()
    value = args.value
    output_file = args.output
    
    plt.figure(figsize=(10,6))
    sns.kdeplot(data=data, x='PRSice2', hue='Demen', common_norm=False)
    plt.axvline(value, color='red', linestyle='--', linewidth = 2)
    plt.title('Dementia')
    plt.xlabel('PRSice2')
    plt.ylabel('Density')
    plt.legend(labels=['case', 'control', f'value: {value}'])

    plt.savefig(output_file, format='png')
    plt.show()

    total_case = data[data['Demen'] == 1].shape[0]
    cases_above_value = data[(data['Demen']==1)&(data['PRSice2'] >= value)].shape[0]
    percentage_above_value = (cases_above_value / total_case) * 100

    print(f'Among those diagnosed with the disease, {percentage_above_value:.2f}% have a polygenic risk score greater than or equal to {value}.')
 


# In[ ]:


if __name__ == "__main__":
    main()

