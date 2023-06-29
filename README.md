FinderApp

Descripción general: El objetivo de esta aplicación es realizar búsquedas de lugares de acuerdo al texto que se ingresa, por ejemplo, si en el buscador se escribe “Pizza” se mostrara una lista de negocios relacionados a pizzas. En esta lista se mostrará información sobre el lugar: Nombre, categoría, dirección, valoración y la distancia aproximada. Al hacer click en algún item se navegará a otra pantalla en el cual encontrara información mas detallada como: fotografías, numero y comentarios de personas que han visitado el lugar, así como también un icono (pin) de color rojo que al hacer click navegara a otra pantalla abriendo un mapa con la ruta desde la posición hasta el lugar mostrando la distancia y tiempo aproximada.

Para abordar este proyecto primero se hizo el registro en la página de yelp con la finalidad de obtener el api key que servirían como método de autenticación para acceder a la información de los lugares, en base a esto se hizo un análisis para determinar el flujo y la información que tendría la aplicación.

Para habilitar el mapa en la aplicación fue necesario crear una cuenta en Google cloud plataform, registrar el proyecto, habilitar el sdk de maps y así obtener las credenciales y permisos necesarios para el funcionamiento del mapa.

Para el trazado de ruta, estimación de tiempo y distancia se utilizó la plataforma de openroute service, y para ello se hizo el registro del proyecto en su pagina con el fin de obtener el api key que nos serviría como método de autentificación para acceder a la información.

Por ultimo se hizo las configuraciones necesarias para firmar y generar el archivo reléase del proyecto, se procedió a realizar el registro en la tienda de Google play y subir los archivos necesarios para mandar a revisión el proyecto. Al pasar unos días la app paso a estar disponible en la tienda.

Tecnologias y herramientas utilizadas:
#Android Studio #Kotlin #Mvvm #Navigation component #Retrofit and interceptors #Dagger hilt #Coil #Room #Coroutines, #Google maps #Postman, #View Binding

Links:

OpenRoute service: https://openrouteservice.org/

Yelp fusion: https://fusion.yelp.com/

Google cloud platform: https://cloud.google.com/?hl=es

Google play console: https://play.google.com/console/about/

Capturas:

<img width="197" alt="Imagen2" src="https://github.com/carlosLoeza12/FinderApp/assets/68243731/a5fb3fb9-64ef-40e7-bbfb-bbb3593e235b">

<img width="194" alt="Imagen3" src="https://github.com/carlosLoeza12/FinderApp/assets/68243731/780d40b0-228d-4f2a-88d2-e8957e30743b">

<img width="195" alt="Imagen4" src="https://github.com/carlosLoeza12/FinderApp/assets/68243731/c50049b1-04db-4c7d-b887-0962b356e774">

<img width="195" alt="Imagen5" src="https://github.com/carlosLoeza12/FinderApp/assets/68243731/366d09c7-c40a-41b6-8a81-448b82bef513">



