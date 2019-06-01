# Pruebas de Rendimiento (Sobre Liferay)

## Autores:
- David Vega Perea ([@davidsrules7](https://twitter.com/DavidsRules7){:target="_blank"})
- Daniel Martínez Cisneros ([@dmcisneros](https://twitter.com/dmcisneros){:target="_blank"})

## Entorno:
- Hardware:2,9 GHz Intel Core i7 (16 GB 2133 MHz LPDDR3)
- Sistema Operativo: macOS Mojave (10.14.4)
- Liferay Community Edition Portal 7.2.0 CE RC2
- Tomcat 9.0.17
- Java: JDK 1.8.202

## Requisitos previos:
- Liferay arrancado: 
	```markdown
	./startup.sh
	```
- Jvisualvm arrancado y conectado al proceso de tomcat: 
	```markdown
	./jvisualvm 
	```
- Jmeter: 
	```markdown
	./jmeter.sh 
	```

## Introducción: 
El objetivo de éste repositorio es ver como realizar unas pruebas de carga coherentes sobre un entorno Liferay (Aunque podría ser utilizado para cualquier aplicación Java).

La pruebas de rendimiento nos servirán para:
	
- Demostrar que el sistema cumple los criterios de rendimiento.
- Validar y verificar atributos de la calidad del sistema: escalabilidad, fiabilidad, uso de los recursos.
- Medir qué partes del sistema o de carga de trabajo provocan que el conjunto rinda mal.

# Secuencia que debemos seguir para asegurar la estabilidad del sistema.


## 1º Planificar y diseñar las pruebas de carga.
Antes de iniciar un proceso de carga contra nuestro sistemas debemos planificar:
	
- ¿Qué páginas u operaciones consideramos que serán más ejecutadas en nuestro sistema? Debemos intentar deteminar e intentar simular la variabilidad de los usuarios.
	
- ¿Qué número de usuarios concurrentes esperamos tener cuando salgamos a producción?

## 2º Que tipos de pruebas queremos lanzar

**Pruebas de carga**
Las pruebas de carga son un tipo de prueba de rendimiento del sistema. Con ellas observamos la respuesta de la aplicación ante un determinado número de peticiones.

Aquí entraría por ejemplo ver cómo se comporta el sistema ante X usuarios que entran concurrentemente a la aplicación y realizan ciertas transacciones.

**Pruebas de estrés**
Este es otro tipo de prueba de rendimiento del sistema. El objetivo de estas pruebas es someter al software a situaciones extremas, intentar que el sistema se caiga, para ver cómo se comporta, si es capaz de recuperarse.

## 3º Que herramientas podemos usar para hacer pruebas de carga

Existen multitud de herramientas que nos facilitan la posibilidad de lanzar pruebas de carga, nos centraremos en Jmeter por ser la más extendida y por ser opensource. 

**Otras herramientas:**

- WebLOAD
- LoadNinja
- SmartMeter.io
- Tricentis Flood
- LoadView
- LoadUI NG Pro
- LoadRunner
- Appvance
- NeoLoad
- LoadComplete
- WAPT
- Loadster
- LoadImpact
- Rational Performance Tester
- Testing Anywhere

## 4º Plan de pruebas de Jmeter
Definimos de forma rápida los componentes de los que puede constar un plan de pruebas son:
- **Test Plan.** Representa la raíz del plan de pruebas.
- **Thread Group.** Representa un grupo de usuarios, cada thread es un usuario virtual.
- **Controllers** (Sampler, Logic Controler). 
	- **Samplers:** Realizan peticiones contra la aplicación 
	- **Logic Controlers:** Establecen el orden en que se ejecutan las peticiones.
	- **Config Element.** Establecen propiedades de configuración.
	- **Assertion.** Comprueban condiciones que aplican a las peticiones.
	- **Listeners.** Almacenan datos de las peticiones.
	- **Timer.** Añaden tiempo extra a la ejecución de las peticiones.
	- **Pre-Processor element.** Realizan acciones o establecen configuraciones previa a la ejecución de los samplers.
	- **Post-Processor element.** Realizan acciones o establecen configuraciones posteriormente a la ejecución de los samplers.

**Plugins de Jmeter:** El uso de los componentes indicados anteriormente son la base de jmeter, sin embargo existen algunos plugins que se pueden incorporar a jmeter para facilitar la configuración de las pruebas y obtener unos reportes que nos ayudarán a analizar los resultados más facilmente. En ésta formación haremos uso de los siguientes plugins: 

- **3 Basic Graphs:** [https://jmeter-plugins.org/wiki/ResponseTimesOverTime/](https://jmeter-plugins.org/wiki/ResponseTimesOverTime/) 
	- Average Response Time 
	- Active Threads 
	- Successful/Failed Transactions 

- **5 Additional Graphs :** [https://jmeter-plugins.org/wiki/ResponseCodesPerSecond/](https://jmeter-plugins.org/wiki/ResponseCodesPerSecond/) 
	- Response Codes 
	- Bytes Throughput 
	- Connect Times 
	- Latency 
	- Hits/s 


**Peticiones (Sampler):** Las peticiones que se van a simular en el portal se podrán grabar haciendo uso de un proxy con cualquier navegador o dar de alta en jmeter directamente, en nuestro ejemplo por no ser el objetivo de este documento se hará de forma manual la introducción de páginas a las que se hará las peticiones


**Plan de pruebas de ejemplo:** Abrir el fichero .jmx preparado para las pruebas: jmeter_files/01_test_lug_.jmx

Para la simulación de usuarios virtuales haremos uso de las opciones definidas a continuación que nos facilitan los plugins instalados: 
- jp@gc Throughput Shaping timer: Definiremos el número de peticiones que se realizaran por segundo (RPS)
- jp@gc Ultimate Thread Group: Se indicarán el número de hilos (usuarios)

Dichas gráficas deben ser semejantes y los hilos que se indiquen en la opción de thread group se deben calcular con la siguiente fórmula:
```markdown
RPS * <max response time> / 1000
```
Siendo la variable "max response time" lo que definamos como tiempo máximo de respuesta para dar como válida la petición, un valor válido podría ser 2.5 segundos (2500 ms) quedando la configuración:
```markdown
Hilos = 200  * 2500/1000  = 500 
```
El numero de peticiones e hilos (usuarios) con los que simularemos las pruebas serán 200 usuarios concurrentes con una rampa de subida y otra de bajada para hacerla progresiva quedando las gráficas de la siguiente forma:
```markdown
Hilos = 80 * 2500/1000  = 200
```

- jp@gc Throughput Shaping timer:
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/01.png)

- jp@gc Ultimate Thread Group:
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/02.png)
	

## 5º Lanzar pruebas y monitorizar comportamiento
Vamos a analizar el comportamiento el portal de liferay con 10 contenidos web, 5 con una estructura con plantilla no cacheable y otros 5 con una estructura con plantilla cacheable. Del mismo modo, se estudiará la respuesta de un portlet a medida según utilice cache o no.


**Pruebas previstas:**

- **Página con un visor de contenidos:**
	- Url: /01_test_lug_visor
	- Descripción: Al ser el visor por defecto con un contenido cacheable debería ser el resultado más optimo de las pruebas realizadas

- **Página con un publicador de contenidos con contenidos web con plantilla sin cachear:**
	- Url: /02_test_lug_publicador_no_cache
	- Descripción: Al no usar la caché de liferay tendrá que procesar la plantilla del contenido cada vez que realice su renderizado con el coste computacional correspondiente.
		
- **Página con un publicador de contenidos con contenidos web con plantilla cacheada:**
	- Url: /03_test_lug_publicador_cache
	- Descripción: En la primera carga el resultado será semejante a la página anterior pero en posteriores los tiempos de respuesta serán mejores al estar cacheada la plantilla.
	
- **Página con un portlet a medida sin hacer uso de cache:**
	- Url: /04_test_lug_custom_module_no_cache
	- Descripción: Al tener un portlet a medida que cada vez que realiza su renderizado realiza peticiones a API externa y posteriormente un tratamiento considerable de datos, se tendrán tiempos de respuestas muy altos cada vez que se acceda al mismo.

- **Página con un portlet a medida haciendo uso de cache:**
	- Url: /05_test_lug_custom_module_cache?cache=true
	- Descripción: En la primera carga el resultado será semejante a la página anterior pero en posteriores los tiempos de respuesta serán mejores al estar cacheada la llamada a la API y gran parte del tratamiento de datos del módulo a medida.


Antes de lanzar las pruebas deberíamos monitorizar el comportamiento de nuestra arquitectura con herramientas como jvisualvm, jmc ó jconsole. Puntos de interés dentro de las métricas que podemos observar:
- **Consumo de CPU, comportamiento de JVM, nº de hilos, etc…**
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/03.png)
				
- En la sección Mbeans podemos ver datos interesantes como son: 
	- **Ehcache** (Las caches disponibles de liferay y su comportamiento)
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/04.png)
	- vPool Hikari** (Pool de conexiones usado por defecto en Liferay)
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/05.png)

### Lanzamientos de Pruebas Elementos OOTB
#### Lanzamiento de Pruebas Elementos OOTB #1:
- **Página con un visor de contenidos:** /01_test_lug_visor 
- **Página con un visor de contenidos con contenido web con plantilla cacheada:** /02_test_lug_publicador_no_cache
- **Página con un visor de contenidos con contenido web con plantilla sin cachear:** /03_test_lug_publicador_cache
	
	
**Resultados:**

Se puede observar como solo han sido cacheados 5 elementos en memoria y la página que visualiza los elementos de la plantilla no cacheable tiene unos tiempos de respuesta peores que la cacheada (Verde vs Rojo), en este caso solo tenemos 10 elementos en total pero cuando un sistema empieza a crecer en varios miles de contenidos se puede apreciar esta diferencia de forma más notable. 

De la misma forma se observan picos constantes de latencias altas.
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/06.png)
	
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/07.png)

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/08.png)

	

