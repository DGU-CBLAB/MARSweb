# NICER
NICE Renew
(other features such as GAMMA, Multitrans will be updated)

# Prerequistes for running Source Code
(suggest using Linux environment)
1. Basic instructions
```
sudo chmod -c 777 ./NICER/ -R
sudo yum install ld-linux.so.2
sudo yum install epel-release
```
2. install R language
```
sudo yum install R
```
3. install R 'qqman' package
``` 
R command : install.packages('qqman')
```
- Using Eclipse
```
add all apache/lib/*.jar to the project
```
4. Change Directory Settings

at
> NICER/NICER/src/main/Setup.java 

change
> final static String mainDir ="**set NICER folder directory here**";

# For Trouble Shooting
a. When NICE/t_test_static doesn't work!!

  use following command to allow execution : sudo chmod -c 777 ./NICE/t_test_static
  

