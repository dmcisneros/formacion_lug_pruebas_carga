##ANALISIS DE THREAD DUMPS
jstack -l ${idProcesoJava} >> test.log
#Indicar el id del proceso java a analizar 
#ps -ef | grep java
#el fichero resultante nos servirá para analizar los hilos en nuestro sistemas y posibles cuellos de botellas. 
#https://fastthread.io/index.jsp
