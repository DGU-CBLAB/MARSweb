# MARSweb

c++ version of MARS(made by Jong Wha J. Joo)<br> 
Original MARS paper -> https://www.biorxiv.org/content/10.1101/498360v1

## Using Docker 

The Docker image for MARSweb can be found on Docker Hub at [taegun89/marsweb](https://hub.docker.com/r/taegun89/marsweb).

### Prerequisites

- Docker (version 19.03 or later)

### Pull the Docker Image

To pull the Docker image from Docker Hub, use the following command:

```bash
docker pull taegun89/marsweb:latest
```

### Running the Docker Contatiner
```bash
docker run -ti \
  -e MARSweb_username=your_username \
  -e MARSweb_passwd=your_password \
  -p 8080:8080 \
  -v "LDREF_path":/Data/1000genome/LDREF/LDREF
  taegun89/marsweb:latest
```

### Start Tomcat Server

To run the Docker container, execute the following command after starting the container
```
/MARSweb/apache-tomcat-9.0.64/bin/startup.sh
```
After running the command, you can access MARSweb locally at: http://cblab.dongguk.edu:8080/

## Using G++ Compiler
1. Unzip Eigen Library 
```
cd MARScpp
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
g++ -O2 -DNDEBUG -std=c++11 -I ./Eigen/ ./Mars_cpp.cpp ./Mars_main.cpp -o MARSweb -I ./boost_1_74_0 
```
4. Run
``` 
./MARSweb -geno ./exam_geno -stat ./exam_stat -sim 10000 -mode 1
```
