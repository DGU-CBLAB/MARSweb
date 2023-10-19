# MarsNet

c++ version of MARS(made by Jong Wha J. Joo)<br> 
Original MARS paper -> https://www.biorxiv.org/content/10.1101/498360v1

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

# Using G++ Compiler
1. Unzip Eigen Library 
```
cd MarsNet
unzip Eigen.zip -d Eigen
```
2. Install boost library 
```
wget https://boostorg.jfrog.io/artifactory/main/release/1.74.0/source/boost_1_74_0.tar.gz
tar -zxvf boost_1_74_0.tar.gz
cd boost_1_74_0
./bootstrap.sh
./b2
```
3. Compile
```
g++ -O2 -DNDEBUG -std=c++11 -I ./Eigen/ ./Mars_cpp.cpp ./Mars_main.cpp -o MarsNet -I ./boost_1_74_0 
```
4. Run
``` 
./MarsNet -geno ./exam_geno -stat ./exam_stat -sim 10000 -mode 1
```
