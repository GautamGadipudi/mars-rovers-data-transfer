# mars-rovers-data-transfer

Author: Gautam Gadipudi

Id: gg7148

All docker testing environment is taken from Professor Fryer's repository: *[MulticastTestingEnvironment](https://www.markdownguide.org)*.
### To build
This will also build any java files in the current directory in the container.

`docker build -t rover . `

### To create the node network
Only needs to be done once.

`docker network create --subnet=172.18.0.0/16 nodenet `


### To Run (for example, node 1)
This will ultimately run the java Main class as an application. Create a rover that wants to receive data.

`docker run -it -p 8080:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.22 rover 2 `

### To Run (node 2):
Create a rover that wants to send data of a file (`alice_in_wonderland.txt`).

`docker run -it -p 8081:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.21 rover 1 alice_in_wonderland.txt 172.18.0.22`

### To randomly drop 10% of incoming packets to Node 1:
`curl "http://localhost:8080/?indrop=0.1"`
