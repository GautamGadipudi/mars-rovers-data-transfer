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
This will ultimately run the java Main class as an application.

`docker run -it -p 8080:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.21 rover 1 `

### To Run (node 2):
`docker run -it -p 8081:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.22 rover 2 `

### To Block Nodes 2 and 3 on Node 1
Using the block=ip http query parameter.

`curl "http://localhost:8080/?block=172.18.0.22&block=172.18.0.23" `

### To unblock Node 2 on Node 1
Using the unblock=ip http query parameter.

`curl "http://localhost:8080/?unblock=172.18.0.22" `