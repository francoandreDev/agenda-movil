# Sintaxis

Esto sirve para ayudarte a comprender mejor el proyecto.

## Sintaxis más básica

`val` es para variables inmutables - que no cambian.
`var` es para variables mutables - pueden cambiar.

Se puede realizar encapsulamiento mediante `private`, `protected` o `internal`.
`public` y `final` o `closed` se aplican por defecto, (no se escriben).
`open`: Permite que la clase pueda ser heredada.
`Internal`: Hace que la clase solo sea visible dentro del módulo.

Los `data class` son usados para establecer una clase con solo atributos, para un obj con atributos.

`fun`: Puede ser usada tanto para funciones como para métodos.

`@`: Annotation class, sirve para adjuntar metadatatos a clases, funciones, propiedades, parámetros 
o expresiones. Esto para modificar o añadir comportamientos, validaciones o generar código.


## Tipos de Constructores

1. Constructor primario - class Clase(var x: Int, var y: Int) {} - Inicializa variables a la vez
que se crea el obj. No tiene cuerpo.
2. Constructor secundario - constructor(var x: Int, var y: Int): this(x,y) {//} - Permiten tener 
varias formas de inicializar una clase de forma alternativa. deben llamar al constructor primario, 
si existe, llamando con this.
3. Init - init {//} - Para lógica posterior a la creación del primer constructor es como si fuera
el cuerpo del primer constructor.

Ejemplo:

```Kotlin
class User(name: String, age: Int) {
    // Bloque init para lógica adicional
    init {
        require(age >= 0) { "Age cannot be negative" }
        println("User initialized with name: $name, age: $age")
    }

    // Constructor secundario
    constructor(name: String) : this(name, 0) {
        println("Secondary constructor: age set to 0")
    }
}

fun main() {
    val user1 = User("Alice", 25)  // Llama al constructor primario y ejecuta init
    val user2 =
        User("Bob")        // Llama al constructor secundario, luego al primario, y ejecuta init
}
```

## El tipado de datos:

Puede ser implícito o por buenas prácticas puede ser explícito bajo la sintaxis: `:` en PascalCase.
`var miVariable: String = ""`.

Aprende sobre: [Tipos de datos Vacios](https://stackoverflow.com/questions/55953052/kotlin-void-vs-unit-vs-nothing)

## Companion Object
Similar al tipo estático en Java solo que con esteroides, se engloba todos los estáticos.
Puede tener un inicializador (init), el companion object puede implementar una interfaz.
Puede ser encapsulada de manera diferente a la clase.

- Kotlin
```Kotlin
class MyClass {
    private companion object: Interface {
        // private visibility for the companion object
        override fun staticMethod() {
            // private method implementation for companion object.
        }
    }
}
```

- Java
```Java
public class MyClass {
    private static void myStaticMethod() {
        // private visibility for the static method
    }
}
```

En kotlin mientras que un object se asocia a una instancia de un objeto relacionada a una clase,
un companion object sería más cercano al objeto relacionándose directamente a la clase.

Más referencias en:
[Companion Objects](https://medium.com/@mobiledev4you/companion-object-in-kotlin-c3a1203cd63c)

## Operador de referencia (::)
Se usa para referirse a funciones, métodos, propiedades o constructores. Referirse, no usarse.
Ejemplo:

```Kotlin
class Persona(var nombre: String)
fun main() {
val persona = Persona("Kotlin")
val referenciaNombre = persona::nombre // Referencia a la propiedad 'nombre'
println(referenciaNombre.get()) // Obtener el valor: Kotlin
referenciaNombre.set("Nuevo Nombre") // Establecer un nuevo valor
println(referenciaNombre.get()) // Nuevo Nombre
}
```

### Diferencias entre acceder de forma normal o por referencia

#### Acceso Directo (persona.nombre):
- Usas el valor inmediatamente cuando lo accedes.
- Modificas directamente.
- Es simple y adecuado para la mayoría de los casos en los que no necesitas manipulación dinámica.

#### Acceso por Referencia (persona::nombre):
- No accedes directamente al valor, sino que obtienes una referencia a la propiedad (un objeto 
KProperty o KMutableProperty).
- Esto es útil cuando necesitas pasar la propiedad a otra función, trabajar con reflexiones, o 
manejar propiedades dinámicamente sin saber de antemano cuál propiedad vas a modificar o acceder.

## Operador Elvis (?:)
Si la expresión a la izquierda tiene valor de null va a devolver el valor de la derecha

### Usando el operador Elvis

```Kotlin
fun obtenerNombreUsuario(usuario: String?): String {
    // Usando el operador Elvis para proporcionar un valor por defecto
    return usuario ?: "Invitado"
}

fun main() {
    val nombre1 = "Carlos"
    val nombre2: String? = null

    println(obtenerNombreUsuario(nombre1)) // Imprime "Carlos"
    println(obtenerNombreUsuario(nombre2)) // Imprime "Invitado"
}
```

### Sin usar el operador Elvis

```kotlin
fun obtenerNombreUsuarioAlternativa(usuario: String?): String {
    // Usando una estructura condicional if para proporcionar un valor por defecto
    return if (usuario != null) {
        usuario
    } else {
        "Invitado"
    }
}

fun main() {
    val nombre1 = "Carlos"
    val nombre2: String? = null

    println(obtenerNombreUsuarioAlternativa(nombre1)) // Imprime "Carlos"
    println(obtenerNombreUsuarioAlternativa(nombre2)) // Imprime "Invitado"
}
```

### Expresiones lambda en Colecciones
Al igual que otros lenguajes Kotlin tiene colecciones pero, en kotlin podemos usarlas con una 
expresión lambda:

#### Con Sintaxis Lambda
```kotlin
fun main() {
    val miColeccion = listOf("hola", "chau")
    // it es un nombre por convención que representa al item de la colección
    val enMayusculas = miColeccion.map { it.upperCase() }
    println(enMayusculas) // Salida: [HOLA, CHAU]
}
```

#### Sin Sintaxis Lambda
```kotlin
fun main() {
    val miColeccion = listOf("hola", "chau")
    val enMayusculas = miColeccion.map(::convertirAMayusculas)
    println(enMayusculas) // Salida: [HOLA, CHAU]
}

fun convertirAMayusculas(texto: String): String {
    return texto.upperCase()
}
```