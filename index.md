# Pruebas de Rendimiento (Sobre Liferay)

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

- 3 Basic Graphs: https://jmeter-plugins.org/wiki/ResponseTimesOverTime/
	- Average Response Time 
	- Active Threads 
	- Successful/Failed Transactions 

- 5 Additional Graphs :https://jmeter-plugins.org/wiki/ResponseCodesPerSecond/
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
Vamos a analizar el comportamiento el portal de liferay con 10 contenidos web 5 con una estructura con plantilla no cacheable y otros 5 con una estructura con plantilla cacheable.


**Pruebas previstas:**

Descripción	Url	Resultado esperado
Página con un visor de contenidos: 	/01_test_lug_visor 	Al ser el visor por defecto con un contenido cacheable debería ser el resultado más optimo de las pruebas realizadas
Página con un publicador de contenidos con contenidos web con plantilla sin cachear	/02_test_lug_publicador_no_cache	Al no usar la caché de liferay tendrá que procesar la plantilla del contenido cada vez que realice su renderizado con el coste computacional correspondiente
Página con un publicador de contenidos con contenidos web con plantilla cacheada	/03_test_lug_publicador_cache	En la primera carga el resultado será semejante a la página anterior pero en posteriores los tiempos de respuesta serán mejores al estar cacheada la plantilla
Página con un módulo a medida sin caché	/04_test_lug_custom_module_no_cache	Un módulo hará lógica de negocio cada vez que se realice una petición sobre la página donde se ubica.
Página con un módulo a medida con caché	/05_test_lug_custom_module_cache	El mismo módulo anterior hará uso de la caché de liferay guardando objetos que no cambien su estado y puedan ser cacheados.


Antes de lanzar las pruebas deberíamos monitorizar el comportamiento de nuestra arquitectura con herramientas como jvisualvm, jmc ó jconsole. Puntos de interés dentro de las métricas que podemos observar:
- **Consumo de CPU, comportamiento de JVM, nº de hilos, etc…**
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/03.png)
				
- En la sección Mbeans podemos ver datos interesantes como son: 
	- **Ehcache** (Las caches disponibles de liferay y su comportamiento)
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/04.png)
	- vPool Hikari** (Pool de conexiones usado por defecto en Liferay)
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/05.png)

### Lanzamientos de Pruebas #1
- Página con un visor de contenidos: /01_test_lug_visor 
- Página con un visor de contenidos con contenido web con plantilla cacheada /02_test_lug_publicador_no_cache
- Página con un visor de contenidos con contenido web con plantilla sin cachear	/03_test_lug_publicador_cache
	
	
**Resultados:**

Se puede observar como solo han sido cacheados 5 elementos en memoria y la página que visualiza los elementos de la plantilla no cacheable tiene unos tiempos de respuesta peores que la cacheada (Verde vs Rojo), en este caso solo tenemos 10 elementos en total pero cuando un sistema empieza a crecer en varios miles de contenidos se puede apreciar esta diferencia de forma más notable. 

De la misma forma se observan picos constantes de latencias altas.
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/06.png)
	
![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/07.png)

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/08.png)

	

### Lanzamiento Pruebas #2: 
Se cambiará la configuración de la plantilla que no era cacheable, ahora se pondrá cacheable y se ejecutarán las mismas pruebas de carga de Pruebas #1 . 

**Resultados:**
Las paginas **/02_test_lug_publicador_no_cache** y **/03_test_lug_publicador_cache** ahora muestran resultados semejantes teniendo tiempos de latencia medios más bajos que los resultados anteriores, observandose que en solo 5 contenidos con plantillas no cacheadas empieza a degradarse la respuesta. 

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/09.png)

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/10.png)

![Image](https://raw.githubusercontent.com/dmcisneros/lug_pruebas_carga/master/images/11.png)




(*) En el caso de tener una degradación en el portal sería conveniente realizar un análisis de hilos con un thread dumps (Ver:https://github.com/dmcisneros/lug_pruebas_carga/tree/master/thread_dumps)


## 6º Analizar resultados de las pruebas realizar tunning y ajustar

Una vez obtenido un punto de referencia se recomienda hacer un tunning para optimizar y mejorar tiempos de respuesta, para ello Liferay  indica algunas recomendaciones como serían:
- portal-ext.properties: 
	- com.liferay.portal.servlet.filters.* (Desactivar Servlet filters no utilizados)
	- session.tracker.memory.enabled=false (Deshabilitar session tracket si está activo)
	- portlet.css.enabled=false (Ajustar la propiedad si no se va a utilizar)
	- locales.enabled= (Deshabilitar los que no se vayan a utilizar)
	- dl.store.impl=com.liferay.portal.store.file.system.AdvancedFileSystemStore (Recomendada)
	- direct.servlet.context.reload=false (En producción evitar la recarga de jsp en cada petición)
- Ajustar Session timeout
- Ajustar ADT caché: En system settings ajustar la propiedad "resource modification check interval", por defecto es 60ms

#Resumen: 
Como desarrolladores debemos asegurarnos principalmente de que nuestro sistema funcionalmente sea lo que quiere el usuario final pero es igualmente importante asegurar la estabilidad y respuesta de nuestra arquitectura optimizando tiempos de respuesta, plan de contingencia ante caídas, asegurar la alta disponibilidad, etc…

**Referencias:**
	
	- [https://sdos.es/blog/pruebas-de-rendimiento-con-jmeter-ejemplos-basicos](https://sdos.es/blog/pruebas-de-rendimiento-con-jmeter-ejemplos-basicos)
	- [https://www.softwaretestinghelp.com/performance-testing-tools-load-testing-tools/](https://www.softwaretestinghelp.com/performance-testing-tools-load-testing-tools/)
	- [https://jmeter-plugins.org/wiki/ThroughputShapingTimer/](https://jmeter-plugins.org/wiki/ThroughputShapingTimer/)













