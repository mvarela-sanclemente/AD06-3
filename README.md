# 6.3 MongoDB básico en JAVA

Neste repositorio podedes atopar un tutorial moi básico de como utilizar MongoDB en JAVA. Tedes máis información no seguinte enlace: https://mongodb.github.io/mongo-java-driver/

## Driver conector Mongo

Podemos baixar o driver de MongoDB para JAVA utilizando Maven:

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.12.2</version>
</dependency>
```

## Conexión coa base de datos.

Podemonos conectar a un sistema xestor base de datos MongoDB do seguinte xeito:

```java
String host = new String("192.168.56.102");
String port = new String("27017");
MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://"+host+":"+port));
```

## Selección base de datos

Co seguinte código podemos seleccionar a base de datos Mongo que queremos utilizar. A base de datos debemos creala dende un cliente como Robo3T, pero tamén hai xeitos de creala a través de código JAVA.

Neste caso seleccionamos a base de datos de nome "test".

```java
String dbName = new String("test");
DB database = mongoClient.getDB(dbName);
```

## Selecció dunha colección

Co seguinte código podemos seleccionar a colección que queremos utilizar da base de datos que temos. A colección debemos creala dende un cliente como Robo3T, pero tamén hai xeitos de creala a través de código JAVA.

Neste caso seleccionamos a colección de nome "alumno".

```java
DBCollection colAlumno= database.getCollection("alumno");
```

## Inserción dun documento

Como vimos na teoría, os documentos en MongoDB son JSON. Por exemplo vamos gardar o seguinte JSON na colección "alumnos":

```json
{
	"nome" : "Manuel Varela",
	"direccion" : {
	    "rua": "San clemente",
	    "numero": 45
	},
	"modulos" : [ "Acceso a datos", "Programacion"]
}
```

No seguinte código JAVA engadimos o JSON anterior. Como vemos utilizamos o método **insert()** da colección. Tedes todos os métodos da clase DBCollection no seguinte enlace: https://mongodb.github.io/mongo-java-driver/3.12/javadoc/com/mongodb/DBCollection.html

```java
List<String> modulos = Arrays.asList("Acceso a datos", "Programacion");
DBObject alumno = new BasicDBObject()
        .append("nome", "Manuel Varela")
        .append("direccion", new BasicDBObject()
                .append("rua", "San Clemente")
                .append("numero", 45))
        .append("modulos", modulos);

colAlumno.insert(alumno);
```

## Inserción de varios documentos

Non temos porque ir engadindo documentos un a un. É moi inificiente. Para engadir varios documentos nunha única chamada á base de datos tan só hai que crear un array e engadir todos os documentos que queremos inserir nese array.

```java
DBObject alumno2 = new BasicDBObject()
        .append("nome", "Manuel Varela 2")
        .append("direccion", new BasicDBObject()
                .append("rua", "San Clemente 2")
                .append("numero", 46))
        .append("modulos", Arrays.asList("Acceso a datos"));

DBObject alumno3 = new BasicDBObject()
        .append("nome", "Manuel Varela 3")
        .append("direccion", new BasicDBObject()
                .append("rua", "San Clemente 3")
                .append("numero", 47))
        .append("modulos", Arrays.asList("Programacion"));