#### Lanzamiento de Pruebas Elementos OOTB #2:
Se cambiará la configuración de la plantilla que no era cacheable, ahora se pondrá cacheable y se ejecutarán las mismas pruebas de carga de Pruebas Elementos OOTB #1 . 

**Resultados:**

Las paginas **/02_test_lug_publicador_no_cache** y **/03_test_lug_publicador_cache** ahora muestran resultados semejantes teniendo tiempos de latencia medios más bajos que los resultados anteriores, observandose que en solo 5 contenidos con plantillas no cacheadas empieza a degradarse la respuesta. 

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/09.png)

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/10.png)

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/11.png)



### Lanzamiento Pruebas Módulo Ad hoc: 
#### Lanzamiento de Pruebas Módulo Ad hoc #1:
- **Página con un portlet a medida sin hacer uso de cache:** /04_test_lug_custom_module_no_cache 
	
Para ofrecer una comparación entre los elementos OOTB y un módulo Ad hoc mantendremos las 3 pruebas realizadas sobre elementos OOTB de Liferay (**/01_test_lug_visor**, **/02_test_lug_publicador_no_cache** y **/03_test_lug_publicador_cache**).

**Resultados:**

Se puede observar que no se puede mantener el nivel de respuestas por segundo pretendido (80), sino que se produce un colapso debido al coste que conlleva cada una de las peticiones. Ya que al no cachearse los datos a mostrar, por cada petición se realiza una comunicación de red con una API externa, así como el tratamiento de los datos recibidos.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/12_sin_cache.png)

