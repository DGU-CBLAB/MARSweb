import argparse
import tensorflow as tf
import tensorflow.keras as keras
from Bio import SeqIO
import numpy as np
from tensorflow.keras import layers
from tensorflow.keras.regularizers import l2
import pandas as pd


def encoder(seq_data,max_len):
    vocab = np.array(['','[UNK]','g','k','l','a','i','v','s','c','f','t','n','p','r','e','d','h','q','m','y','w','x','u'])
    encoded_data = np.zeros((seq_data.shape[0],max_len))
    for i, l in enumerate(seq_data):
        for j,alp in enumerate(list(l)):
            try:
                encoded_data[i,j]=np.where(alp.lower()==vocab)[0]
            except:
                encoded_data[i,j]=1
            
    return encoded_data

def im_dense_build(cache):
    for l in cache:
        if l[0]=='input':
            b_size = l[1].shape[0]
            shape = l[1].shape[1]
            inp = keras.Input(shape=shape, batch_size=b_size)
        elif l[0]=='dense_first':
            x1 = keras.layers.Dense(l[1], activation=l[2])(inp)
            x2 = keras.layers.Dense(l[1], activation=l[2])(inp)
            x3 = keras.layers.Dense(l[1], activation=l[2])(inp)
            x4 = keras.layers.Dense(l[1], activation=l[2])(inp)                
        elif l[0]=='dense':
            x1 = keras.layers.Dense(l[1], activation=l[2])(x1)
            x2 = keras.layers.Dense(l[1], activation=l[2])(x2)
            x3 = keras.layers.Dense(l[1], activation=l[2])(x3)
            x4 = keras.layers.Dense(l[1], activation=l[2])(x4)                
        elif l[0]=='dense_out':
            out1 = keras.layers.Dense(l[1], activation=l[2])(x1)
            out2 = keras.layers.Dense(l[1], activation=l[2])(x2)
            out3 = keras.layers.Dense(l[1], activation=l[2])(x3)
            out4 = keras.layers.Dense(l[1], activation=l[2])(x4)                

    out = keras.layers.concatenate([out1, out2, out3, out4])
    return keras.Model(inp, out),keras.Model(inp, out1),keras.Model(inp, out2),keras.Model(inp, out3),keras.Model(inp, out4)

def LSTM_layers():
    input_layer = keras.Input(shape=(300), batch_size=None)
    x = layers.Embedding(input_dim=input_shape,output_dim=out_dim, mask_zero=True)(input_layer)
    x = layers.LSTM(100, return_sequences=True, kernel_regularizer=l2(0.001), dropout=0.1)(x)
    x = layers.BatchNormalization()(x)
    x = layers.LSTM(100, return_sequences=True, kernel_regularizer=l2(0.001), dropout=0.1)(x)
    x = layers.BatchNormalization()(x)    
    x = layers.LSTM(100, return_sequences=True, kernel_regularizer=l2(0.001), dropout=0.1)(x)    
    x = layers.BatchNormalization()(x)    
    x = layers.LSTM(100, kernel_regularizer=l2(0.001), dropout=0.1)(x)
    x = layers.BatchNormalization()(x)    
    x = layers.Flatten()(x)
    
    return keras.Model(input_layer, x)

def GRU_layers():
    input_layer = keras.Input(shape=(300), batch_size=None)
    x = layers.Embedding(input_dim=input_shape,output_dim=out_dim, mask_zero=True)(input_layer)
    x = layers.GRU(100, return_sequences=True, kernel_regularizer=l2(0.001), dropout=0.1)(x)
    x = layers.BatchNormalization()(x)    
    x = layers.GRU(100, return_sequences=True, kernel_regularizer=l2(0.001), dropout=0.1)(x)
    x = layers.BatchNormalization()(x)    
    x = layers.GRU(100, return_sequences=True, kernel_regularizer=l2(0.001), dropout=0.1)(x)    
    x = layers.BatchNormalization()(x)
    
    x = layers.GRU(100, kernel_regularizer=l2(0.001), dropout=0.1)(x)    
    x = layers.BatchNormalization()(x)
    
    x = layers.Flatten()(x)
    
    return keras.Model(input_layer, x)

def Dense_layer(output_layer):
    input_layer = keras.Input(shape=(output_layer.output.shape[1]), batch_size=None)
    x = layers.Dense(50)(input_layer)
    x = layers.Dense(1)(x)
    return keras.Model(input_layer, x)

def im_dense_build(dense_str):
    for l in dense_str:
        if l[0]=='input':
            b_size = l[1].shape[0]
            shape = l[1].shape[1]
            inp = keras.Input(shape=shape, batch_size=b_size)
        elif l[0]=='dense_first':
            x = keras.layers.Dense(l[1], activation=l[2])(inp)
        elif l[0]=='dense':
            x = keras.layers.Dense(l[1], activation=l[2])(x)
        elif l[0]=='dense_out':
            out = keras.layers.Dense(l[1], activation=l[2])(x)

    return keras.Model(inp, out)
            
parser = argparse.ArgumentParser(description='Prediction of ion channel modulating-peptides')
parser.add_argument('--fasta', metavar='fasta', type=str, help='input fasta file')
parser.add_argument('--output', metavar='output', type=str, help='Output file')    
args = parser.parse_args()

inp_data = []
for l in SeqIO.parse(args.fasta,'fasta'):
    inp_data.append([l.id, str(l.seq)])

    
inp_data = pd.DataFrame(inp_data,columns=['ID', 'Sequence'])
inp_seq = encoder(inp_data.values[:,1],300)


print('PrIMP prediction start ------------------')

opt_model = {}

opt_model['Calcium']='/DATA/taegun/NICER-master/PrIMP-main/model/cav.ckpt'
opt_model['nAChR']='/DATA/taegun/NICER-master/PrIMP-main/model/nachr.ckpt'
opt_model['Potassium']='/DATA/taegun/NICER-master/PrIMP-main/model/pota.ckpt'
opt_model['Sodium']='/DATA/taegun/NICER-master/PrIMP-main/model/sodium.ckpt'

res_pred = {}
input_shape = 24
out_dim = 10

activity = pd.DataFrame(['']*inp_seq.shape[0])
for k in opt_model.keys():
    if k=='Calcium' or k=='nAChR':
        top_model = LSTM_layers()
    else:
        top_model=GRU_layers()

    im_dense_str = [['input',top_model.output],
                        ['dense_first',50,'relu'],
                        ['dense_out',1,None]]

    bottom_model = im_dense_build(im_dense_str)
    model = keras.Sequential([top_model, bottom_model])
    model.load_weights(opt_model[k])

    pred_res = tf.nn.sigmoid(model.predict(inp_seq)).numpy()

    activity.values[tf.squeeze(pred_res)>=0.5]='Modulator'
    activity.values[tf.squeeze(pred_res)<0.5]='-'

    temp = np.concatenate((pred_res, activity.values),1)

    
    inp_data = pd.concat([inp_data,pd.DataFrame(temp,columns=[k+ ' probability',k+' prediction'])],axis=1)

inp_data.to_csv(args.output)

print('Prediction done ---------------------')
print('Prediction results were saved ', args.output)
