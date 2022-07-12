ps -ef |grep java |awk '{print $2}'|xargs kill -9
nohup java -jar iokfine-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > nohup.log 2>&1 &