Debido a los altos tiempos de ejecución que implica una comunicación por red, al no utilizar caché se obtienen latencias altas, donde tras el colapso y acumulación de peticiones aumentan de forma drástica llegando a latencias de 9 segundos.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/13_sin_cache.png)

Si comparamos los tiempos de respuesta obtenidos en esta página con las 3 que contienen elementos OOTB, observamos que siempre se encuentra por encima. Incluso podemos observar como llegados al punto en el que empieza el colapso, no sólo empeora los tiempos de respuestas de la página con el módulo Ad hoc, sino que también degrada el rendimiento en las páginas restantes.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/14_sin_cache.png)


#### Lanzamiento de Pruebas Módulo Ad hoc #2:
- **Página con un portlet a medida haciendo uso de cache:** /05_test_lug_custom_module_cache?cache=true
	

**Resultados:**

Se puede observar cómo en este caso sí se mantiene el nivel de respuestas por segundo pretendido (80). Ya que al cachearse los datos a mostrar, el 100% del coste de la operación se realiza en la primera petición mientras que en las siguientes se reduce conciderablemente el coste computacional.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/12_con_cache.png)

En este caso, los tiempos de latencia son bajos debido al ahorro computacional que proporciona el uso de cache, lo cual previene el colapso y acumulación de peticiones. Cuantitativamente el mayor pico que tenemos con cache es de 155 milisegundos, cuando sin cache se tuvo un pico de 9 segundos de latencia.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/13_con_cache.png)

