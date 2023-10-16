# MarsNet
c++ version of MARS(made by Jong Wha J. Joo)
MARS paper -> https://www.biorxiv.org/content/10.1101/498360v1

# Setup using Docker image
1. cat Docker image
nicer_centos.tar file should be created after these commands
```
cd docker/
cat nicer_centos.tar* >> nicer_centos.tar
```
2. Install Docker

3. Run NICER using Docker
```
docker load -i nicer_centos.tar
docker run --priviledged -d -p 8080:8080 nicer_centos init
docker exec -it containername
```
3. Check NICER using web
```
Quick link: http://localhost:8080
```
# Setup using Eclipse(on Linux)
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