colAlumno.insert(Arrays.asList(alumno2,alumno3));
```
## Consultas

O método básico para facer consultas é **find()** da clase DBCollection que xa antes referenciamos cun enlace.

As buscas en MongoDB son obxectos JSON. Por exemplo se queremos buscar todos os documentos que teñan como nome "Manuel Varela" creamos o seguinte JSON apra a consulta.

```json
{
	"nome":"Manuel Varela"
}
```

Polo tanto tan só temos que construír ese obxecto JSON que se lle pasará como parámetro ao método find. Neste caso devóvenos un cursos que podemos ir iterando cun "while". Por último cerramos o cursor. 

```java
DBObject query = new BasicDBObject("nome", "Manuel Varela");
DBCursor cursor = colAlumno.find(query);
while (cursor.hasNext()){
    DBObject documento = cursor.next();
    System.out.println(documento.toString());
}
cursor.close();
```
Se só queremos que se nos devolva un único obxecto podemos utilizar o método **findOne()**.

```java
System.out.println("O primeiro documento con Manuel Varela: ");
DBObject documento  = colAlumno.findOne(query);
System.out.println(documento.toString());
```

### Utilización de cualificadores

A API de JAVA para MongoDB proprocionanos un xeito máis sinxelo de construir consultas. Para iso ofrecenos varios "builders". Neste caso temos o builder **Filters** que se nos axuda a construír consultas: https://mongodb.github.io/mongo-java-driver/3.12/builders/filters/ . No enlace anterior tedes todos os filtros que se poden utilizar. Entre os máis utilizados:

- eq : coincidencia do valor dun atributo
- and
- or
- not
- lt : menor
- lte : menor ou igual
- gt: maior
- gte: maior ou igual
- regex: para expresións regulares 

No seguinte código temos un exemplo de utilización de **Filters** para consultar aqueles alumnos que teñen de nome "Manuel Varela" e "Manuel Varela 2".

```java
Bson filter = Filters.or(Filters.eq("nome","Manuel Varela"),Filters.eq("nome","Manuel Varela 2"));
DBObject query2 = new BasicDBObject(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
System.out.println(query2.toString());
DBCursor cursor2  = colAlumno.find(query2);
while (cursor2.hasNext()){
    DBObject documentoAux = cursor2.next();
    System.out.println(documentoAux.toString());
}
cursor2.close();
```
**Explicación:** Creamos primeiro un filtro. Utilizamos un filtro "or" para coller os documentos que coindidan cos dous nomes. Utilizamos o filtro "eq" para a coincidencia de nomes. A continuación creamos a consulta. Imprimímola só para ver como sería o JSON da consulta. Despois facemos a consulta tal e como vimos anteriormente.

### Consultas sobre subdocumentos

Realizar consultas sobre subdocumentos é moi sinxelo. Tan só temos que separalos campos por un punto como vemos no exemplo. No seguinte exemplo buscamos todos os alumnos que teñen como número da súa dirección un número maior ou igual a 46.

```java
Bson filter2 = Filters.gte("direccion.numero",46);
DBObject query3 = new BasicDBObject(filter2.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
System.out.println(query3.toString());
DBCursor cursor3  = colAlumno.find(query3);
while (cursor3.hasNext()){
    DBObject documentoAux = cursor3.next();
    System.out.println(documentoAux.toString());
}
cursor3.close();
```

### Consultas sobre arrays

Existen moitos tipos de consultas sobre arrays. Pero unha moi doada e moi interesante é facer unha consulta que devolva todos os documentos que teñan un determinado elemento nun array. Neste caso vamos consultar todos os alumnos que están matriculados no módulo de "Acceso a datos".

```java
Bson filter3 = Filters.eq("modulos","Acceso a datos");
DBObject query4 = new BasicDBObject(filter3.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
System.out.println(query4.toString());
DBCursor cursor4  = colAlumno.find(query4);
while (cursor4.hasNext()){
    DBObject documentoAux = cursor4.next();
    System.out.println(documentoAux.toString());
}
cursor4.close();
```

## Outras opcións de consulta

Sobre as consultas podemos facer algunhas accións interesantes máis que filtrar só que documentos devolve. Por exemplo ordear por un campo, limitar o número de documentos, ... Nos seguintes puntos imos ver estas posibilidades.

Vamos usar o seguinte método para as consultas:

```java
find(DBObject query, DBCollectionFindOptions options)
```

A clase **DBCollectionFindOptions** é a que nos permite usar estas opcións. Temos a documentación no seguinte enlace: https://mongodb.github.io/mongo-java-driver/3.12/javadoc/com/mongodb/client/model/DBCollectionFindOptions.html#projection(com.mongodb.DBObject)

### Proxeccións

Vamos comenzar por realizar proxeccions sobre os documentos. Neste caso só queremos que se nos devolvan os atributos "nome" e "modulos" de todos os documentos. Para iso vamos a utilizar o builder **Projections**.

As posibles opcións deste builder témolas no seguinte enlace: https://mongodb.github.io/mongo-java-driver/3.12/builders/projections/

Neste exemplo imos recuperar todos os documentos da colección "alumnos" (para iso o JSON da conulta ten que estar vacío) pero só cos atributos "nome" e "modulos". Para iso utilizamos o método "include" do Builder "Projections".

```java
DBCollectionFindOptions options1 = new DBCollectionFindOptions();
Bson projectionAux = Projections.include(Arrays.asList("nome","modulos"));
DBObject projection1 = new BasicDBObject(projectionAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
options1.projection(projection1);
DBCursor cursor5  = colAlumno.find(new BasicDBObject(),options1);
while (cursor5.hasNext()){
    DBObject documentoAux = cursor5.next();
    System.out.println(documentoAux.toString());
}
cursor5.close();
```

### Limit e skip

Estes dous parámetros son moi interesantes para paxinar. 
- Limit: é o número de documentos que se nos devolven.
- Skip: é o número de documentos que se saltan.

Por exemplo, imaxinemos que queremos mostrar os alumnso de 5 en 5. O limit e 5. Para a primeira páxina non necesitamos saltarnos ningún dos documentos. Pero para a segunda necesitamos omitir os 5 primeiros documentos que se obteñen. Entón para a segunda páxina poñemos como skip 5. Así os 5 primeiros documentos omiteos e collemos os 5 seguintes.

No noso exemplo imos utilizar a consulta do apartado anterior. Neste caso imos saltarnos un documento e imos collelos dous seguintes.

```java
DBCollectionFindOptions options2 = new DBCollectionFindOptions();
Bson projectionAux2 = Projections.include(Arrays.asList("nome","modulos"));
DBObject projection2 = new BasicDBObject(projectionAux2.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
options2.projection(projection2);
options2.limit(2);
options2.skip(1);
DBCursor cursor6  = colAlumno.find(new BasicDBObject(),options2);
while (cursor6.hasNext()){
    DBObject documentoAux = cursor6.next();
    System.out.println(documentoAux.toString());
}
cursor6.close();
```

### Ordenar

Do mesmo xeito que estamos facendo nos apartados anteriores, para ordenar imos utilizar un Builder. Neste caso o builder **Sorts**: https://mongodb.github.io/mongo-java-driver/3.12/builders/sorts/

Neste exemplo imos ordenar por nome descendentemente.

```java
DBCollectionFindOptions options3 = new DBCollectionFindOptions();
Bson projectionAux3 = Projections.include(Arrays.asList("nome","modulos"));
DBObject projection3 = new BasicDBObject(projectionAux2.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
options3.projection(projection3);
options3.limit(2);
options3.skip(1);
Bson sortAux = Sorts.descending("nome");
DBObject sort = new BasicDBObject(sortAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
options3.sort(sort);
DBCursor cursor7  = colAlumno.find(new BasicDBObject(),options3);
while (cursor7.hasNext()){
    DBObject documentoAux = cursor7.next();
    System.out.println(documentoAux.toString());
}
cursor7.close();
```

## Actualizacións

Para realizar actualizacións temos o seguinte método:

```java
WriteResult	update​(DBObject query, DBObject update)
```

Polo tanto o primeiro argumento é unha consulta. Todos aqueles documentos que satisfagan esa consulta seran modificados segundo o segundo parámetro. As consultas poden facerse como vimos nos pasos anteriores

Para construir o segundo parámetro tamén podemos facer uso de builders. Neste caso do builder **Updates** : https://mongodb.github.io/mongo-java-driver/3.12/builders/updates/.

Neste exemplo imos modificar o nome de "Manuel Varela 2" a "Manuel Varela Lopez".

```java
Bson filterUp = Filters.eq("nome","Manuel Varela 2");
DBObject queryUp = new BasicDBObject(filterUp.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
Bson updateAux = Updates.set("nome","Manuel Varela Lopez");
DBObject update = new BasicDBObject(updateAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
colAlumno.update(queryUp,update);
DBCursor cursor8 = colAlumno.find(new BasicDBObject());
while (cursor8.hasNext()){
    DBObject documentoAux = cursor8.next();
    System.out.println(documentoAux.toString());
}
cursor8.close();
```

## Eliminación de documentos

Para elimiar documentos utilizamos o seguinte método:

```java
public WriteResult remove​(DBObject query)
```

Este método elimina todos os documentos que coincidan coa consulta. Estas poden facerse tal e como vimos nos apartados anteriores.

No noso caso imos borrar todos os documentos para deixar a colección limpia.

```java
colAlumno.remove(new BasicDBObject());
```