Al igual que ocurrió en el ejemplo anterior, los tiempos de respuesta del módulo Ad hoc siempre está por encima de los elementos OOTB, pero en este caso no se produce ningún colapso que degrade ninguna de las páginas.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/14_con_cache.png)

Fijándonos en la influencia sobre la máquina, observamos que en cuando se utiliza la cache en el módulo ad hoc (actividad de las 19:25) la CPU está entorno al 50% del rendimiento, con picos del 67%, y no se produce apenas un aumento del número de hilos. Mientras que sin el uso de la cache (actividad de las 19:30) la CPU sufre mayores porcentajes de uso, llegando a picos del 100% y casi se triplica el número de hilos.

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/15_sin_cache.png)

Por último, cabe mencionar el alto acierto generado en la cache usada para la prueba del módulo ad hoc con cache. Acertando en 1949 peticiones y fallando sólamente en 1 (la primera).

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/16.png)


(*) En el caso de tener una degradación en el portal sería conveniente realizar un análisis de hilos con un thread dumps (Ver:https://github.com/dmcisneros/lug_pruebas_carga/tree/master/thread_dumps)


## 6º Analizar resultados de las pruebas, realizar tunning y ajustar

Una vez obtenido un punto de referencia se recomienda hacer un tunning para optimizar y mejorar tiempos de respuesta, cada proyecto por la propia arquitectura, elementos de red, desarrollos realizados tendrán una configuración que se adapte mejor a sus necesidades no pudiendo recomendaciones estandarizadas siendo diferentes para cada caso, Liferay indica algunas recomendaciones básicas en su documento **Deployment Checklist** como serían:
- portal-ext.properties: 
	- com.liferay.portal.servlet.filters.* (Desactivar Servlet filters no utilizados)
	- session.tracker.memory.enabled=false (Deshabilitar session tracket si está activo)
	- portlet.css.enabled=false (Ajustar la propiedad si no se va a utilizar personalización de apariencia desde portlets)
	- locales.enabled= (Deshabilitar los que no se vayan a utilizar)
	- dl.store.impl=com.liferay.portal.store.file.system.AdvancedFileSystemStore (Recomendada)
	- direct.servlet.context.reload=false (En producción evitar la recarga de jsp en cada petición)
	- counter.increment=2000 (Ajustar los indices de contadores)
	- buffered.increment.standby.queue.threshold=60
	- buffered.increment.standby.time.upper.limit=10000
	- dl.file.rank.enabled=false
	- dl.file.rank.check.interval=-1
	- blogs.ping.google.enabled=false
	- blogs.pingback.enabled=false
	- blogs.trackback.enabled=false
	- message.boards.pingback.enabled=false
	- live.users.enabled=false
	- session.tracker.memory.enabled=false
	- session.tracker.persistence.enabled=false
	- session.tracker.friendly.paths.enabled=false
- Ajustar Session timeout
- Ajustar server.xml con recomendaciones del fichero Deployment Checklist
- Ajustar ADT caché: En system settings ajustar la propiedad "resource modification check interval", por defecto es 60ms
- Tunning de JDK, GC, etc...
- Añadir elementos frontales para tratamiento de estáticos como podrian ser varnish, nginx, etc...
- Large Cluster pages en SO
- Y más allá....

# Resumen: 
Como desarrolladores debemos asegurarnos principalmente de que nuestro sistema funcionalmente sea lo que quiere el usuario final pero es igualmente importante asegurar la estabilidad y respuesta de nuestra arquitectura optimizando tiempos de respuesta, plan de contingencia ante caídas, asegurar la alta disponibilidad, etc…

**Referencias:**
	
- [https://sdos.es/blog/pruebas-de-rendimiento-con-jmeter-ejemplos-basicos](https://sdos.es/blog/pruebas-de-rendimiento-con-jmeter-ejemplos-basicos)
- [https://www.softwaretestinghelp.com/performance-testing-tools-load-testing-tools/](https://www.softwaretestinghelp.com/performance-testing-tools-load-testing-tools/)
- [https://jmeter-plugins.org/wiki/ThroughputShapingTimer/](https://jmeter-plugins.org/wiki/ThroughputShapingTimer/)

