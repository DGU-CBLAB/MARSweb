# MarsNet

c++ version of MARS(made by Jong Wha J. Joo)<br> 
MARS paper -> https://www.biorxiv.org/content/10.1101/498360v1

# Setup using Docker image
1. cat Docker image<br>
MarsNet.tar file should be created after these commands
```
cd docker/
cat MarsNet.tar_* >> MarsNet.tar
```
2. Install Docker

3. Run MarsNet using Docker
```
docker load -i MarsNet.tar
docker run -v "data_path":/data --rm marsnet /MarsNet/MarsNet -geno /data/"yourdata_geno" -stat /data/"yourdata_stat" -sim 10000 -mode 1
```
3. Check NICER using web
```
Quick link: http://localhost:8080
```
# Using G++ Compiler
1. Unzip Eigen Library
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
